package uc2.repository;

import java.util.ArrayList;
import java.util.List;
import uc2.domain.BusinessRule;

public final class InMemoryRuleRepository implements RuleRepository {
    private final List<BusinessRule> rules = new ArrayList<>();

    @Override
    public synchronized List<BusinessRule> findAll() {
        return List.copyOf(rules);
    }

    @Override
    public synchronized void replaceAll(List<BusinessRule> updatedRules) {
        rules.clear();
        rules.addAll(updatedRules);
    }
}
