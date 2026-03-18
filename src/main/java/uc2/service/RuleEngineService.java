package uc2.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import uc2.domain.BusinessRule;
import uc2.domain.RequestContext;
import uc2.domain.RequestProcessingResult;
import uc2.expert.BusinessRuleExpert;
import uc2.repository.RuleRepository;

public final class RuleEngineService {
    private final RuleRepository repository;
    private final BusinessRuleExpert expert;

    public RuleEngineService(RuleRepository repository, BusinessRuleExpert expert) {
        this.repository = repository;
        this.expert = expert;
    }

    public RequestProcessingResult process(String rawRequest) {
        RequestContext context = RequestContext.fromText(rawRequest);
        List<BusinessRule> rankedRules = repository.findAll();
        List<String> trace = new ArrayList<>();
        List<BusinessRule> matches = new ArrayList<>();
        LinkedHashSet<String> actions = new LinkedHashSet<>();

        for (BusinessRule rule : rankedRules) {
            boolean matched = rule.condition().interpret(context);
            trace.add((matched ? "MATCH " : "SKIP  ") + rule.toDisplayString());
            if (matched) {
                matches.add(rule);
                actions.addAll(rule.actions());
            }
        }

        List<BusinessRule> rankedMatches = expert.findMatchingRules(context, matches);
        return new RequestProcessingResult(rankedMatches, new ArrayList<>(actions), trace);
    }
}
