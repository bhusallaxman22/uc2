package uc2.domain;

import java.util.List;

public record RequestProcessingResult(
    List<BusinessRule> matchedRules,
    List<String> actions,
    List<String> trace
) {
    public RequestProcessingResult {
        matchedRules = List.copyOf(matchedRules);
        actions = List.copyOf(actions);
        trace = List.copyOf(trace);
    }
}
