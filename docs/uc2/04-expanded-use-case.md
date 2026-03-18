# Task 4: Expanded Use Case

## Use Case

**UC2: Edit Business Rules**

## Two-Column Expanded Use Case Table

| Actor Actions | System Responses |
|---|---|
| 1. The actor opens the rule-editing function. | 1. The system displays the current business rules and shows two update options: manual update and AI-powered update. |
| 2. The actor chooses an update mode. | 2. The system enables the input controls for the selected mode. |
| 3a. The actor selects **manual update** and enters one or more formal rule statements. | 3a. The system accepts the manual rule text. |
| 3b. The actor selects **AI-powered update** and chooses a rule specification document. | 3b. The system accepts the selected file path. |
| 4a. The actor submits the manual update. | 4a. The system validates the manual input, parses each rule, builds condition trees, ranks the rules, stores them, and prepares parse-tree output. |
| 4b. The actor submits the AI-powered update. | 4b. The system validates the document, selects a reader, extracts candidate rules from the document text, normalizes them, ranks them, stores them, and prepares parse-tree output. |
| 5. The actor reviews the result. | 5. The system displays the updated rules and a summary of the performed processing steps. |

## Nontrivial Steps

The following steps are nontrivial because they require collaboration among multiple objects and the application of design patterns.

- **NT-1**: Step 4a, manual update processing
- **NT-2**: Step 4b, AI-powered update processing

## Alternate Flows

### A1: Invalid Manual Rule Syntax

| Actor Actions | System Responses |
|---|---|
| A1.1 The actor submits malformed manual rule text. | A1.1 The system detects invalid syntax or missing actions. |
| A1.2 The actor reviews the error. | A1.2 The system displays an error message and keeps the previous valid rule set unchanged. |

### A2: Unsupported or Missing Document

| Actor Actions | System Responses |
|---|---|
| A2.1 The actor selects a missing or unsupported file. | A2.1 The system rejects the file and displays an explanatory message. |

### A3: No Rules Extracted from Document

| Actor Actions | System Responses |
|---|---|
| A3.1 The actor submits a document that contains no extractable rules. | A3.1 The system reports that no business rules were produced and keeps the previous valid rule set unchanged. |

## ASCII Foreground Flow Overview

```text
Actor                     System
-----                     ------
open UC2   ----------->   show current rules and update modes
choose mode ---------->   enable matching controls

manual path:
enter rules ---------->   accept manual text
submit      ---------->   validate -> parse -> rank -> store -> show summary

AI path:
choose file ---------->   accept document path
submit      ---------->   validate -> read -> extract -> normalize -> rank -> store -> show summary
```
