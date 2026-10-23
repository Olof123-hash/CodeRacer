# CBL: Coding TyperRacer

A TypeRacer clone for programmers. Instead of normal sentences, the player races to type real lines of code. Each race shows one random code snippet, and at the end the game reports the player's WPM, accuracy and number of mistakes.

This is a collaborative project by two Computer Science and Engineering students at TU/e, made for the course *Introduction to Programming* (30% of the final grade). It is built with Java Swing and Maven.

## Requirements

- **Java JDK 21 or newer** (the project is compiled with `maven.compiler.release` 21)
- **Apache Maven 3.9 or newer** (NetBeans bundles its own Maven, so it is not needed if you only use NetBeans)
- **Git** (only needed to clone the repository)

Check your installed versions:

```
java -version
mvn -version
```

### Dependencies

No manual installation is needed for these:

| Library | Purpose | How it is provided |
|---|---|---|
| Gson 2.8.6 | Reads the code snippets from `DB.json` | Downloaded automatically by Maven |
| NetBeans AbsoluteLayout | Layout used by the GUI forms | Bundled in the repository's `lib/` folder |

## Getting the code

```
git clone https://github.com/marilou125/CBL.git
cd CBL
```

## Building

### With Maven

From the project root (the folder that contains `pom.xml`):

```
mvn clean compile
```

### With NetBeans

1. Choose **File → Open Project** and select the project folder.
2. Right-click the project and choose **Clean and Build**.
3. The Output window should end with `BUILD SUCCESS`.

## Running

### With Maven

```
mvn exec:java
```

### With NetBeans

Right-click `TyperRacerApp.java` and choose **Run File** (or press Shift+F6).

The entry point of the application is the class `TyperRacerApp`. The game window opens with a background image and a **Start New Race** button in the middle. The game is designed for a window of about 1200 x 700 pixels, and the components scale proportionally if the window is resized.

## How to play

1. Click **Start New Race**.
2. Type the line of code shown on screen.
3. The newest character turns green if it is correct and red if it is wrong. A wrong character must be fixed with backspace before you can continue.
4. When you type the last character correctly, the race ends and a window shows your WPM, accuracy and mistakes.
5. Start another race to get a new random snippet.

## Project structure

Source files are under `src/main/java`, and resources are under `src/main/resources`.

- **`TyperRacerApp.java`**: the entry point. It opens the main window containing `RacePanel`.
- **`UI` package**
  - `RacePanel.java`: the main game screen. It shows the snippet, takes the player's typing, colors the typed characters and displays live statistics.
  - `BackgroundPanel.java`: draws the background image behind the start screen. The image is AI-generated and used only for styling.
  - `GameOver.java`: the window that shows the statistics when a race is finished.
- **`logic` package**
  - `RaceEngine.java`: the game logic, with no Swing code. It compares the typed text with the target text, rejects typing past a mistake, runs the timer, and calculates WPM, accuracy and mistakes. `RacePanel` reads these values through getters.
- **`database` package**
  - `Database.java`: reads the snippets from `DB.json` using Gson and returns one random snippet to `RacePanel`.
- **`model` package**
  - `Snippet.java`: a small data class holding the text of one snippet.
- **Resources**
  - `DB.json`: all the code snippets.
  - `backgroundstartpanel.jpg`: the background image.

### How the pieces work together

`RacePanel` asks `Database` for a random `Snippet`, creates a new `RaceEngine` with its text, and passes everything the player types to the engine. The engine decides whether the input is accepted, and `RacePanel` displays the result. When the engine reports that the race is finished, `RacePanel` opens `GameOver` with the final statistics.

### How WPM and accuracy are calculated

- **WPM** = (typed characters / 5) / minutes elapsed. This is the standard definition used by typing tests, so every character counts equally, including symbols such as braces.
- **Accuracy** = correct keystrokes / total keystrokes. Because the player cannot continue past a mistake, accuracy is measured while typing, not from the final text.

### Adding your own snippets

Open `src/main/resources/DB.json` and add an entry to the list:

```json
{ "body": "for (int i = 0; i < 10; i++) {" }
```

Separate entries with commas, with no comma after the last one. Escape double quotes inside a snippet with a backslash (`\"`), and avoid leading or trailing spaces.

## Advanced topics

- **Databases:** the snippets are stored in a JSON file (`DB.json`) and loaded with Gson. `Database.java` is the only class that knows where the snippets come from, so the storage could be replaced without changing the rest of the program.
- **Git and GitHub:** the project was developed with feature branches and pull requests.

## Troubleshooting

- **`mvn` is not recognized:** Maven is not installed or its `bin` folder is not on your PATH. Install Maven, or use NetBeans instead.
- **"invalid target release" or similar Java error:** your JDK is older than 21. Install JDK 21 or newer.
- **The background image is missing (dark screen):** run **Clean and Build** so the resources are copied.
- **Dependency "AbsoluteLayout" cannot be resolved:** make sure the `lib/` folder from the repository is present next to `pom.xml`.

## Authors

- *Your name*
- *Your partner's name*
