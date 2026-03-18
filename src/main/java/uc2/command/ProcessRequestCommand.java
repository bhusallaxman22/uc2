package uc2.command;

import uc2.domain.RequestProcessingResult;
import uc2.service.RuleEngineService;

public final class ProcessRequestCommand implements ApplicationCommand<RequestProcessingResult> {
    private final RuleEngineService engineService;
    private final String requestText;

    public ProcessRequestCommand(RuleEngineService engineService, String requestText) {
        this.engineService = engineService;
        this.requestText = requestText;
    }

    @Override
    public RequestProcessingResult execute() {
        return engineService.process(requestText);
    }
}
