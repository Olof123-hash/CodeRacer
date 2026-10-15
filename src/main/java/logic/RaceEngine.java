package logic;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author maril
 */
public class RaceEngine {
    //All the fields I use in the methods
    private String typedsnip = "";
    private final String targetText;
    private long startTime;
    private long endTime;
    private int keystrokes = 0;
    private int mistakes = 0;
  //I use an enum for the race state because I only want it to take the three values and not any other, which would happend if i used a string
    public enum RaceState {
        NOT_STARTED, RUNNING, FINISHED;
    }
    private RaceState currentState = RaceState.NOT_STARTED;
    //this below is the constructor 
    public RaceEngine(String snippetText) {
        this.targetText = snippetText;
    }
    /* the method below will be used every time the user changes his typed snippet, which means either he types a character of he deletes one
       It returns true, if the character he typed is the same as the character from the target text, which is the code snippet
       It also returns true, if he backspaced, because he couldnt move one if the previous text wasnt correct, so deleting a character returns true
      Also returns true if the typed snip is empty
       It returns false if the last character is not the same as the one in the target text or if the typed snip is longer in characters than the target snip
       We will use this to determine the colors of the typed letters (green or red) and it also stops the user of continuing to write if he has typed something wrong
    */
    public boolean updateTypedSnip(String newTypedSnip) {
        if (currentState == RaceState.FINISHED) {
            return false;
        }
        if (newTypedSnip.isEmpty()) {
            typedsnip = newTypedSnip;
            return true;
        }
        if (newTypedSnip.length() > this.targetText.length()) {
            return false;
        }
        if (!(newTypedSnip.substring(0, newTypedSnip.length() - 1).equals(this.targetText.substring(0, newTypedSnip.length() - 1)))) {
            return false;
        }
        if (newTypedSnip.length() == typedsnip.length() + 1) {
            keystrokes = keystrokes + 1;
            if (newTypedSnip.charAt(newTypedSnip.length() - 1) != this.targetText.charAt(newTypedSnip.length() - 1)) {
                mistakes = mistakes + 1;
            }
        }
        typedsnip = newTypedSnip;
        if (currentState == RaceState.NOT_STARTED) {
            startTime = System.currentTimeMillis();
            currentState = RaceState.RUNNING;
        }
        if (typedsnip.length() == this.targetText.length() && typedsnip.charAt(typedsnip.length() - 1) == this.targetText.charAt(this.targetText.length() - 1)) {
            currentState = RaceState.FINISHED;
            endTime = System.currentTimeMillis();
            return true;
        }
        return true;
    }
    public String getLastAcceptedText(){
        return typedsnip;
    }
 /*   The timer method will e used to count the typing time of the user
    if the race has started (so the race state is equal to running) then it takes the start time (which I initialized when the race started in the previous method)
    and it subtracks it from the current time. 
    We will use that to display a timer while the game is running.
    If the game is finished (so the race state is equal to finished), it subtracks the start time from the endtime and we have the total time of the race
    If neither are true, so the game hasnt started yet, it returns 0
    */
    public long timer() {
        if (currentState == RaceState.RUNNING) {
            long estimatedTime = System.currentTimeMillis() - startTime;
            return estimatedTime;
        } else if (currentState == RaceState.FINISHED) {
            long estimatedTime = endTime - startTime;
            return estimatedTime;
        } else {
            return 0;
        }
    }
/* The comparison method is used to check whether or  not the character the user typed matches the equivalent character of the target snippet
    if it does match it, it is an accepted typed snip and the mthod returns true
    if it doesnt, it is a rejected typed snip nd it returns false
    This wil be used for the colors and the accuracy percentage 
    */
    public boolean comparison(int charposition) {
        boolean isCorrect = false;
        if (typedsnip.length() <= targetText.length()) {
            if (charposition < typedsnip.length()) {
                char[] targetTextArr = this.targetText.toCharArray();
                char currentchar = typedsnip.charAt(charposition);
                if (currentchar == targetTextArr[charposition]) {
                    isCorrect = true;
                }
            }
        }
        return isCorrect;
    }
//just a getter for the state 
    public RaceState getState() {
        return currentState;
    }
//just a getter for the number keystrokes (used for the accuracy percentage)
    public int getKeystrokes() {
        return keystrokes;
    }
//just a getter for the number of mistakes (used for the accuracy percentage)
    public int getMistakes() {
        return mistakes;
    }
// this is ta getter for the accuracy perfentage which is one of the stats we will display at the end of the game(it uses the number of keystrokes and mistakes)
    public double getAccuracy() {
        if (keystrokes == 0) {
            return 100;
        }
        double keystrokesd = keystrokes;
        double percentage = 100 - (mistakes / keystrokesd) * 100;
        return percentage;
    }
//Another gatter for the wpm. It uses the timer method to get the time. Nothing special i think you can understand this 
    public double getWPM() {
        long milliseconds = timer();
        double minutes = milliseconds / 60000.0;
        double words;
        if (milliseconds == 0 || typedsnip.isEmpty()) {
            return 0;
        }
        boolean isCorrect = comparison(typedsnip.length() - 1);
        if (isCorrect) {
            words = typedsnip.length() / 5.0;
        } else {
            words = (typedsnip.length() - 1) / 5.0;
        }
        return words / minutes;
    }

}