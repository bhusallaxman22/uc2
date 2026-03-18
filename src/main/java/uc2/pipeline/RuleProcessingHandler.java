package uc2.pipeline;

public interface RuleProcessingHandler {
    RuleProcessingHandler setNext(RuleProcessingHandler next);

    void handle(RuleProcessingContext context);
}
