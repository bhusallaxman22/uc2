package uc2.interpreter;

import uc2.domain.RequestContext;

public final class LiteralExpression implements Expression {
    private final boolean value;

    public LiteralExpression(boolean value) {
        this.value = value;
    }

    @Override
    public boolean interpret(RequestContext context) {
        return value;
    }

    @Override
    public String asRuleString() {
        return Boolean.toString(value);
    }

    @Override
    public String asTree(String indent) {
        return indent + "LITERAL(" + value + ")";
    }

    @Override
    public int specificity() {
        return 0;
    }
}
