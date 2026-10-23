# Advanced Topics in Databases and Git

**Course:** Introduction to Programming (TU/e) — CBL project, worth 30% of the final grade
**Project:** CBL — a TypeRacer clone where users race by typing programming-language-specific words and text snippets.

## Introduction

For this project I chose two advanced topics:

1. **Advanced database topics** — specifically JSON-based document store data persistence (NoSQL-style storage).
2. **Using Git** — version control and collaborative branching for the codebase.

Below, for each chosen topic, I briefly describe why I chose it and how it is relevant to the project, what I learned through my research including the main concepts or principles, how I applied the topic in the project, and what I learned from the implementation, including the challenges, decisions, and the impact on the project.

---

## Topic 1: Advanced Database Topics

### 1.1 Why I chose this topic and how it is relevant to my project

I chose to go beyond basic, purely relational database thinking and instead explore how a **document store** (JSON) could be used to persist the coding snippets of the game.

This was relevant to my project because the game needs a place to store the text snippets players have to type. A relational database with rigid tables would be overkill for simple snippet bodies, whereas a JSON document store matches the flexible, evolving nature of the data (a snippet currently has a `body` field, but later on it could easily grow fields such as the programming language, author, difficulty, etc.).

### 1.2 What I learned through my research, including the main concepts or principles

Through my research I learned that:

- Databases can be **relational (SQL)** or **non-relational / NoSQL**. Document stores such as MongoDB store data as JSON-like documents grouped into *collections*.
- In a document store, each document is self-contained and schema-less, which makes it easy to add new fields without migrating a whole table.
- In Java, the **Gson** library maps JSON to plain old Java objects (POJOs) via reflection, and generic collections such as `List<Snippet>` require a `TypeToken` to be deserialized correctly.

### 1.3 How I applied the topic in my project

I applied this by:

- Creating the `Snippet` model class that holds the text of a snippet (`body`).
- Creating the `Database` class, which:
  - reads the `/DB.json` resource file from the classpath,
  - deserializes it into a `List<Snippet>` using Gson and a `TypeToken`,
  - picks a random snippet with `getRandomSnippet()`.

### 1.4 What I learned from the implementation, including challenges, decisions, and the impact on the project

- **Challenges:** I had to handle loading the JSON file through the classpath (`getResourceAsStream`) instead of a plain file path, and I had to handle the cases where the snippet list is empty or `null`.
- **Decisions:** I decided to use Gson for the JSON-to-Java mapping instead of writing manual parsing code, and to keep the snippet as a single `body` field for now.
- **Impact on the project:** The random snippet selection now works through the document store, and the structure makes it easy to extend the database later with more fields (language, author, difficulty) without redesigning the storage.

---

## Topic 2: Using Git

### 2.1 Why I chose this topic and how it is relevant to my project

I chose to focus on **using Git** because the project is collaborative (developed together with another author) and hosted on GitHub. A TypeRacer clone requires many small iterations in the UI and game logic, so version control is essential to track changes, experiment safely, and merge work done in parallel.

### 2.2 What I learned through my research, including the main concepts or principles

Through my research I learned the main Git concepts and principles:

- The basic workflow: `init`, `add`, `commit`, `push`, `pull`.
- Branches to isolate features, and merging branches back into `main`.
- Pull requests and merge commits as the way to integrate finished work from one branch into another.
- Keeping commit messages clear and meaningful.

### 2.3 How I applied the topic in my project

I applied Git by using feature branches such as `feature/game-window` and `feature/race-panel`, merging them back into `main` through merge commits (visible in the repository history, e.g. merges of pull requests), and writing descriptive commit messages (e.g. *"Lots of new changes in UI logic and new features, bunch of comments added"* / *"Major syntax cleanup"*).

### 2.4 What I learned from the implementation, including challenges, decisions, and the impact on the project

- **Challenges:** Coordinating parallel work on different features while avoiding conflicts between the UI work and the race logic.
- **Decisions:** I chose to develop each feature in its own branch and merge it back once it was complete, keeping `main` stable.
- **Impact on the project:** The collaboration stayed organized, the history of the project clearly shows how the game window and race panel features were built up, and the final code was assembled from the feature branches.

---

## Final Remarks

Both advanced topics — the JSON document database and Git version control — played a big role in the development of this project. The database gave the game a simple but extendable way to store and serve snippets, while Git kept a clean, collaborative workflow for a project built by two authors.
