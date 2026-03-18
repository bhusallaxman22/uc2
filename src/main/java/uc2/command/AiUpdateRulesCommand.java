package uc2.command;

import java.nio.file.Path;
import uc2.domain.RuleUpdateResult;
import uc2.workflow.AiRuleUpdateTemplate;

public final class AiUpdateRulesCommand implements ApplicationCommand<RuleUpdateResult> {
    private final AiRuleUpdateTemplate template;
    private final Path source;

    public AiUpdateRulesCommand(AiRuleUpdateTemplate template, Path source) {
        this.template = template;
        this.source = source;
    }

    @Override
    public RuleUpdateResult execute() throws Exception {
        return template.executeUpdate(source);
    }
}
