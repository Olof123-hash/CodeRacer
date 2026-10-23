# CBL: TyperRacerApp

**Project Overview**

This is a collaborative project by two Computer Science and Engineering students at TU/e, made for the course Introduction to Programming (30% of the final grade). It is built with Java Swing and Maven.

This project is a TypeRacer clone for programmers, made so that programmers can practice their manual code creation speed, since due to AI a lot of code is automated. This application will allow the user to gain back their coding speed with enough practice.

Instead of normal sentences, the player races to type real lines of code, which are a mix of real LeetCode solutions and other code found on the internet. Each race shows one random code snippet, and at the end the game reports the player's WPM, accuracy and number of mistakes.

**Requirements**

1. Java JDK 21 or newer (the project is compiled with maven.compiler.release 21).
2. Apache Maven 3.9 or newer (NetBeans bundles its own Maven, so it is not needed if you only use NetBeans).
3. Git (only needed to clone the repository).

**Dependencies**

No manual installation is needed for these:

1. Gson 2.8.6: Reads the code snippets from DB.json and is downloaded automatically by Maven/NetBeans.
2. AbsoluteLayout: Layout used by the GUI forms and bundled in the repository's lib/ folder.

**Getting the Code**

In your terminal:

git clone https://github[.]com/marilou125/CBL.git

cd CBL

**Building**

**1. With Maven**

From the project root (the folder that contains pom.xml):

mvn clean compile

**2. With NetBeans**

- Choose File → Open Project and select the project folder.
- Right-click the project and choose Clean and Build.
- The Output window should end with BUILD SUCCESS.

**Running**

**1. With Maven**

mvn exec:java

**2. With NetBeans**

- Right-click TyperRacerApp.java and choose Run File (or press Shift+F6).
- The entry point of the application is the class TyperRacerApp.
- The game window opens with a background image and a Start New Race button in the middle.

**Adding Your Own Snippets or Removing Snippets**

Open src/main/resources/ and navigate to where you want to add the snippet. Add an entry to the list in this example form:

{ "body": "for (int i = 0; i < 10; i++) {" }
