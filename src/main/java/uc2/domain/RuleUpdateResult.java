package uc2.domain;

import java.util.List;

public record RuleUpdateResult(List<BusinessRule> rules, List<String> messages, List<String> parseTrees) {
    public RuleUpdateResult {
        rules = List.copyOf(rules);
        messages = List.copyOf(messages);
        parseTrees = List.copyOf(parseTrees);
    }
}
