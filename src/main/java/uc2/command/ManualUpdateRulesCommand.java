package uc2.command;

import uc2.domain.RuleUpdateResult;
import uc2.workflow.ManualRuleUpdateTemplate;

public final class ManualUpdateRulesCommand implements ApplicationCommand<RuleUpdateResult> {
    private final ManualRuleUpdateTemplate template;
    private final String rulesText;

    public ManualUpdateRulesCommand(ManualRuleUpdateTemplate template, String rulesText) {
        this.template = template;
        this.rulesText = rulesText;
    }

    @Override
    public RuleUpdateResult execute() throws Exception {
        return template.executeUpdate(rulesText);
    }
}
