# UC2 Business Rule Engine

Java desktop application for the UC2 assignment in `UC2-Edit Business Rules-1.pdf`.

## What It Covers

- Manual business rule updates in the required format:
  - `C1 && C2 && ... && Cn -> A1, A2, ..., Am`
- AI-powered document updates from `txt`, `md`, `docx`, and `pdf`
- Rule ranking by specificity
- Request submission and rule evaluation
- A Swing UI for update and request-processing flows
- Explicit use of the required patterns:
  - Controller: `uc2.controller.BusinessRuleController`
  - Expert: `uc2.expert.BusinessRuleExpert`
  - Creator: `uc2.service.RuleCreator`
  - Interpreter: `uc2.interpreter.*`
  - Command: `uc2.command.*`
  - Chain of Responsibility: `uc2.pipeline.*`
  - Template Method: `uc2.workflow.*`

## Build

```bash
mkdir -p out
javac -d out $(find src/main/java -name '*.java' | sort)
```

## Run

```bash
java -cp out uc2.app.Main
```

## Verify

```bash
java -cp out uc2.app.SelfTest
```

## UC2 Documentation

The requested task 1 through 11 documentation for **UC2: Edit Business Rules** is in:

```text
docs/uc2/
```

## Notes

- The AI-powered update is implemented as an offline rule-extraction pipeline so it works without external services.
- PDF extraction first tries `gs` (Ghostscript) when available and falls back to a built-in text extractor.
- A sample rule file is available at `src/main/resources/samples/discount-rules.txt`.
