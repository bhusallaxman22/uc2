package uc2.interpreter;

public enum ComparisonOperator {
    EQUALS("=="),
    NOT_EQUALS("!="),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUALS(">="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUALS("<="),
    CONTAINS("contains");

    private final String symbol;

    ComparisonOperator(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }

    public static ComparisonOperator fromToken(String token) {
        return switch (token) {
            case "==" -> EQUALS;
            case "!=" -> NOT_EQUALS;
            case ">" -> GREATER_THAN;
            case ">=" -> GREATER_THAN_OR_EQUALS;
            case "<" -> LESS_THAN;
            case "<=" -> LESS_THAN_OR_EQUALS;
            case "contains" -> CONTAINS;
            default -> throw new IllegalArgumentException("Unsupported operator: " + token);
        };
    }
}
