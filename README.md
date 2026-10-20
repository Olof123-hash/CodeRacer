# CBL

This is a collaborative project between two authors who study Computer Science and Engineering at TU/e. The goal of this project is to create a Type Racer clone but for programming language specific words and text snippets. This project is a part of the course introduction to programming, worth 30% of our final grade.



Inside our zip, other than the Readme.md, the Advanced topics review and the Product backlog, our runnable project will be found in a folder  under the name TypeRacerApp. Opening the folder in Neatbeans will show all of our files. 



Under Source Packages, the folders: model, UI, logic and database will be found. In the folder UI, 3 java classes exist. 

&#x20; BackgroundPanel.java is used to upload an image as the background of our application. The image used is AI generated and it is only used for styling purposes. 

&#x20; GameOver.java, is the graphical user interface that displays the statistics of a game once it is over. 

&#x20; Lastly, RacePanel.java is the main class that contains the game user interface. It is the file that should be run for the application to work. It uses both BackgroundPanel and GameOver. 

&#x20; RacePanel also uses RaceEngine.java, that is located in the logic folder  and it contains the functionality of the game. Inside RaceEngine, the program compares the typed text of the user with the target text and it returns whether the user has typed right or not, it also initiates the timer and all the statistics of the game (WPM, Accuracy%, Mistakes), it also uses a bunch of getters to return values in RacePanel. 

&#x20; RacePanel also uses Database.java, located under the database folder. This class uses a Gson to read our coding snippets from our Json file, which I analyse further later. It selects one of the snippets randomly and returns it to RacePanel to use. 

&#x20; Lastly, in order to get an object snippet and turn it into a string we use Snippet.java, located in the model folder

&#x20; 

&#x20;   One of our advanced topics was the use of Databases.Thas why under Other Sources in the resources folder, a DB.json file can be found. This file contains all of our coding snippets, which then Database.java uses and with the help of the Gson, selects one randomly.

&#x20;  Also, in the same folder, we have the background image that is the reason we use  BackgroundPanel in the UI folder.

&#x20;  Lastly we find 





The program is run by the RacePanel.java file. After right clicking on the file and clicking Run File, the UI should appear with out background and a Start New Race Button in the middle. Clicking that button starts our game.



