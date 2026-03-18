package uc2.interpreter;

import uc2.domain.RequestContext;

public final class OrExpression implements Expression {
    private final Expression left;
    private final Expression right;

    public OrExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean interpret(RequestContext context) {
        return left.interpret(context) || right.interpret(context);
    }

    @Override
    public String asRuleString() {
        return left.asRuleString() + " || " + right.asRuleString();
    }

    @Override
    public String asTree(String indent) {
        return indent + "OR" + System.lineSeparator()
            + left.asTree(indent + "  ") + System.lineSeparator()
            + right.asTree(indent + "  ");
    }

    @Override
    public int specificity() {
        return left.specificity() + right.specificity();
    }
}
