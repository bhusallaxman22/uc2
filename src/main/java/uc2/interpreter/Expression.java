package uc2.interpreter;

import uc2.domain.RequestContext;

public interface Expression {
    boolean interpret(RequestContext context);

    String asRuleString();

    String asTree(String indent);

    int specificity();
}
