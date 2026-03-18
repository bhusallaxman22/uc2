package uc2.controller;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import uc2.command.AiUpdateRulesCommand;
import uc2.command.ManualUpdateRulesCommand;
import uc2.command.ProcessRequestCommand;
import uc2.domain.BusinessRule;
import uc2.domain.RequestProcessingResult;
import uc2.domain.RuleUpdateResult;
import uc2.repository.RuleRepository;
import uc2.service.RuleEngineService;
import uc2.workflow.AiRuleUpdateTemplate;
import uc2.workflow.ManualRuleUpdateTemplate;

public final class BusinessRuleController {
    private final RuleRepository repository;
    private final ManualRuleUpdateTemplate manualUpdateTemplate;
    private final AiRuleUpdateTemplate aiUpdateTemplate;
    private final RuleEngineService engineService;

    public BusinessRuleController(
        RuleRepository repository,
        ManualRuleUpdateTemplate manualUpdateTemplate,
        AiRuleUpdateTemplate aiUpdateTemplate,
        RuleEngineService engineService
    ) {
        this.repository = repository;
        this.manualUpdateTemplate = manualUpdateTemplate;
        this.aiUpdateTemplate = aiUpdateTemplate;
        this.engineService = engineService;
    }

    public List<BusinessRule> currentRules() {
        return repository.findAll();
    }

    public RuleUpdateResult updateManually(String rulesText) throws Exception {
        return new ManualUpdateRulesCommand(manualUpdateTemplate, rulesText).execute();
    }

    public RuleUpdateResult updateWithDocument(Path path) throws Exception {
        return new AiUpdateRulesCommand(aiUpdateTemplate, path).execute();
    }

    public RequestProcessingResult processRequest(String requestText) throws Exception {
        return new ProcessRequestCommand(engineService, requestText).execute();
    }

    public String renderRules() {
        List<BusinessRule> rules = repository.findAll();
        if (rules.isEmpty()) {
            return "No business rules configured.\n\nExpected format:\nC1 && C2 && ... && Cn -> A1, A2, ..., Am";
        }

        return rules.stream()
            .map(rule -> "[priority=" + rule.priority() + "] " + rule.toDisplayString())
            .collect(Collectors.joining(System.lineSeparator()));
    }
}
