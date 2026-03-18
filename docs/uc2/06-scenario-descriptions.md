# Task 6: Scenario Descriptions for Nontrivial Steps

## Nontrivial Step NT-1: Manual Update Processing

The nontrivial manual-update step begins after the actor has entered one or more formal rule statements and presses the manual update button. `BusinessRuleAppFrame` forwards the system event to `BusinessRuleController`, which acts as the **controller** for UC2. The controller creates a `ManualUpdateRulesCommand` object and invokes its `execute` operation.

The command delegates the request to `ManualRuleUpdateTemplate`. The template method `executeUpdate` defines the common algorithm for a rule update: validate the source, build rules, ask the expert to validate and rank them, store the ranked rules, and produce a result object. In the manual case, `ManualRuleUpdateTemplate` performs the mode-specific steps.

To build the rules, `ManualRuleUpdateTemplate` asks `RuleCreator` to create business rule objects from the submitted text. `RuleCreator` plays the **creator** role because it has the information needed to instantiate `BusinessRule` objects. For each statement, `RuleCreator` separates the condition text from the action list and asks `ConditionParser` to parse the condition. `ConditionParser` creates an `Expression` tree using the **Interpreter** pattern, for example `ComparisonExpression`, `AndExpression`, and `OrExpression`.

After the rules are created, `ManualRuleUpdateTemplate` sends the collection to `BusinessRuleExpert`. `BusinessRuleExpert` plays the **expert** role because it contains the rule validation and ranking knowledge. It removes duplicates, checks rule correctness, and orders rules by specificity. The template then stores the ranked rules through `RuleRepository`.

Finally, `ManualRuleUpdateTemplate` returns a `RuleUpdateResult` containing the ranked rules, messages, and parse-tree text. The controller returns the result to the UI, which refreshes the displayed rule list and shows the summary to the actor.

### Patterns and GRASP Used

- Controller: `BusinessRuleController`
- Creator: `RuleCreator`
- Expert: `BusinessRuleExpert`
- Command: `ManualUpdateRulesCommand`
- Template Method: `RuleUpdateTemplate` / `ManualRuleUpdateTemplate`
- Interpreter: `Expression` hierarchy

## Nontrivial Step NT-2: AI-Powered Update Processing

The nontrivial AI-powered step begins after the actor selects a document and presses the AI-powered update button. `BusinessRuleAppFrame` sends the request to `BusinessRuleController`, which creates an `AiUpdateRulesCommand` and executes it.

The command delegates to `AiRuleUpdateTemplate`, which follows the same template algorithm as the manual path. First, it validates that the selected file exists. Next, it asks `DocumentReaderFactory` to choose the proper `DocumentReader` implementation for the file type. The selected reader, such as `TextDocumentReader`, `DocxDocumentReader`, or `PdfDocumentReader`, extracts readable document text.

Once the raw document text is available, `AiRuleUpdateTemplate` creates a `RuleProcessingContext` and sends it through the AI pipeline. The pipeline is implemented with the **Chain of Responsibility** pattern. `RuleExtractionHandler` asks `HeuristicRuleExtractor` to find candidate rule statements. `RuleNormalizationHandler` removes duplicates and normalizes the rule syntax. `RuleRankingHandler` uses `RuleCreator` and `ConditionParser` to create `BusinessRule` objects and expression trees, then delegates validation and ranking to `BusinessRuleExpert`. `ParseTreeGenerationHandler` appends parse-tree status messages.

After the chain finishes, `AiRuleUpdateTemplate` stores the final ranked rules in `RuleRepository` and produces a `RuleUpdateResult`. The controller returns that result to the UI, which refreshes the current rule list and displays the pipeline summary to the actor.

### Patterns and GRASP Used

- Controller: `BusinessRuleController`
- Creator: `RuleCreator`
- Expert: `BusinessRuleExpert`
- Command: `AiUpdateRulesCommand`
- Template Method: `RuleUpdateTemplate` / `AiRuleUpdateTemplate`
- Chain of Responsibility: `RuleProcessingHandler` chain
- Interpreter: `Expression` hierarchy inside rule creation

## Comparison of the Two Nontrivial Steps

| Concern | Manual Path | AI-Powered Path |
|---|---|---|
| Input source | Formal rule text | Uploaded document |
| Command class | `ManualUpdateRulesCommand` | `AiUpdateRulesCommand` |
| Template subclass | `ManualRuleUpdateTemplate` | `AiRuleUpdateTemplate` |
| Main collaborators | `RuleCreator`, `ConditionParser`, `BusinessRuleExpert`, `RuleRepository` | `DocumentReaderFactory`, `DocumentReader`, processing chain, `RuleCreator`, `BusinessRuleExpert`, `RuleRepository` |
| Dominant GoF patterns | Command, Template Method, Interpreter | Command, Template Method, Chain of Responsibility, Interpreter |
