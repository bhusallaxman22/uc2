package uc2.pipeline;

public abstract class AbstractRuleProcessingHandler implements RuleProcessingHandler {
    private RuleProcessingHandler next;

    @Override
    public RuleProcessingHandler setNext(RuleProcessingHandler next) {
        this.next = next;
        return next;
    }

    @Override
    public final void handle(RuleProcessingContext context) {
        process(context);
        if (next != null) {
            next.handle(context);
        }
    }

    protected abstract void process(RuleProcessingContext context);
}
