package uc2.pipeline;

import java.util.ArrayList;
import java.util.List;
import uc2.domain.BusinessRule;
import uc2.expert.BusinessRuleExpert;
import uc2.service.RuleCreator;

public final class RuleRankingHandler extends AbstractRuleProcessingHandler {
    private final RuleCreator creator;
    private final BusinessRuleExpert expert;

    public RuleRankingHandler(RuleCreator creator, BusinessRuleExpert expert) {
        this.creator = creator;
        this.expert = expert;
    }

    @Override
    protected void process(RuleProcessingContext context) {
        List<BusinessRule> rules = new ArrayList<>();
        for (String statement : context.extractedStatements()) {
            rules.add(creator.createFromStatement(statement));
        }

        List<BusinessRule> rankedRules = expert.validateAndRank(rules);
        context.setRules(rankedRules);
        context.addMessage("Rule ranking chain ordered rules by specificity.");
    }
}
