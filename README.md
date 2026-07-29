# StudyDeck

A JavaFX trivia and study app. Make an account, browse questions by category, take a quiz, get a score.

CST 338 Project 2 - Team NAiDev

## Slices

We are 3 people, so we build 3 of the 5 slices. Accounts is required.

| Slice | Owner | Github | Enhancement | Status |
|---|---|---|---|---|
| 1 - Accounts | Joshua Burke | joburke-bot | Trivia Account Login | complete |
| 2 - Question Bank | William Delgado | williamzdelgado | REST API import (Open Trivia DB) | planned |
| 3 - Quiz Engine | Allen Orozco | allorozco-eng | JavaFX Data Binding| planned |

Issues, branches and PRs get linked here as we go.

## WILL NOT DO

- Slice 4, Leaderboard and History - cut, only 3 of us
- Slice 5, Study Mode and Tagging - cut, only 3 of us
- Question Bank: no media questions, no difficulty levels, no CSV import (doing the REST import instead)

If we finish early we can pull one of these back for extra credit.

## Code reviews

| PR | Author | Reviewer | AI review | Outcome |
|---|---|---|---|---|

## AI usage

Each of us logs our AI-drafted tests in TESTING.md and links our AI code review PR here.

## Extra credit

| What | Who | Link |
|---|---|---|

## Build and run

```
./gradlew run
./gradlew test
```

Needs JDK 25 and JavaFX 25. The gradle wrapper is committed so you don't need gradle installed. Open the folder in IntelliJ and let it import the gradle build.
