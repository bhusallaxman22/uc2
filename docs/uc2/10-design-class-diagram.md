# Task 10: Design Class Diagram (DCD)

This DCD is derived from the design sequence diagrams for UC2 and reflects the implemented Java design.

## Major Design Decisions

- `BusinessRuleController` receives system events for UC2.
- Commands encapsulate the two update requests.
- `RuleUpdateTemplate<T>` defines the common update algorithm.
- `RuleCreator` and `ConditionParser` create `BusinessRule` objects and their expression trees.
- `BusinessRuleExpert` validates and ranks rules.
- `DocumentReaderFactory` and `DocumentReader` polymorphism support multiple file types.
- `RuleProcessingHandler` and its subclasses realize the AI pipeline.

## ASCII Design Class Diagram

### A. Update Coordination

```text
+---------------------------------------------------------------+
| BusinessRuleController                                        |
+---------------------------------------------------------------+
| - repository: RuleRepository                                  |
| - manualUpdateTemplate: ManualRuleUpdateTemplate              |
| - aiUpdateTemplate: AiRuleUpdateTemplate                      |
| - engineService: RuleEngineService                            |
+---------------------------------------------------------------+
| + currentRules(): List<BusinessRule>                          |
| + updateManually(rulesText: String): RuleUpdateResult         |
| + updateWithDocument(path: Path): RuleUpdateResult            |
| + processRequest(requestText: String): RequestProcessingResult|
| + renderRules(): String                                       |
+------------------------------+--------------------------------+
                               |
                               | creates / invokes
                               v
+--------------------------------------+
| ApplicationCommand<R>                |
+--------------------------------------+
| + execute(): R                       |
+----------------+---------------------+
                 ^
                 |
      +----------+-------------------------------+
      |                                          |
+--------------------------------------+   +--------------------------------------+
| ManualUpdateRulesCommand             |   | AiUpdateRulesCommand                 |
+--------------------------------------+   +--------------------------------------+
| - template: ManualRuleUpdateTemplate |   | - template: AiRuleUpdateTemplate     |
| - rulesText: String                  |   | - source: Path                       |
| + execute(): RuleUpdateResult        |   | + execute(): RuleUpdateResult        |
+------------------+-------------------+   +------------------+-------------------+
                   |                                          |
                   +------------------+-----------------------+
                                      |
                                      v
+----------------------------------------------------+
| RuleUpdateTemplate<T>                              |
+----------------------------------------------------+
| - repository: RuleRepository                       |
| - expert: BusinessRuleExpert                       |
+----------------------------------------------------+
| + executeUpdate(source: T): RuleUpdateResult       |
| # validateSource(source: T): void                  |
| # buildRules(source: T, messages: List<String>)    |
|     : List<BusinessRule>                           |
+------------------------+---------------------------+
                         ^
         +---------------+------------------+
         |                                  |
+-------------------------------+   +--------------------------------------+
| ManualRuleUpdateTemplate      |   | AiRuleUpdateTemplate                  |
+-------------------------------+   +--------------------------------------+
| - creator: RuleCreator        |   | - readerFactory: DocumentReaderFactory|
+-------------------------------+   | - processingChain: RuleProcessingHandler |
| # validateSource(source)      |   +--------------------------------------+
| # buildRules(source, messages)|   | # validateSource(source)             |
+-------------------------------+   | # buildRules(source, messages)       |
                                    +--------------------------------------+
```

### B. Rule Construction and Interpretation

```text
+----------------------------------+      +----------------------------------+
| RuleCreator                      |      | BusinessRuleExpert               |
+----------------------------------+      +----------------------------------+
| - parser: ConditionParser        |      |                                  |
+----------------------------------+      +----------------------------------+
| + createFromText(rawRules: String): List<BusinessRule>                     |
| + createFromStatement(statement: String): BusinessRule                     |
+----------------------------------+      | + validateAndRank(rules): List<BusinessRule> |
                                          | + findMatchingRules(...): List<BusinessRule>  |
                                          +----------------------------------+

+----------------------------------+      +----------------------------------+
| ConditionParser                  |      | RuleRepository                    |
+----------------------------------+      +----------------------------------+
| + parse(rawCondition: String):   |      | + findAll(): List<BusinessRule>   |
|   Expression                     |      | + replaceAll(rules): void         |
+----------------------------------+      +----------------+-----------------+
                                                        ^
                                                        |
                                          +----------------------------------+
                                          | InMemoryRuleRepository           |
                                          +----------------------------------+

+----------------------------------+
| BusinessRule                     |
+----------------------------------+
| - id: String                     |
| - sourceText: String             |
| - condition: Expression          |
| - actions: List<String>          |
| - priority: int                  |
+----------------------------------+
| + specificity(): int             |
| + toDisplayString(): String      |
| + parseTree(): String            |
+----------------+-----------------+
                 |
                 | has
                 v
+----------------------------------+
| Expression                       |
+----------------------------------+
| + interpret(context): boolean    |
| + asRuleString(): String         |
| + asTree(indent): String         |
| + specificity(): int             |
+----------------+-----------------+
                 ^
   +-------------+---------------------------+------------------------------+
   |                                         |                              |
+-----------------------------+  +---------------------------+  +---------------------------+
| ComparisonExpression        |  | AndExpression             |  | OrExpression              |
+-----------------------------+  +---------------------------+  +---------------------------+
| - field: String             |  | - left: Expression        |  | - left: Expression        |
| - operator: ComparisonOperator | | - right: Expression     |  | - right: Expression       |
| - expectedValue: String     |  +---------------------------+  +---------------------------+
+-----------------------------+
```

### C. AI Pipeline and Document Processing

```text
+--------------------------------------+
| DocumentReaderFactory                |
+--------------------------------------+
| - readers: List<DocumentReader>      |
+--------------------------------------+
| + readerFor(path: Path): DocumentReader |
+------------------+-------------------+
                   |
                   | selects
                   v
+----------------------------------+
| DocumentReader                   |
+----------------------------------+
| + supports(path: Path): boolean  |
| + read(path: Path): String       |
+----------+--------------+--------------+
           ^              ^              ^
           |              |              |
+---------------------+ +---------------------+ +---------------------+
| TextDocumentReader  | | DocxDocumentReader  | | PdfDocumentReader   |
+---------------------+ +---------------------+ +---------------------+

+-------------------------------------------+
| RuleProcessingHandler                      |
+-------------------------------------------+
| + setNext(next): RuleProcessingHandler     |
| + handle(context: RuleProcessingContext)   |
+--------------------+----------------------+
                     ^
                     |
+-------------------------------------------+
| AbstractRuleProcessingHandler              |
+-------------------------------------------+
| - next: RuleProcessingHandler              |
+-------------------------------------------+
| + setNext(next): RuleProcessingHandler     |
| + handle(context: RuleProcessingContext)   |
| # process(context): void                   |
+--------------------+----------------------+
                     ^
   +-----------------+--------------------+----------------------+--------------------------+
   |                                      |                      |                          |
+-----------------------------+ +--------------------------+ +-----------------------+ +---------------------------+
| RuleExtractionHandler       | | RuleNormalizationHandler | | RuleRankingHandler    | | ParseTreeGenerationHandler|
+-----------------------------+ +--------------------------+ +-----------------------+ +---------------------------+
| - extractor:               | |                          | | - creator: RuleCreator | |                           |
|   HeuristicRuleExtractor   | |                          | | - expert:              | |                           |
|                            | |                          | |   BusinessRuleExpert   | |                           |
+-----------------------------+ +--------------------------+ +-----------------------+ +---------------------------+

+----------------------------------+
| RuleProcessingContext            |
+----------------------------------+
| - documentText: String           |
| - extractedStatements: List<String> |
| - rules: List<BusinessRule>      |
| - messages: List<String>         |
+----------------------------------+
```

## Relationship Summary

- `BusinessRuleController` depends on command and template objects.
- `ManualUpdateRulesCommand` and `AiUpdateRulesCommand` implement `ApplicationCommand<R>`.
- `ManualRuleUpdateTemplate` and `AiRuleUpdateTemplate` inherit from `RuleUpdateTemplate<T>`.
- `RuleCreator` creates `BusinessRule` and uses `ConditionParser`.
- `BusinessRule` contains an `Expression`.
- `AiRuleUpdateTemplate` uses `DocumentReaderFactory` and a `RuleProcessingHandler` chain.
- `RuleRankingHandler` depends on `RuleCreator` and `BusinessRuleExpert`.
- `RuleRepository` abstracts persistence and is implemented by `InMemoryRuleRepository`.

## DCD Consistency with Code

The DCD matches the current Java implementation under `src/main/java/uc2`. Additional classes for later request processing exist in the codebase, but the focus of this DCD is the UC2 update flow derived from tasks 6 through 9.
