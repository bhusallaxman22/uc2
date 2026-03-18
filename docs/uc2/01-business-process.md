# Task 1: Business Process Description for UC2

## Use Case

**UC2: Edit Business Rules**

## Business Goal

Allow a business user to update the business rules used by the application without modifying source code. The user may either:

- enter rules manually in a formal rule format, or
- upload a rule specification document and let the system generate the formal rules automatically.

## Business Context

Organizations use business rules to make operational decisions such as discounts, shipping eligibility, or fee calculation. Traditionally, whenever rules change, developers must:

1. read the updated business rule document,
2. locate the relevant code,
3. modify the code,
4. test the change, and
5. redeploy the application.

UC2 improves this process by moving rule updates into the application itself.

## As-Is Process

1. A business user identifies that the current rules are outdated.
2. The business user communicates the new policy to a software team.
3. Developers interpret the document manually.
4. Developers rewrite program logic.
5. Developers test and redeploy the application.
6. The business user waits for the new version before the updated rules can be used.

## To-Be Process with UC2

1. A business user opens the rule-editing function.
2. The system shows the current rules and offers two update modes.
3. The user chooses either manual update or AI-powered update.
4. The system validates the input.
5. The system transforms the input into ranked formal business rules.
6. The system stores the new rule set.
7. The system displays the updated rules and parse-tree information.
8. The updated rules become available for later request processing.

## Participants

- **Business Analyst / Operations User**: updates rules.
- **UC2 Business Rule Engine**: validates, transforms, ranks, and stores rules.
- **Rule Specification Document**: optional input source for AI-powered update.

## Business Objects

- Rule catalog
- Business rule
- Condition
- Action
- Rule specification document
- Parse tree
- Update summary

## Business Process Narrative

The process begins when a business user decides that the currently stored business rules are no longer correct. The user opens the UC2 screen and sees the existing ranked rules. If the user already knows the formal rule syntax, the user enters one or more rules directly. If the user only has a policy document, the user uploads that document and requests AI-powered processing.

For a manual update, the system parses each rule statement, builds a condition tree, validates the presence of actions, ranks the rules by specificity, and replaces the stored rule catalog with the new ranked set. For an AI-powered update, the system reads the document, extracts candidate rules from the text, normalizes them into the formal rule notation, ranks them, generates parse-tree summaries, and stores the result.

The process ends when the system confirms that the new rules are stored and displays the updated rule list and supporting output.

## ASCII Business Process Diagram

```text
+---------------------------+
| Need to change a policy?  |
+-------------+-------------+
              |
              v
+---------------------------+
| Open UC2: Edit Rules      |
+-------------+-------------+
              |
              v
+---------------------------+
| System shows current      |
| rules and update options  |
+-------------+-------------+
              |
   +----------+-----------+
   |                      |
   v                      v
+----------------+   +----------------------+
| Manual update  |   | AI-powered update    |
| selected       |   | selected             |
+-------+--------+   +----------+-----------+
        |                       |
        v                       v
+----------------+   +----------------------+
| Enter formal   |   | Upload rule          |
| rules          |   | specification file   |
+-------+--------+   +----------+-----------+
        |                       |
        v                       v
+----------------+   +----------------------+
| Parse, build   |   | Read, extract,       |
| parse trees,   |   | normalize, rank,     |
| rank, store    |   | generate parse tree, |
|                |   | store                |
+-------+--------+   +----------+-----------+
        |                       |
        +-----------+-----------+
                    |
                    v
        +-------------------------------+
        | Show updated rules and output |
        +-------------------------------+
```

## Outcome

UC2 shortens the rule-change cycle, reduces dependence on code changes for every policy update, and gives business users direct control over the rule base.
