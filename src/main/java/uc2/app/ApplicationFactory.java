package uc2.app;

import java.util.List;
import uc2.controller.BusinessRuleController;
import uc2.document.DocxDocumentReader;
import uc2.document.DocumentReaderFactory;
import uc2.document.PdfDocumentReader;
import uc2.document.TextDocumentReader;
import uc2.expert.BusinessRuleExpert;
import uc2.interpreter.ConditionParser;
import uc2.pipeline.ParseTreeGenerationHandler;
import uc2.pipeline.RuleExtractionHandler;
import uc2.pipeline.RuleNormalizationHandler;
import uc2.pipeline.RuleProcessingHandler;
import uc2.pipeline.RuleRankingHandler;
import uc2.repository.InMemoryRuleRepository;
import uc2.repository.RuleRepository;
import uc2.service.HeuristicRuleExtractor;
import uc2.service.RuleCreator;
import uc2.service.RuleEngineService;
import uc2.workflow.AiRuleUpdateTemplate;
import uc2.workflow.ManualRuleUpdateTemplate;

public final class ApplicationFactory {
    private ApplicationFactory() {
    }

    public static BusinessRuleController createController() {
        RuleRepository repository = new InMemoryRuleRepository();
        BusinessRuleExpert expert = new BusinessRuleExpert();
        RuleCreator creator = new RuleCreator(new ConditionParser());

        RuleProcessingHandler extraction = new RuleExtractionHandler(new HeuristicRuleExtractor());
        RuleProcessingHandler normalization = new RuleNormalizationHandler();
        RuleProcessingHandler ranking = new RuleRankingHandler(creator, expert);
        RuleProcessingHandler parseTree = new ParseTreeGenerationHandler();
        extraction.setNext(normalization).setNext(ranking).setNext(parseTree);

        DocumentReaderFactory documentReaderFactory = new DocumentReaderFactory(List.of(
            new TextDocumentReader(),
            new DocxDocumentReader(),
            new PdfDocumentReader()
        ));

        ManualRuleUpdateTemplate manualTemplate = new ManualRuleUpdateTemplate(repository, expert, creator);
        AiRuleUpdateTemplate aiTemplate = new AiRuleUpdateTemplate(repository, expert, documentReaderFactory, extraction);
        RuleEngineService engineService = new RuleEngineService(repository, expert);

        return new BusinessRuleController(repository, manualTemplate, aiTemplate, engineService);
    }
}
