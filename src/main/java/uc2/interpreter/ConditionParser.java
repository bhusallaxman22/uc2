package uc2.interpreter;

import java.util.ArrayList;
import java.util.List;
import uc2.util.TextUtilities;

public final class ConditionParser {
    public Expression parse(String rawCondition) {
        String input = TextUtilities.compactWhitespace(rawCondition);
        if (input.isBlank() || "true".equalsIgnoreCase(input)) {
            return new LiteralExpression(true);
        }

        Parser parser = new Parser(tokenize(input));
        Expression expression = parser.parseExpression();
        parser.expect(TokenType.EOF, "Unexpected trailing tokens in condition: " + rawCondition);
        return expression;
    }

    private List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        int index = 0;
        while (index < input.length()) {
            char current = input.charAt(index);
            if (Character.isWhitespace(current)) {
                index++;
                continue;
            }

            if (input.startsWith("&&", index)) {
                tokens.add(new Token(TokenType.OPERATOR, "&&"));
                index += 2;
                continue;
            }
            if (input.startsWith("||", index)) {
                tokens.add(new Token(TokenType.OPERATOR, "||"));
                index += 2;
                continue;
            }
            if (input.startsWith(">=", index) || input.startsWith("<=", index) || input.startsWith("!=", index)
                || input.startsWith("==", index)) {
                tokens.add(new Token(TokenType.OPERATOR, input.substring(index, index + 2)));
                index += 2;
                continue;
            }
            if (current == '>' || current == '<') {
                tokens.add(new Token(TokenType.OPERATOR, Character.toString(current)));
                index++;
                continue;
            }
            if (current == '(') {
                tokens.add(new Token(TokenType.LEFT_PAREN, "("));
                index++;
                continue;
            }
            if (current == ')') {
                tokens.add(new Token(TokenType.RIGHT_PAREN, ")"));
                index++;
                continue;
            }
            if (current == '"' || current == '\'') {
                int end = index + 1;
                StringBuilder value = new StringBuilder();
                while (end < input.length()) {
                    char character = input.charAt(end);
                    if (character == '\\' && end + 1 < input.length()) {
                        value.append(input.charAt(end + 1));
                        end += 2;
                    } else if (character == current) {
                        break;
                    } else {
                        value.append(character);
                        end++;
                    }
                }
                if (end >= input.length() || input.charAt(end) != current) {
                    throw new IllegalArgumentException("Unterminated string literal in condition: " + input);
                }
                tokens.add(new Token(TokenType.STRING, value.toString()));
                index = end + 1;
                continue;
            }

            int start = index;
            while (index < input.length()) {
                char character = input.charAt(index);
                if (Character.isWhitespace(character) || character == '(' || character == ')' || character == '<'
                    || character == '>' || character == '!' || character == '=' || character == '&'
                    || character == '|') {
                    break;
                }
                index++;
            }
            String value = input.substring(start, index);
            if ("and".equalsIgnoreCase(value)) {
                tokens.add(new Token(TokenType.OPERATOR, "&&"));
            } else if ("or".equalsIgnoreCase(value)) {
                tokens.add(new Token(TokenType.OPERATOR, "||"));
            } else if ("contains".equalsIgnoreCase(value)) {
                tokens.add(new Token(TokenType.OPERATOR, "contains"));
            } else if (value.matches("[-+]?\\$?[0-9][0-9,]*(\\.[0-9]+)?%?")) {
                tokens.add(new Token(TokenType.NUMBER, value));
            } else {
                tokens.add(new Token(TokenType.IDENTIFIER, value));
            }
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    private record Token(TokenType type, String text) {
    }

    private enum TokenType {
        IDENTIFIER,
        STRING,
        NUMBER,
        OPERATOR,
        LEFT_PAREN,
        RIGHT_PAREN,
        EOF
    }

    private static final class Parser {
        private final List<Token> tokens;
        private int position;

        private Parser(List<Token> tokens) {
            this.tokens = tokens;
        }

        private Expression parseExpression() {
            return parseOr();
        }

        private Expression parseOr() {
            Expression expression = parseAnd();
            while (matchOperator("||")) {
                expression = new OrExpression(expression, parseAnd());
            }
            return expression;
        }

        private Expression parseAnd() {
            Expression expression = parsePrimary();
            while (matchOperator("&&")) {
                expression = new AndExpression(expression, parsePrimary());
            }
            return expression;
        }

        private Expression parsePrimary() {
            if (match(TokenType.LEFT_PAREN)) {
                Expression expression = parseExpression();
                expect(TokenType.RIGHT_PAREN, "Missing closing parenthesis.");
                return expression;
            }
            return parseComparison();
        }

        private Expression parseComparison() {
            Token field = consume(TokenType.IDENTIFIER, "Expected a field name.");
            Token operator = consume(TokenType.OPERATOR, "Expected a comparison operator.");
            if ("&&".equals(operator.text()) || "||".equals(operator.text())) {
                throw new IllegalArgumentException("Expected comparison operator after field: " + field.text());
            }

            Token value = current();
            if (value.type() == TokenType.STRING || value.type() == TokenType.NUMBER || value.type() == TokenType.IDENTIFIER) {
                position++;
                return new ComparisonExpression(field.text(), ComparisonOperator.fromToken(operator.text()), value.text());
            }
            throw new IllegalArgumentException("Expected a comparison value after " + field.text());
        }

        private boolean match(TokenType type) {
            if (current().type() == type) {
                position++;
                return true;
            }
            return false;
        }

        private boolean matchOperator(String operator) {
            if (current().type() == TokenType.OPERATOR && operator.equals(current().text())) {
                position++;
                return true;
            }
            return false;
        }

        private Token consume(TokenType type, String message) {
            Token token = current();
            if (token.type() != type) {
                throw new IllegalArgumentException(message);
            }
            position++;
            return token;
        }

        private void expect(TokenType type, String message) {
            Token token = current();
            if (token.type() != type) {
                throw new IllegalArgumentException(message);
            }
            position++;
        }

        private Token current() {
            return tokens.get(position);
        }
    }
}
