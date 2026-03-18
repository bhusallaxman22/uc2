# Task 11: Implementation Report

## Status

The optional implementation task has been completed in Java.

## Language and Platform

- Language: Java 21
- UI: Swing
- Build style: plain `javac`
- Main entry point: `uc2.app.Main`

## Source Layout

```text
src/main/java/uc2/
  app/          application wiring and entry points
  command/      command pattern classes
  controller/   system controller
  document/     file readers and reader factory
  domain/       core domain objects and results
  expert/       ranking and validation logic
  interpreter/  expression tree for rule conditions
  pipeline/     AI-powered update processing chain
  repository/   rule storage abstraction
  service/      creators and use-case services
  ui/           Swing user interface
  util/         normalization and text helpers
  workflow/     template-method update algorithms
```

## Implemented Functionality for UC2

- Display current business rules
- Manual business rule update
- AI-powered rule update from `txt`, `md`, `docx`, and `pdf`
- Rule ranking by specificity
- Parse-tree generation summary
- Request evaluation support for later rule execution

## Mapping from Documentation to Code

| Documentation Item | Implemented Classes |
|---|---|
| Controller | `BusinessRuleController` |
| Manual command flow | `ManualUpdateRulesCommand`, `ManualRuleUpdateTemplate` |
| AI-powered command flow | `AiUpdateRulesCommand`, `AiRuleUpdateTemplate` |
| Rule creation | `RuleCreator`, `ConditionParser`, `BusinessRule` |
| Ranking and validation | `BusinessRuleExpert` |
| AI pipeline | `RuleExtractionHandler`, `RuleNormalizationHandler`, `RuleRankingHandler`, `ParseTreeGenerationHandler` |
| Document access | `DocumentReaderFactory`, `TextDocumentReader`, `DocxDocumentReader`, `PdfDocumentReader` |

## Build Instructions

```bash
mkdir -p out
javac -d out $(find src/main/java -name '*.java' | sort)
```

## Run Instructions

```bash
java -cp out uc2.app.Main
```

## Verification

The implementation includes a self-test harness:

```bash
java -cp out uc2.app.SelfTest
```

This verifies:

- manual update parsing
- rule ranking
- request evaluation against ranked rules
- text document ingestion
- DOCX ingestion
- PDF text extraction

## ASCII Deployment View

```text
+------------------------------+
| User Workstation             |
|------------------------------|
| Java Runtime                 |
| Swing Desktop Application    |
+---------------+--------------+
                |
                v
      +------------------------+
      | UC2 Business Rule      |
      | Engine Application     |
      |------------------------|
      | UI                     |
      | Controller             |
      | Commands               |
      | Templates              |
      | Rule Services          |
      | AI Processing Chain    |
      | In-Memory Repository   |
      +-----------+------------+
                  |
                  +-----------------------------+
                  |                             |
                  v                             v
      +------------------------+     +------------------------+
      | Manual Rule Text       |     | Rule Specification     |
      | Entered by User        |     | Document (txt/docx/pdf)|
      +------------------------+     +------------------------+
```

## Demo Script

1. Launch the application.
2. Observe that the current rule list is initially empty or displays the most recent rules.
3. Use the manual update tab to enter two formal rules and apply the update.
4. Observe the ranked rule list and parse-tree output.
5. Use the AI-powered update tab to select a supported document.
6. Run the update and observe the extraction, normalization, and ranking messages.
7. Optionally use the request-processing tab to verify that the stored rules behave as expected.

## Limitations and Notes

- The AI-powered update is implemented as an offline heuristic pipeline rather than an external hosted LLM service.
- The repository is currently in-memory, so rule updates persist only for the current application run.
- The PDF reader first attempts Ghostscript when available and otherwise falls back to a built-in text extractor.

## Submission Guidance

For submission, include:

- the `docs/uc2` folder,
- the Java source code under `src/main/java/uc2`,
- the sample input file under `src/main/resources/samples`,
- and a short demo of the running application.
