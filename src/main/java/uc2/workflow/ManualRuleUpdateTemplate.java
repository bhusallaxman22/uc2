package uc2.workflow;

import java.util.List;
import uc2.domain.BusinessRule;
import uc2.expert.BusinessRuleExpert;
import uc2.repository.RuleRepository;
import uc2.service.RuleCreator;

public final class ManualRuleUpdateTemplate extends RuleUpdateTemplate<String> {
    private final RuleCreator creator;

    public ManualRuleUpdateTemplate(RuleRepository repository, BusinessRuleExpert expert, RuleCreator creator) {
        super(repository, expert);
        this.creator = creator;
    }

    @Override
    protected void validateSource(String source) {
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("Manual rule input is empty.");
        }
    }

    @Override
    protected List<BusinessRule> buildRules(String source, List<String> messages) {
        List<BusinessRule> rules = creator.createFromText(source);
        messages.add("Manual update parsed " + rules.size() + " rule(s).");
        return rules;
    }
}
