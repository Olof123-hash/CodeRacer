# CBL: Coding TyperRacer

A TypeRacer clone for programmers. Instead of normal sentences, the player races to type real lines of code. Each race shows one random code snippet, and at the end the game reports the player's WPM, accuracy and number of mistakes.

This is a collaborative project by two Computer Science and Engineering students at TU/e, made for the course *Introduction to Programming* (30% of the final grade). It is built with Java Swing and Maven.

## Requirements

- **Java JDK 21 or newer** (the project is compiled with `maven.compiler.release` 21)
- **Apache Maven 3.9 or newer** (NetBeans bundles its own Maven, so it is not needed if you only use NetBeans)
- **Git** (only needed to clone the repository)

### Dependencies

No manual installation is needed for these:

Gson 2.8.6  Reads the code snippets from `DB.json` Downloaded automatically by Maven 
NetBeans AbsoluteLayout Layout used by the GUI forms Bundled in the repository's `lib/` folder 

## Getting the code

git clone https://github.com/marilou125/CBL.git
cd CBL


## Building

### With Maven

From the project root (the folder that contains `pom.xml`):

mvn clean compile


### With NetBeans

1. Choose **File → Open Project** and select the project folder.
2. Right-click the project and choose **Clean and Build**.
3. The Output window should end with `BUILD SUCCESS`.

## Running

### With Maven

mvn exec:java

### With NetBeans

Right-click `TyperRacerApp.java` and choose **Run File** (or press Shift+F6).

The entry point of the application is the class `TyperRacerApp`. The game window opens with a background image and a **Start New Race** button in the middle. 

### Adding your own snippets or removing snippets

Open `src/main/resources/DB.json` and add an entry to the list:

json
{ "body": "for (int i = 0; i < 10; i++) {" }

Separate entries with commas, with no comma after the last one. Escape double quotes inside a snippet with a backslash (`\"`), and avoid leading or trailing spaces.

## Authors

- *Your name*
- *Your partner's name*
