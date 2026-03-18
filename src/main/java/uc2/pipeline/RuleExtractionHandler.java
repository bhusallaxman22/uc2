package uc2.pipeline;

import java.util.List;
import uc2.service.HeuristicRuleExtractor;

public final class RuleExtractionHandler extends AbstractRuleProcessingHandler {
    private final HeuristicRuleExtractor extractor;

    public RuleExtractionHandler(HeuristicRuleExtractor extractor) {
        this.extractor = extractor;
    }

    @Override
    protected void process(RuleProcessingContext context) {
        List<String> statements = extractor.extractRuleStatements(context.documentText());
        context.setExtractedStatements(statements);
        context.addMessage("Rule extraction chain found " + statements.size() + " candidate rule(s).");
    }
}
