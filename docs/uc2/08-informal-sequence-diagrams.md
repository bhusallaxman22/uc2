# Task 8: Informal Sequence Diagrams

The following diagrams are informal sequence diagrams. The messages are labeled in English rather than function signatures.

## Informal Sequence Diagram for NT-1: Manual Update Processing

```text
Actor/UI              Controller            Manual Command      Manual Template       Rule Creator        Condition Parser     Rule Expert       Repository
   |                       |                       |                    |                    |                    |                  |                |
   | request manual update |                       |                    |                    |                    |                  |                |
   |---------------------->|                       |                    |                    |                    |                  |                |
   |                       | create/execute manual |                    |                    |                    |                  |                |
   |                       | command               |                    |                    |                    |                  |                |
   |                       |---------------------->|                    |                    |                    |                  |                |
   |                       |                       | ask template to    |                    |                    |                  |                |
   |                       |                       | perform manual     |                    |                    |                  |                |
   |                       |                       | update             |                    |                    |                  |                |
   |                       |                       |------------------->|                    |                    |                  |                |
   |                       |                       |                    | validate source    |                    |                  |                |
   |                       |                       |                    | create rules from  |                    |                  |                |
   |                       |                       |                    | submitted text     |                    |                  |                |
   |                       |                       |                    |------------------->|                    |                  |                |
   |                       |                       |                    |                    | parse each         |                  |                |
   |                       |                       |                    |                    | condition          |                  |                |
   |                       |                       |                    |                    |------------------->|                  |                |
   |                       |                       |                    |                    | create business    |                  |                |
   |                       |                       |                    |                    | rules              |                  |                |
   |                       |                       |                    | validate and rank  |                    |                  |                |
   |                       |                       |                    |------------------------------->|                  |                |
   |                       |                       |                    | replace stored rules                                 |                |
   |                       |                       |                    |----------------------------------------------------->|                |
   |                       |                       |                    | return update result                                   |                |
   |                       |<----------------------------------------------------------------------------------------------------|                |
   | show updated rules    |                       |                    |                    |                    |                  |                |
   |<----------------------|                       |                    |                    |                    |                  |                |
```

## Informal Sequence Diagram for NT-2: AI-Powered Update Processing

```text
Actor/UI              Controller         AI Command       AI Template      Reader Factory     Document Reader     Processing Chain      Rule Creator    Rule Expert   Repository
   |                      |                  |                 |                 |                   |                   |                   |              |            |
   | request AI update    |                  |                 |                 |                   |                   |                   |              |            |
   |--------------------->|                  |                 |                 |                   |                   |                   |              |            |
   |                      | create/execute   |                 |                 |                   |                   |                   |              |            |
   |                      | AI command       |                 |                 |                   |                   |                   |              |            |
   |                      |----------------->|                 |                 |                   |                   |                   |              |            |
   |                      |                  | ask template to |                 |                   |                   |                   |              |            |
   |                      |                  | perform AI      |                 |                   |                   |                   |              |            |
   |                      |                  | update          |                 |                   |                   |                   |              |            |
   |                      |                  |--------------->|                 |                   |                   |                   |              |            |
   |                      |                  |                | validate file    |                   |                   |                   |              |            |
   |                      |                  |                | choose reader    |                   |                   |                   |              |            |
   |                      |                  |                |---------------->|                   |                   |                   |              |            |
   |                      |                  |                |                 | ask selected      |                   |                   |              |            |
   |                      |                  |                |                 | reader to read    |                   |                   |              |            |
   |                      |                  |                |                 |------------------>|                   |                   |              |            |
   |                      |                  |                | create pipeline context                 |                   |                   |              |            |
   |                      |                  |                | start processing chain                 |                   |                   |              |            |
   |                      |                  |                |--------------------------------------------------------->|                   |              |            |
   |                      |                  |                |                   chain extracts, normalizes, ranks, and generates parse-tree messages         |
   |                      |                  |                |--------------------------------------------------------------------------------------->|              |            |
   |                      |                  |                |                                                                 validate and rank       |            |
   |                      |                  |                |------------------------------------------------------------------------------------------------>|            |
   |                      |                  |                | replace stored rules                                                                                      |
   |                      |                  |                |---------------------------------------------------------------------------------------------------------------->| 
   |                      |<-----------------------------------------------------------------------------------------------------------------------------------------------|
   | show updated rules   |                  |                 |                 |                   |                   |                   |              |            |
   |<---------------------|                  |                 |                 |                   |                   |                   |              |            |
```
