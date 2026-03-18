package uc2.workflow;

import java.util.ArrayList;
import java.util.List;
import uc2.domain.BusinessRule;
import uc2.domain.RuleUpdateResult;
import uc2.expert.BusinessRuleExpert;
import uc2.repository.RuleRepository;

public abstract class RuleUpdateTemplate<T> {
    private final RuleRepository repository;
    private final BusinessRuleExpert expert;

    protected RuleUpdateTemplate(RuleRepository repository, BusinessRuleExpert expert) {
        this.repository = repository;
        this.expert = expert;
    }

    public final RuleUpdateResult executeUpdate(T source) throws Exception {
        validateSource(source);

        List<String> messages = new ArrayList<>();
        List<BusinessRule> builtRules = buildRules(source, messages);
        List<BusinessRule> rankedRules = expert.validateAndRank(builtRules);
        repository.replaceAll(rankedRules);
        messages.add("Stored " + rankedRules.size() + " business rule(s).");

        List<String> parseTrees = rankedRules.stream()
            .map(BusinessRule::parseTree)
            .toList();
        return new RuleUpdateResult(rankedRules, messages, parseTrees);
    }

    protected abstract void validateSource(T source);

    protected abstract List<BusinessRule> buildRules(T source, List<String> messages) throws Exception;
}
