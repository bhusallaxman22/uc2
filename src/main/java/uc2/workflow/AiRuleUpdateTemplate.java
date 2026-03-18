package uc2.workflow;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import uc2.document.DocumentReaderFactory;
import uc2.domain.BusinessRule;
import uc2.expert.BusinessRuleExpert;
import uc2.pipeline.RuleProcessingContext;
import uc2.pipeline.RuleProcessingHandler;
import uc2.repository.RuleRepository;

public final class AiRuleUpdateTemplate extends RuleUpdateTemplate<Path> {
    private final DocumentReaderFactory readerFactory;
    private final RuleProcessingHandler processingChain;

    public AiRuleUpdateTemplate(
        RuleRepository repository,
        BusinessRuleExpert expert,
        DocumentReaderFactory readerFactory,
        RuleProcessingHandler processingChain
    ) {
        super(repository, expert);
        this.readerFactory = readerFactory;
        this.processingChain = processingChain;
    }

    @Override
    protected void validateSource(Path source) {
        if (source == null || !Files.exists(source)) {
            throw new IllegalArgumentException("Selected document does not exist.");
        }
    }

    @Override
    protected List<BusinessRule> buildRules(Path source, List<String> messages) throws Exception {
        String documentText = readerFactory.readerFor(source).read(source);
        if (documentText.isBlank()) {
            throw new IllegalArgumentException("The selected document did not yield readable text.");
        }

        RuleProcessingContext context = new RuleProcessingContext(documentText);
        processingChain.handle(context);
        messages.add("AI-powered update processed: " + source.getFileName());
        messages.addAll(context.messages());
        return context.rules();
    }
}
