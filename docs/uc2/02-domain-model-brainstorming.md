# Task 2: Domain Model Brainstorming and UML Class Diagram

## Brainstorming Results

The following candidate concepts were identified from the business process description:

- business analyst
- business rule
- rule catalog
- condition
- atomic condition
- compound condition
- action
- rule specification document
- update session
- update mode
- parse tree
- rule update result

## Classification

### Classes

- BusinessAnalyst
- RuleCatalog
- BusinessRule
- Condition
- AtomicCondition
- CompoundCondition
- Action
- RuleSpecificationDocument
- UpdateSession
- ParseTree
- RuleUpdateResult

### Attributes of Classes

| Class | Attributes |
|---|---|
| BusinessAnalyst | analystId, name, role |
| RuleCatalog | catalogId, lastUpdatedAt |
| BusinessRule | ruleId, sourceText, priority |
| Condition | conditionText |
| AtomicCondition | fieldName, operator, expectedValue |
| CompoundCondition | logicalOperator |
| Action | actionText |
| RuleSpecificationDocument | documentId, fileName, fileType, rawText |
| UpdateSession | sessionId, updateMode, submittedAt |
| ParseTree | treeText |
| RuleUpdateResult | message, status |

### Relationships

| From | Relationship | To |
|---|---|---|
| BusinessAnalyst | initiates | UpdateSession |
| UpdateSession | updates | RuleCatalog |
| RuleCatalog | contains | BusinessRule |
| BusinessRule | has | Condition |
| BusinessRule | triggers | Action |
| CompoundCondition | is a | Condition |
| AtomicCondition | is a | Condition |
| RuleSpecificationDocument | supports | UpdateSession |
| BusinessRule | has | ParseTree |
| UpdateSession | produces | RuleUpdateResult |

## Notes on the Domain Model

- This is an **analysis-level** model, not a software design model.
- Therefore, the diagram intentionally does **not** show operations.
- The model focuses on business concepts, not implementation classes such as controllers or factories.

## ASCII UML Class Diagram

```text
+------------------+       initiates       +----------------------+
| BusinessAnalyst  |---------------------->| UpdateSession        |
+------------------+                       +----------------------+
| analystId        |                       | sessionId            |
| name             |                       | updateMode           |
| role             |                       | submittedAt          |
+------------------+                       +----------+-----------+
                                                      |
                                                      | updates
                                                      v
                                             +----------------------+
                                             | RuleCatalog          |
                                             +----------------------+
                                             | catalogId            |
                                             | lastUpdatedAt        |
                                             +----------+-----------+
                                                        |
                                                        | contains 1..*
                                                        v
                                             +----------------------+
                                             | BusinessRule         |
                                             +----------------------+
                                             | ruleId               |
                                             | sourceText           |
                                             | priority             |
                                             +---+--------------+---+
                                                 |              |
                               has 1             |              | triggers 1..*
                                                 |              v
                                                 v     +----------------------+
                                      +----------------------+ Action         |
                                      | Condition            |----------------|
                                      +----------------------+ actionText     |
                                      | conditionText        +----------------+
                                      +----------+-----------+
                                                 ^
                           +---------------------+----------------------+
                           |                                            |
                           | is a                                       | is a
                           |                                            |
              +---------------------------+                +---------------------------+
              | AtomicCondition           |                | CompoundCondition         |
              +---------------------------+                +---------------------------+
              | fieldName                 |                | logicalOperator           |
              | operator                  |                +---------------------------+
              | expectedValue             |
              +---------------------------+

+---------------------------+         supports         +----------------------+
| RuleSpecificationDocument |------------------------->| UpdateSession        |
+---------------------------+                          +----------------------+
| documentId                |
| fileName                  |
| fileType                  |
| rawText                   |
+---------------------------+

+------------------+         has              +----------------------+
| BusinessRule     |------------------------->| ParseTree            |
+------------------+                          +----------------------+
                                              | treeText             |
                                              +----------------------+

+------------------+       produces           +----------------------+
| UpdateSession    |------------------------->| RuleUpdateResult     |
+------------------+                          +----------------------+
                                              | message              |
                                              | status               |
                                              +----------------------+
```

## Domain Model Rationale

- `RuleCatalog` is included because UC2 replaces and redisplays the current rule set.
- `BusinessRule`, `Condition`, and `Action` are the core business concepts.
- `RuleSpecificationDocument` is required because AI-powered update starts from a business document.
- `ParseTree` is modeled because the assignment explicitly includes parse-tree generation in the AI path.
- `UpdateSession` represents one execution of UC2 and ties the actor, input mode, and outcome together.
