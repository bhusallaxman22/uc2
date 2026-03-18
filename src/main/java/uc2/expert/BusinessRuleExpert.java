package uc2.expert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import uc2.domain.BusinessRule;
import uc2.domain.RequestContext;

public final class BusinessRuleExpert {
    public List<BusinessRule> validateAndRank(Collection<BusinessRule> rules) {
        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("No business rules were produced.");
        }

        Map<String, BusinessRule> uniqueRules = new LinkedHashMap<>();
        for (BusinessRule rule : rules) {
            validate(rule);
            uniqueRules.putIfAbsent(rule.toDisplayString().toLowerCase(Locale.ROOT), rule);
        }

        List<BusinessRule> rankedRules = new ArrayList<>(uniqueRules.values());
        rankedRules.sort(
            Comparator.comparingInt(BusinessRule::specificity).reversed()
                .thenComparing(BusinessRule::toDisplayString)
        );
        return rankedRules;
    }

    public List<BusinessRule> findMatchingRules(RequestContext context, List<BusinessRule> rules) {
        List<BusinessRule> matches = new ArrayList<>();
        for (BusinessRule rule : rules) {
            if (rule.condition().interpret(context)) {
                matches.add(rule);
            }
        }
        return matches;
    }

    private void validate(BusinessRule rule) {
        if (rule.actions().isEmpty()) {
            throw new IllegalArgumentException("Rule has no actions: " + rule.sourceText());
        }
        if (rule.toDisplayString().isBlank()) {
            throw new IllegalArgumentException("Rule is blank.");
        }
    }
}
