# Task 9: Design Sequence Diagrams

The following design sequence diagrams refine the informal diagrams by converting English messages into function calls with parameter names, parameter types, and return values.

## Design Sequence Diagram for NT-1: Manual Update Processing

```text
ui:BusinessRuleAppFrame
controller:BusinessRuleController
cmd:ManualUpdateRulesCommand
template:ManualRuleUpdateTemplate
creator:RuleCreator
parser:ConditionParser
expert:BusinessRuleExpert
repo:RuleRepository

ui -> controller :
  updateManually(rulesText: String) : RuleUpdateResult

controller -> cmd :
  <<create>>(template: ManualRuleUpdateTemplate, rulesText: String)

controller -> cmd :
  execute() : RuleUpdateResult

cmd -> template :
  executeUpdate(source: String) : RuleUpdateResult

template -> template :
  validateSource(source: String) : void

template -> template :
  buildRules(source: String, messages: List<String>) : List<BusinessRule>

template -> creator :
  createFromText(rawRules: String) : List<BusinessRule>

loop for each rule statement
  creator -> creator :
    createFromStatement(statement: String) : BusinessRule

  creator -> parser :
    parse(rawCondition: String) : Expression
end loop

template -> expert :
  validateAndRank(rules: Collection<BusinessRule>) : List<BusinessRule>

template -> repo :
  replaceAll(updatedRules: List<BusinessRule>) : void

template --> cmd :
  RuleUpdateResult

cmd --> controller :
  RuleUpdateResult

controller --> ui :
  RuleUpdateResult
```

## Design Sequence Diagram for NT-2: AI-Powered Update Processing

```text
ui:BusinessRuleAppFrame
controller:BusinessRuleController
cmd:AiUpdateRulesCommand
template:AiRuleUpdateTemplate
factory:DocumentReaderFactory
reader:DocumentReader
context:RuleProcessingContext
extract:RuleExtractionHandler
normalize:RuleNormalizationHandler
rank:RuleRankingHandler
parseTree:ParseTreeGenerationHandler
extractor:HeuristicRuleExtractor
creator:RuleCreator
parser:ConditionParser
expert:BusinessRuleExpert
repo:RuleRepository

ui -> controller :
  updateWithDocument(path: Path) : RuleUpdateResult

controller -> cmd :
  <<create>>(template: AiRuleUpdateTemplate, source: Path)

controller -> cmd :
  execute() : RuleUpdateResult

cmd -> template :
  executeUpdate(source: Path) : RuleUpdateResult

template -> template :
  validateSource(source: Path) : void

template -> factory :
  readerFor(path: Path) : DocumentReader

template -> reader :
  read(path: Path) : String

template -> context :
  <<create>>(documentText: String)

template -> extract :
  handle(context: RuleProcessingContext) : void

extract -> extractor :
  extractRuleStatements(documentText: String) : List<String>

extract -> normalize :
  handle(context: RuleProcessingContext) : void

normalize -> rank :
  handle(context: RuleProcessingContext) : void

loop for each normalized statement
  rank -> creator :
    createFromStatement(statement: String) : BusinessRule

  creator -> parser :
    parse(rawCondition: String) : Expression
end loop

rank -> expert :
  validateAndRank(rules: Collection<BusinessRule>) : List<BusinessRule>

rank -> parseTree :
  handle(context: RuleProcessingContext) : void

template -> repo :
  replaceAll(updatedRules: List<BusinessRule>) : void

template --> cmd :
  RuleUpdateResult

cmd --> controller :
  RuleUpdateResult

controller --> ui :
  RuleUpdateResult
```

## Notes

- The design diagrams are based on the implemented Java classes in `src/main/java/uc2`.
- The AI pipeline uses chained `handle(context)` calls as defined by the `RuleProcessingHandler` abstraction.
- The `RuleUpdateTemplate.executeUpdate` algorithm is common to both sequences and is specialized by the concrete subclasses.
