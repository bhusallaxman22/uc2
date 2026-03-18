package uc2.pipeline;

import java.util.ArrayList;
import java.util.List;
import uc2.domain.BusinessRule;

public final class RuleProcessingContext {
    private final String documentText;
    private List<String> extractedStatements = new ArrayList<>();
    private List<BusinessRule> rules = new ArrayList<>();
    private final List<String> messages = new ArrayList<>();

    public RuleProcessingContext(String documentText) {
        this.documentText = documentText;
    }

    public String documentText() {
        return documentText;
    }

    public List<String> extractedStatements() {
        return extractedStatements;
    }

    public void setExtractedStatements(List<String> extractedStatements) {
        this.extractedStatements = new ArrayList<>(extractedStatements);
    }

    public List<BusinessRule> rules() {
        return rules;
    }

    public void setRules(List<BusinessRule> rules) {
        this.rules = new ArrayList<>(rules);
    }

    public List<String> messages() {
        return List.copyOf(messages);
    }

    public void addMessage(String message) {
        messages.add(message);
    }
}
