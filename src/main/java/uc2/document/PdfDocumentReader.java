package uc2.document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.InflaterInputStream;

public final class PdfDocumentReader implements DocumentReader {
    @Override
    public boolean supports(Path path) {
        return path.getFileName().toString().toLowerCase().endsWith(".pdf");
    }

    @Override
    public String read(Path path) throws IOException {
        String viaGhostscript = readWithGhostscript(path);
        if (!viaGhostscript.isBlank()) {
            return viaGhostscript;
        }
        return readWithFallbackParser(path);
    }

    private String readWithGhostscript(Path path) throws IOException {
        Process process = null;
        try {
            process = new ProcessBuilder("gs", "-q", "-sDEVICE=txtwrite", "-o", "-", path.toString())
                .redirectErrorStream(true)
                .start();
            try (InputStream stream = process.getInputStream()) {
                String text = new String(stream.readAllBytes(), StandardCharsets.UTF_8).trim();
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    return text;
                }
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException("PDF extraction was interrupted.", exception);
        } catch (IOException exception) {
            return "";
        } finally {
            if (process != null) {
                process.destroyForcibly();
            }
        }
        return "";
    }

    private String readWithFallbackParser(Path path) throws IOException {
        byte[] pdfBytes = Files.readAllBytes(path);
        String rawPdf = new String(pdfBytes, StandardCharsets.ISO_8859_1);
        StringBuilder builder = new StringBuilder();
        int searchStart = 0;

        while (true) {
            int streamIndex = rawPdf.indexOf("stream", searchStart);
            if (streamIndex < 0) {
                break;
            }
            int dataStart = streamIndex + "stream".length();
            if (dataStart < pdfBytes.length && pdfBytes[dataStart] == '\r') {
                dataStart++;
            }
            if (dataStart < pdfBytes.length && pdfBytes[dataStart] == '\n') {
                dataStart++;
            }

            int endStreamIndex = rawPdf.indexOf("endstream", dataStart);
            if (endStreamIndex < 0) {
                break;
            }

            String dictionary = rawPdf.substring(Math.max(0, streamIndex - 400), streamIndex);
            byte[] streamBytes = slice(pdfBytes, dataStart, endStreamIndex);
            byte[] decoded = dictionary.contains("/FlateDecode") ? inflate(streamBytes) : streamBytes;
            builder.append(extractTextOperators(new String(decoded, StandardCharsets.ISO_8859_1))).append(System.lineSeparator());
            searchStart = endStreamIndex + "endstream".length();
        }

        return builder.toString().replaceAll("\\s+", " ").trim();
    }

    private byte[] slice(byte[] source, int startInclusive, int endExclusive) {
        int length = Math.max(0, endExclusive - startInclusive);
        byte[] result = new byte[length];
        System.arraycopy(source, startInclusive, result, 0, length);
        return result;
    }

    private byte[] inflate(byte[] streamBytes) throws IOException {
        try (InflaterInputStream inflater = new InflaterInputStream(new ByteArrayInputStream(streamBytes));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            inflater.transferTo(outputStream);
            return outputStream.toByteArray();
        } catch (IOException exception) {
            return streamBytes;
        }
    }

    private String extractTextOperators(String contentStream) {
        StringBuilder text = new StringBuilder();
        Matcher textBlockMatcher = Pattern.compile("(?s)BT(.*?)ET").matcher(contentStream);
        while (textBlockMatcher.find()) {
            String block = textBlockMatcher.group(1);
            Matcher showTextMatcher = Pattern.compile(
                "(\\[(?:.|\\R)*?\\]\\s*TJ)|(\\((?:\\\\.|[^\\\\)])*\\)\\s*Tj)|(\\((?:\\\\.|[^\\\\)])*\\)\\s*[\"'])"
            ).matcher(block);
            while (showTextMatcher.find()) {
                String token = showTextMatcher.group();
                if (token.startsWith("[")) {
                    text.append(extractArrayStrings(token));
                } else {
                    int start = token.indexOf('(');
                    int end = token.lastIndexOf(')');
                    if (start >= 0 && end > start) {
                        text.append(decodeLiteral(token.substring(start + 1, end)));
                    }
                }
                text.append(' ');
            }
        }
        return text.toString();
    }

    private String extractArrayStrings(String arrayToken) {
        List<String> fragments = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\((?:\\\\.|[^\\\\)])*\\)|<[0-9A-Fa-f]+>").matcher(arrayToken);
        while (matcher.find()) {
            String token = matcher.group();
            if (token.startsWith("(")) {
                fragments.add(decodeLiteral(token.substring(1, token.length() - 1)));
            } else {
                fragments.add(decodeHexString(token.substring(1, token.length() - 1)));
            }
        }
        return String.join("", fragments);
    }

    private String decodeHexString(String hex) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        for (int index = 0; index + 1 < hex.length(); index += 2) {
            bytes.write(Integer.parseInt(hex.substring(index, index + 2), 16));
        }
        byte[] data = bytes.toByteArray();
        if (data.length >= 2 && data[0] == (byte) 0xFE && data[1] == (byte) 0xFF) {
            return new String(data, StandardCharsets.UTF_16BE);
        }
        return new String(data, StandardCharsets.ISO_8859_1);
    }

    private String decodeLiteral(String literal) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < literal.length(); index++) {
            char current = literal.charAt(index);
            if (current != '\\') {
                builder.append(current);
                continue;
            }
            if (index + 1 >= literal.length()) {
                break;
            }
            char escaped = literal.charAt(++index);
            switch (escaped) {
                case 'n' -> builder.append('\n');
                case 'r' -> builder.append('\r');
                case 't' -> builder.append('\t');
                case 'b' -> builder.append('\b');
                case 'f' -> builder.append('\f');
                case '(' -> builder.append('(');
                case ')' -> builder.append(')');
                case '\\' -> builder.append('\\');
                default -> {
                    if (Character.isDigit(escaped)) {
                        StringBuilder octal = new StringBuilder().append(escaped);
                        for (int count = 0; count < 2 && index + 1 < literal.length()
                            && Character.isDigit(literal.charAt(index + 1)); count++) {
                            octal.append(literal.charAt(++index));
                        }
                        builder.append((char) Integer.parseInt(octal.toString(), 8));
                    } else {
                        builder.append(escaped);
                    }
                }
            }
        }
        return builder.toString();
    }
}
