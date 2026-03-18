# Task 7: Scenario Tables

## Scenario Table for NT-1: Manual Update Processing

| Step | Sender | Receiver | Responsibility / English Message | Pattern or GRASP |
|---|---|---|---|---|
| 1 | BusinessRuleAppFrame | BusinessRuleController | Request a manual business rule update | Controller |
| 2 | BusinessRuleController | ManualUpdateRulesCommand | Create and execute the manual update command | Command |
| 3 | ManualUpdateRulesCommand | ManualRuleUpdateTemplate | Perform the manual update algorithm | Command, Template Method |
| 4 | ManualRuleUpdateTemplate | ManualRuleUpdateTemplate | Validate that the manual source is not empty | Template Method |
| 5 | ManualRuleUpdateTemplate | RuleCreator | Create rule objects from the submitted text | Creator |
| 6 | RuleCreator | ConditionParser | Parse each condition into an expression tree | Interpreter |
| 7 | RuleCreator | BusinessRule | Create a business rule containing condition and actions | Creator |
| 8 | ManualRuleUpdateTemplate | BusinessRuleExpert | Validate and rank the created rules | Expert |
| 9 | ManualRuleUpdateTemplate | RuleRepository | Replace the stored rule set with the ranked rules | Expert-informed persistence |
| 10 | ManualRuleUpdateTemplate | RuleUpdateResult | Package the update summary and parse-tree results | Result construction |
| 11 | BusinessRuleController | BusinessRuleAppFrame | Return the result so the UI can refresh output | Controller |

## Scenario Table for NT-2: AI-Powered Update Processing

| Step | Sender | Receiver | Responsibility / English Message | Pattern or GRASP |
|---|---|---|---|---|
| 1 | BusinessRuleAppFrame | BusinessRuleController | Request an AI-powered business rule update | Controller |
| 2 | BusinessRuleController | AiUpdateRulesCommand | Create and execute the AI update command | Command |
| 3 | AiUpdateRulesCommand | AiRuleUpdateTemplate | Perform the AI update algorithm | Command, Template Method |
| 4 | AiRuleUpdateTemplate | AiRuleUpdateTemplate | Validate that the selected document exists | Template Method |
| 5 | AiRuleUpdateTemplate | DocumentReaderFactory | Choose the proper reader for the uploaded file | Factory-style selection |
| 6 | DocumentReaderFactory | DocumentReader | Read the document into raw text | Polymorphism |
| 7 | AiRuleUpdateTemplate | RuleProcessingContext | Create the pipeline context object | Context creation |
| 8 | AiRuleUpdateTemplate | RuleExtractionHandler | Start the processing chain | Chain of Responsibility |
| 9 | RuleExtractionHandler | HeuristicRuleExtractor | Extract candidate rule statements from document text | Chain of Responsibility |
| 10 | RuleNormalizationHandler | RuleProcessingContext | Normalize and deduplicate extracted rules | Chain of Responsibility |
| 11 | RuleRankingHandler | RuleCreator | Create `BusinessRule` objects from normalized statements | Creator |
| 12 | RuleCreator | ConditionParser | Parse each condition into an expression tree | Interpreter |
| 13 | RuleRankingHandler | BusinessRuleExpert | Validate and rank the created rules | Expert |
| 14 | ParseTreeGenerationHandler | RuleProcessingContext | Record parse-tree generation messages | Chain of Responsibility |
| 15 | AiRuleUpdateTemplate | RuleRepository | Replace the stored rule set with the ranked rules | Persistence |
| 16 | AiRuleUpdateTemplate | RuleUpdateResult | Package the update summary and parse-tree results | Result construction |
| 17 | BusinessRuleController | BusinessRuleAppFrame | Return the result so the UI can refresh output | Controller |
