# Task 5: Application-Specific Structural Designs for Required GoF Patterns

This section applies the required GoF patterns to UC2 by replacing generic pattern roles with application-specific classes.

## 5.1 Interpreter Pattern

### Generic-to-Application Mapping

| Generic Role | UC2 Class |
|---|---|
| AbstractExpression | `Expression` |
| TerminalExpression | `ComparisonExpression`, `LiteralExpression` |
| NonterminalExpression | `AndExpression`, `OrExpression` |
| Context | `RequestContext` |
| Client | `ConditionParser`, `RuleCreator`, `BusinessRule` |

### Structural View

```text
                    +----------------------+
                    | Expression           |
                    +----------------------+
                    | interpret(context)   |
                    | asRuleString()       |
                    | asTree(indent)       |
                    | specificity()        |
                    +----------+-----------+
                               ^
         +---------------------+----------------------+
         |                     |                      |
         |                     |                      |
+----------------------+  +------------------+  +------------------+
| ComparisonExpression |  | AndExpression    |  | OrExpression     |
+----------------------+  +------------------+  +------------------+
| field                |  | left             |  | left             |
| operator             |  | right            |  | right            |
| expectedValue        |  +------------------+  +------------------+
+----------------------+
         ^
         |
+----------------------+
| LiteralExpression    |
+----------------------+

ConditionParser creates Expression trees.
BusinessRule stores one Expression tree as its condition.
```

## 5.2 Command Pattern

### Generic-to-Application Mapping

| Generic Role | UC2 Class |
|---|---|
| Command | `ApplicationCommand<R>` |
| ConcreteCommand | `ManualUpdateRulesCommand`, `AiUpdateRulesCommand` |
| Receiver | `ManualRuleUpdateTemplate`, `AiRuleUpdateTemplate` |
| Invoker | `BusinessRuleController` |
| Client | `BusinessRuleAppFrame` |

### Structural View

```text
+----------------------+
| ApplicationCommand<R>|
+----------------------+
| execute()            |
+----------+-----------+
           ^
           |
   +-------+-------------------+
   |                           |
+-------------------------+  +----------------------+
| ManualUpdateRulesCommand|  | AiUpdateRulesCommand |
+-------------------------+  +----------------------+
| template                |  | template             |
| rulesText               |  | source               |
+-------------------------+  +----------------------+
           |                           |
           v                           v
+-------------------------+  +----------------------+
| ManualRuleUpdateTemplate|  | AiRuleUpdateTemplate |
+-------------------------+  +----------------------+

BusinessRuleController creates and invokes the commands.
```

## 5.3 Chain of Responsibility Pattern

### Generic-to-Application Mapping

| Generic Role | UC2 Class |
|---|---|
| Handler | `RuleProcessingHandler` |
| AbstractHandler | `AbstractRuleProcessingHandler` |
| ConcreteHandler 1 | `RuleExtractionHandler` |
| ConcreteHandler 2 | `RuleNormalizationHandler` |
| ConcreteHandler 3 | `RuleRankingHandler` |
| ConcreteHandler 4 | `ParseTreeGenerationHandler` |
| Request | `RuleProcessingContext` |

### Structural View

```text
+-------------------------+
| RuleProcessingHandler   |
+-------------------------+
| setNext(next)           |
| handle(context)         |
+------------+------------+
             ^
             |
+------------------------------+
| AbstractRuleProcessingHandler|
+------------------------------+
| next                         |
+------------------------------+
| setNext(next)                |
| handle(context)              |
| process(context)             |
+-------------+----------------+
              ^
   +----------+-----------+----------------------+----------------------+
   |                      |                      |                      |
   |                      |                      |                      |
+-------------------+ ++-------------------+ ++-------------------+ ++------------------------+
| RuleExtraction    | | RuleNormalization  | | RuleRanking        | | ParseTreeGeneration    |
| Handler           | | Handler            | | Handler            | | Handler                |
+-------------------+ ++-------------------+ ++-------------------+ ++------------------------+

Chain order:
RuleExtractionHandler
    -> RuleNormalizationHandler
    -> RuleRankingHandler
    -> ParseTreeGenerationHandler
```

## 5.4 Template Method Pattern

### Generic-to-Application Mapping

| Generic Role | UC2 Class |
|---|---|
| AbstractClass | `RuleUpdateTemplate<T>` |
| Template Method | `executeUpdate(source)` |
| Primitive Operation 1 | `validateSource(source)` |
| Primitive Operation 2 | `buildRules(source, messages)` |
| ConcreteClass 1 | `ManualRuleUpdateTemplate` |
| ConcreteClass 2 | `AiRuleUpdateTemplate` |

### Structural View

```text
+-----------------------------------+
| RuleUpdateTemplate<T>             |
+-----------------------------------+
| repository                        |
| expert                            |
+-----------------------------------+
| executeUpdate(source)             |
| validateSource(source)            |
| buildRules(source, messages)      |
+----------------+------------------+
                 ^
                 |
      +----------+-----------+
      |                      |
+-----------------------+  +----------------------+
| ManualRuleUpdateTemplate| | AiRuleUpdateTemplate|
+-----------------------+  +----------------------+
| creator               |  | readerFactory        |
|                       |  | processingChain      |
+-----------------------+  +----------------------+
```

## Pattern Rationale

- **Interpreter** fits because rule conditions must be represented as parse trees and evaluated structurally.
- **Command** fits because manual and AI-powered updates are user-triggered operations that can be encapsulated as executable requests.
- **Chain of Responsibility** fits because AI-powered processing is naturally a pipeline of ordered transformation steps.
- **Template Method** fits because both update modes share the same overall algorithm but differ in validation and rule-construction details.
