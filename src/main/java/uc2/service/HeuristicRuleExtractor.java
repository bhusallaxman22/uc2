package uc2.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uc2.util.IdentifierNormalizer;
import uc2.util.TextUtilities;

public final class HeuristicRuleExtractor {
    private static final Pattern IF_THEN_PATTERN =
        Pattern.compile("(?i)(?:if|when)\\s+(.+?)\\s*(?:,?\\s+then\\s+|,\\s+)(.+)");

    public List<String> extractRuleStatements(String documentText) {
        if (documentText == null || documentText.isBlank()) {
            return List.of();
        }

        LinkedHashSet<String> statements = new LinkedHashSet<>();
        for (String line : documentText.split("\\R")) {
            String trimmed = TextUtilities.compactWhitespace(line);
            if (trimmed.isBlank()) {
                continue;
            }
            if (trimmed.contains("->")) {
                statements.add(trimmed.replace("=>", "->"));
            } else {
                fromNaturalLanguage(trimmed).ifPresent(statements::add);
            }
        }

        String flattened = documentText.replace('\r', '\n').replaceAll("\\s+", " ");
        for (String sentence : flattened.split("(?<=[.!?])\\s+")) {
            String trimmed = TextUtilities.compactWhitespace(sentence);
            if (trimmed.isBlank()) {
                continue;
            }
            fromNaturalLanguage(trimmed).ifPresent(statements::add);
        }

        return new ArrayList<>(statements);
    }

    private Optional<String> fromNaturalLanguage(String sentence) {
        String cleanedSentence = sentence
            .replaceFirst("^[\\-*]+\\s*", "")
            .replaceAll("^\\d+\\.\\s*", "")
            .trim();

        Matcher matcher = IF_THEN_PATTERN.matcher(cleanedSentence);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String normalizedCondition = normalizeConditionGroup(matcher.group(1));
        String normalizedAction = normalizeActions(matcher.group(2));
        if (normalizedCondition.isBlank() || normalizedAction.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(normalizedCondition + " -> " + normalizedAction);
    }

    private String normalizeConditionGroup(String rawCondition) {
        String symbolic = rawCondition
            .replaceAll("(?i)\\band\\b", " && ")
            .replaceAll("(?i)\\bor\\b", " || ");

        List<String> connectors = new ArrayList<>();
        List<String> clauses = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\s*(&&|\\|\\|)\\s*").matcher(symbolic);
        int previous = 0;
        while (matcher.find()) {
            clauses.add(symbolic.substring(previous, matcher.start()).trim());
            connectors.add(matcher.group(1));
            previous = matcher.end();
        }
        clauses.add(symbolic.substring(previous).trim());

        List<String> normalizedClauses = new ArrayList<>();
        for (String clause : clauses) {
            String normalized = normalizeClause(clause);
            if (normalized.isBlank()) {
                return "";
            }
            normalizedClauses.add(normalized);
        }

        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < normalizedClauses.size(); index++) {
            if (index > 0) {
                builder.append(' ').append(connectors.get(index - 1)).append(' ');
            }
            builder.append(normalizedClauses.get(index));
        }
        return builder.toString();
    }

    private String normalizeClause(String rawClause) {
        String clause = TextUtilities.trimTrailingPunctuation(
            rawClause.replaceAll("(?i)\\b(the|a|an)\\b", " ").replaceAll("\\s+", " ").trim()
        );
        if (clause.isBlank()) {
            return "";
        }

        String direct = normalizeDirectOperatorClause(clause);
        if (!direct.isBlank()) {
            return direct;
        }

        String[][] operatorPatterns = {
            {"(?i)(.+?)\\s+(?:is\\s+)?greater than or equal to\\s+(.+)", ">="},
            {"(?i)(.+?)\\s+(?:is\\s+)?at least\\s+(.+)", ">="},
            {"(?i)(.+?)\\s+(?:is\\s+)?less than or equal to\\s+(.+)", "<="},
            {"(?i)(.+?)\\s+(?:is\\s+)?at most\\s+(.+)", "<="},
            {"(?i)(.+?)\\s+(?:is\\s+)?greater than\\s+(.+)", ">"},
            {"(?i)(.+?)\\s+(?:is\\s+)?more than\\s+(.+)", ">"},
            {"(?i)(.+?)\\s+(?:is\\s+)?less than\\s+(.+)", "<"},
            {"(?i)(.+?)\\s+(?:does\\s+)?contain\\s+(.+)", "contains"},
            {"(?i)(.+?)\\s+(?:is\\s+)?not equal to\\s+(.+)", "!="},
            {"(?i)(.+?)\\s+(?:is\\s+)?not\\s+(.+)", "!="},
            {"(?i)(.+?)\\s+(?:equals|equal to|is)\\s+(.+)", "=="}
        };

        for (String[] operatorPattern : operatorPatterns) {
            Matcher matcher = Pattern.compile(operatorPattern[0]).matcher(clause);
            if (matcher.matches()) {
                return buildAtomicCondition(matcher.group(1), operatorPattern[1], matcher.group(2));
            }
        }

        return "";
    }

    private String normalizeDirectOperatorClause(String clause) {
        Matcher matcher = Pattern.compile("(.+?)\\s*(==|!=|>=|<=|>|<|contains)\\s*(.+)", Pattern.CASE_INSENSITIVE).matcher(clause);
        if (!matcher.matches()) {
            return "";
        }
        return buildAtomicCondition(matcher.group(1), matcher.group(2).toLowerCase(Locale.ROOT), matcher.group(3));
    }

    private String buildAtomicCondition(String rawField, String operator, String rawValue) {
        String field = IdentifierNormalizer.normalizeFieldName(rawField);
        String value = TextUtilities.formatLiteral(rawValue);
        if (field.isBlank() || value.isBlank()) {
            return "";
        }
        return field + " " + operator + " " + value;
    }

    private String normalizeActions(String rawActionText) {
        String cleaned = TextUtilities.trimTrailingPunctuation(
            rawActionText.replaceAll("(?i)\\bthen\\b", " ").replaceAll("\\s+", " ").trim()
        );
        if (cleaned.isBlank()) {
            return "";
        }

        String[] parts = cleaned.split("\\s*,\\s*|\\s+and\\s+");
        List<String> actions = new ArrayList<>();
        for (String part : parts) {
            String action = TextUtilities.trimTrailingPunctuation(part);
            if (!action.isBlank()) {
                actions.add(action);
            }
        }
        return String.join(", ", actions);
    }
}
