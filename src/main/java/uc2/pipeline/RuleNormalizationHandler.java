package uc2.pipeline;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import uc2.util.TextUtilities;

public final class RuleNormalizationHandler extends AbstractRuleProcessingHandler {
    @Override
    protected void process(RuleProcessingContext context) {
        LinkedHashSet<String> normalizedStatements = new LinkedHashSet<>();
        for (String statement : context.extractedStatements()) {
            String normalized = TextUtilities.compactWhitespace(statement.replace("=>", "->"));
            if (!normalized.isBlank()) {
                normalizedStatements.add(normalized);
            }
        }

        context.setExtractedStatements(new ArrayList<>(normalizedStatements));
        context.addMessage("Rule normalization chain retained " + normalizedStatements.size() + " unique rule(s).");
    }
}
