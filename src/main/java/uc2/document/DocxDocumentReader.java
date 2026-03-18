package uc2.document;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import uc2.util.TextUtilities;

public final class DocxDocumentReader implements DocumentReader {
    @Override
    public boolean supports(Path path) {
        return path.getFileName().toString().toLowerCase().endsWith(".docx");
    }

    @Override
    public String read(Path path) throws IOException {
        try (ZipFile zipFile = new ZipFile(path.toFile())) {
            ZipEntry entry = zipFile.getEntry("word/document.xml");
            if (entry == null) {
                throw new IOException("DOCX file does not contain word/document.xml");
            }

            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                String xml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                String text = xml
                    .replace("</w:p>", System.lineSeparator())
                    .replace("</w:tr>", System.lineSeparator())
                    .replaceAll("<[^>]+>", " ");
                return TextUtilities.unescapeXml(text).replaceAll("\\s+", " ").trim();
            }
        }
    }
}
