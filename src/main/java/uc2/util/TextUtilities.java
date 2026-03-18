package uc2.util;

import java.util.Locale;

public final class TextUtilities {
    private TextUtilities() {
    }

    public static String compactWhitespace(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replaceAll("\\s+", " ").trim();
    }

    public static String stripQuotes(String raw) {
        if (raw == null) {
            return "";
        }
        String trimmed = raw.trim();
        if (trimmed.length() >= 2) {
            char first = trimmed.charAt(0);
            char last = trimmed.charAt(trimmed.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return trimmed.substring(1, trimmed.length() - 1);
            }
        }
        return trimmed;
    }

    public static String trimTrailingPunctuation(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().replaceAll("[,;:.]+$", "").trim();
    }

    public static boolean isNumeric(String raw) {
        if (raw == null) {
            return false;
        }
        return raw.trim().matches("[-+]?\\$?[0-9][0-9,]*(\\.[0-9]+)?%?");
    }

    public static boolean isBoolean(String raw) {
        if (raw == null) {
            return false;
        }
        String value = raw.trim().toLowerCase(Locale.ROOT);
        return "true".equals(value) || "false".equals(value);
    }

    public static String formatLiteral(String raw) {
        String cleaned = trimTrailingPunctuation(stripQuotes(raw));
        if (cleaned.isBlank()) {
            return "\"\"";
        }
        if (isNumeric(cleaned) || isBoolean(cleaned)) {
            return cleaned;
        }
        return '"' + cleaned + '"';
    }

    public static String unescapeXml(String raw) {
        if (raw == null) {
            return "";
        }
        return raw
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&apos;", "'")
            .replace("&amp;", "&");
    }
}
