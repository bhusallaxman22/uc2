package uc2.interpreter;

import java.util.Locale;
import uc2.domain.RequestContext;
import uc2.util.IdentifierNormalizer;
import uc2.util.TextUtilities;

public final class ComparisonExpression implements Expression {
    private final String field;
    private final ComparisonOperator operator;
    private final String expectedValue;

    public ComparisonExpression(String field, ComparisonOperator operator, String expectedValue) {
        this.field = IdentifierNormalizer.normalizeFieldName(field);
        this.operator = operator;
        this.expectedValue = TextUtilities.trimTrailingPunctuation(expectedValue);
    }

    @Override
    public boolean interpret(RequestContext context) {
        String actualValue = context.valueOf(field);
        if (actualValue == null) {
            return false;
        }

        String actual = TextUtilities.stripQuotes(actualValue);
        String expected = TextUtilities.stripQuotes(expectedValue);

        return switch (operator) {
            case EQUALS -> compare(actual, expected) == 0;
            case NOT_EQUALS -> compare(actual, expected) != 0;
            case GREATER_THAN -> compare(actual, expected) > 0;
            case GREATER_THAN_OR_EQUALS -> compare(actual, expected) >= 0;
            case LESS_THAN -> compare(actual, expected) < 0;
            case LESS_THAN_OR_EQUALS -> compare(actual, expected) <= 0;
            case CONTAINS -> actual.toLowerCase(Locale.ROOT).contains(expected.toLowerCase(Locale.ROOT));
        };
    }

    @Override
    public String asRuleString() {
        return field + " " + operator.symbol() + " " + TextUtilities.formatLiteral(expectedValue);
    }

    @Override
    public String asTree(String indent) {
        return indent + operator.name() + "(" + field + ", " + TextUtilities.formatLiteral(expectedValue) + ")";
    }

    @Override
    public int specificity() {
        return 1;
    }

    private int compare(String actual, String expected) {
        Double actualNumber = toNumber(actual);
        Double expectedNumber = toNumber(expected);
        if (actualNumber != null && expectedNumber != null) {
            return Double.compare(actualNumber, expectedNumber);
        }

        if (TextUtilities.isBoolean(actual) && TextUtilities.isBoolean(expected)) {
            return Boolean.compare(Boolean.parseBoolean(actual), Boolean.parseBoolean(expected));
        }

        return actual.trim().compareToIgnoreCase(expected.trim());
    }

    private Double toNumber(String raw) {
        String sanitized = raw.trim()
            .replace("$", "")
            .replace(",", "")
            .replace("%", "");
        if (!sanitized.matches("[-+]?[0-9]+(\\.[0-9]+)?")) {
            return null;
        }
        return Double.parseDouble(sanitized);
    }
}
