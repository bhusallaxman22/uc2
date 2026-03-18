package uc2.pipeline;

import uc2.domain.BusinessRule;

public final class ParseTreeGenerationHandler extends AbstractRuleProcessingHandler {
    @Override
    protected void process(RuleProcessingContext context) {
        for (BusinessRule rule : context.rules()) {
            context.addMessage("Parse tree ready for " + rule.id() + ".");
        }
    }
}
