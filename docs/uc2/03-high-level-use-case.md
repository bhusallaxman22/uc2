# Task 3: High-Level Use Case

## Use Case Name

**UC2: Edit Business Rules**

## Primary Actor

Business Analyst / Operations User

## Supporting Actors

- Rule specification document
- Local file system

## Goal

Replace the current business rule set with a new validated and ranked rule set, using either manual entry or AI-powered document processing.

## Scope

UC2 Business Rule Engine

## Trigger

The actor decides that existing business rules must be updated.

## TUCBW

**The use case begins when** the business analyst opens the rule-editing function and requests to replace the current business rules by either:

- entering formal rules manually, or
- uploading a business rule specification document for AI-powered processing.

## TUCEW

**The use case ends when** the system has:

1. validated the submitted input,
2. transformed the input into formal business rules,
3. ranked and stored the resulting rules, and
4. displayed the updated rule set and summary information to the actor.

## Preconditions

- The application is running.
- The actor has access to the UC2 screen.
- If AI-powered update is chosen, the selected document exists and is in a supported format.

## Postconditions

- The stored rule catalog is replaced by the new ranked rule set.
- The updated rules are available to other application functions.
- The actor receives confirmation or an error explanation.

## Success Guarantee

If the use case completes successfully, the repository contains the updated ranked rules and the UI displays them.

## Minimal Guarantee

If the use case fails, the system reports the failure and does not replace the rule catalog with invalid or unreadable rule data.
