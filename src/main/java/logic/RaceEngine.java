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
    //new TypedSnip is got from the isAccepted = currentRace.updateTypedSnip(typedsnip);
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
        //The user added text. Normally this is exactly one typed character, but Enter can add the newline and
        //the indentation of the next line together. That whole block still counts as one keystroke.
        int addedChars = newTypedSnip.length() - typedsnip.length();
        if (addedChars == 1) {
            keystrokes = keystrokes + 1;
            if (newTypedSnip.charAt(newTypedSnip.length() - 1) != this.targetText.charAt(newTypedSnip.length() - 1)) {
                mistakes = mistakes + 1;
            }
        } else if (addedChars > 1 && isAutoIndentBlock(newTypedSnip, typedsnip.length())) {
            //The newline and the indentation are copied from the target text, so they can not contain a mistake
            keystrokes = keystrokes + 1;
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

    public String getLastAcceptedText() {
        return typedsnip;
    }

    /* When the user presses Enter at the end of a line, the newline alone is not enough: the next line of a snippet
       usually starts with indentation (spaces or tabs) that the user would then have to type as well. In a code editor
       that indentation is added automatically, so this method returns the newline together with the leading whitespace
       of the next line. That way the caret ends up on the first character of the next line instead of on its indentation.
       It returns null when Enter should just insert a normal newline, for example when the user is not at the end of a line.
     */
    public String getEnterInsertion() {
        if (currentState == RaceState.FINISHED) {
            return null;
        }
        //Only auto indent when everything typed so far is correct and there is still text left
        if (typedsnip.length() >= targetText.length() || !targetText.startsWith(typedsnip)) {
            return null;
        }
        //The next character has to be a newline, otherwise the user is not at the end of the line
        if (targetText.charAt(typedsnip.length()) != '\n') {
            return null;
        }
        //Take the newline and all the spaces/tabs that start the next line
        int end = typedsnip.length() + 1;
        while (end < targetText.length()
                && (targetText.charAt(end) == ' ' || targetText.charAt(end) == '\t')) {
            end++;
        }
        return targetText.substring(typedsnip.length(), end);
    }

    /* Checks whether the text that was added at startPosition is the block that getEnterInsertion returns:
       a newline followed only by spaces and tabs. Such a block is inserted as one piece but typed with one key press.
     */
    private boolean isAutoIndentBlock(String text, int startPosition) {
        if (startPosition >= text.length() || text.charAt(startPosition) != '\n') {
            return false;
        }
        for (int i = startPosition + 1; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c != ' ' && c != '\t') {
                return false;
            }
        }
        return true;
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
        }

        if (currentState == RaceState.FINISHED) {
            long estimatedTime = endTime - startTime;

            return estimatedTime;
        }

        return 0;

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

    //Will return the integer version
    public long getTime() {
        return ((int) timer() / 1000);
    }

    public RaceState getState() {
        return currentState;
    }
//just a getter for the number keystrokes (used for the accuracy percentage)

    public int getKeystrokes() {
        return (keystrokes);
    }
//just a getter for the number of mistakes (used for the mistakes counter)

    public int getMistakes() {
        return (mistakes);
    }

    public double getAccuracy() {

        double keystrokesM = getKeystrokes();
        double mistakes = getMistakes();

        if (keystrokesM == 0) {
            return 0;
        }

        return Math.round((100 - (mistakes / keystrokesM) * 100));

    }

//Another gatter for the wpm. It uses the timer method to get the time. Nothing special i think you can understand this 
    public long getWPM() {
        long milliseconds = timer();

        long words;
        if (milliseconds == 0 || typedsnip.isEmpty()) {
            return 0;
        }
        boolean isCorrect = comparison(typedsnip.length() - 1);
        if (isCorrect) {
            words = (long) ((typedsnip.length() / 5) / (milliseconds / 60000.0));
        } else {
            words = (long) ((long) ((typedsnip.length() - 1) / 5) / (milliseconds / 60000.0));
        }

        return words;
    }

}
