package uc2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import uc2.domain.BusinessRule;
import uc2.interpreter.ConditionParser;
import uc2.interpreter.Expression;
import uc2.util.TextUtilities;

public final class RuleCreator {
    private final ConditionParser parser;

    public RuleCreator(ConditionParser parser) {
        this.parser = parser;
    }

    public List<BusinessRule> createFromText(String rawRules) {
        List<BusinessRule> rules = new ArrayList<>();
        if (rawRules == null || rawRules.isBlank()) {
            return rules;
        }

        for (String line : rawRules.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.isBlank()) {
                continue;
            }
            rules.add(createFromStatement(trimmed));
        }
        return rules;
    }

    public BusinessRule createFromStatement(String statement) {
        String normalizedStatement = TextUtilities.compactWhitespace(statement.replace("=>", "->"));
        String[] halves = normalizedStatement.split("\\s*->\\s*", 2);
        if (halves.length != 2) {
            throw new IllegalArgumentException("Invalid rule format. Expected 'condition -> action': " + statement);
        }

        String conditionText = halves[0].trim();
        String actionText = halves[1].trim();
        if (conditionText.isBlank() || actionText.isBlank()) {
            throw new IllegalArgumentException("Invalid rule format. Condition or action is blank: " + statement);
        }

        Expression condition = parser.parse(conditionText);
        List<String> actions = new ArrayList<>();
        for (String action : actionText.split("\\s*,\\s*")) {
            String normalizedAction = TextUtilities.trimTrailingPunctuation(action);
            if (!normalizedAction.isBlank()) {
                actions.add(normalizedAction);
            }
        }
        if (actions.isEmpty()) {
            throw new IllegalArgumentException("Rule has no actions: " + statement);
        }

        return new BusinessRule(UUID.randomUUID().toString().substring(0, 8), normalizedStatement, condition, actions, condition.specificity());
    }
}
