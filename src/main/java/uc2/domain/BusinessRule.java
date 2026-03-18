package uc2.domain;

import java.util.List;
import java.util.Objects;
import uc2.interpreter.Expression;

public final class BusinessRule {
    private final String id;
    private final String sourceText;
    private final Expression condition;
    private final List<String> actions;
    private final int priority;

    public BusinessRule(String id, String sourceText, Expression condition, List<String> actions, int priority) {
        this.id = Objects.requireNonNull(id, "id");
        this.sourceText = Objects.requireNonNull(sourceText, "sourceText");
        this.condition = Objects.requireNonNull(condition, "condition");
        this.actions = List.copyOf(actions);
        this.priority = priority;
    }

    public String id() {
        return id;
    }

    public String sourceText() {
        return sourceText;
    }

    public Expression condition() {
        return condition;
    }

    public List<String> actions() {
        return actions;
    }

    public int priority() {
        return priority;
    }

    public int specificity() {
        return condition.specificity();
    }

    public String toDisplayString() {
        return condition.asRuleString() + " -> " + String.join(", ", actions);
    }

    public String parseTree() {
        return "Rule " + id + System.lineSeparator() + condition.asTree("  ");
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}
