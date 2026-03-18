package uc2.domain;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import uc2.util.IdentifierNormalizer;

public final class RequestContext {
    private final Map<String, String> values;

    public RequestContext(Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    public static RequestContext fromText(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            throw new IllegalArgumentException("Request input is empty.");
        }

        Map<String, String> values = new LinkedHashMap<>();
        String[] lines = rawInput.split("\\R");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isBlank()) {
                continue;
            }

            String[] parts = trimmed.split("\\s*[:=]\\s*", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid request entry: " + trimmed);
            }

            String key = IdentifierNormalizer.normalizeFieldName(parts[0]);
            String value = parts[1].trim();
            if (key.isBlank() || value.isBlank()) {
                throw new IllegalArgumentException("Invalid request entry: " + trimmed);
            }
            values.put(key, value);
        }

        if (values.isEmpty()) {
            throw new IllegalArgumentException("Request input is empty.");
        }
        return new RequestContext(values);
    }

    public String valueOf(String key) {
        Objects.requireNonNull(key, "key");
        return values.get(IdentifierNormalizer.normalizeFieldName(key));
    }

    public Map<String, String> values() {
        return values;
    }
}
