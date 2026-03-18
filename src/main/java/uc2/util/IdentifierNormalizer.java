package uc2.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class IdentifierNormalizer {
    private IdentifierNormalizer() {
    }

    public static String normalizeFieldName(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }

        String[] segments = raw.trim().split("\\.");
        List<String> normalizedSegments = new ArrayList<>();
        for (String segment : segments) {
            String normalized = normalizeSegment(segment);
            if (!normalized.isBlank()) {
                normalizedSegments.add(normalized);
            }
        }

        if (normalizedSegments.isEmpty()) {
            return "";
        }
        return String.join(".", normalizedSegments);
    }

    private static String normalizeSegment(String raw) {
        String spaced = raw
            .replaceAll("([a-z0-9])([A-Z])", "$1 $2")
            .replaceAll("[^A-Za-z0-9]+", " ")
            .trim();
        if (spaced.isBlank()) {
            return "";
        }

        String[] words = spaced.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            String lower = word.toLowerCase(Locale.ROOT);
            if (builder.isEmpty()) {
                builder.append(lower);
            } else {
                builder.append(Character.toUpperCase(lower.charAt(0)));
                builder.append(lower.substring(1));
            }
        }
        return builder.toString();
    }
}
