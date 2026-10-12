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

    private String typedsnip = "";
    private final String targetText;
    private long startTime;
    private long endTime;
    private int keystrokes = 0;
    private int mistakes = 0;

    public enum RaceState {
        NOT_STARTED, RUNNING, FINISHED;
    }
    private RaceState currentState = RaceState.NOT_STARTED;

    public RaceEngine(String snippetText) {
        this.targetText = snippetText;
    }

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

    public boolean comparison(int charposition) {
        boolean isCorrect = false;
        if (!typedsnip.isEmpty() && typedsnip.length() <= targetText.length()) {
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

    public RaceState getState() {
        return currentState;
    }
    public int getKeystrokes(){
        return keystrokes;
    }
    public int getMistakes(){
        return mistakes;
    }
    public double getAccuracy() {
        if (keystrokes == 0) {
            return 100;
        }
        double keystrokesd= keystrokes;
        double percentage = 100 - (mistakes / keystrokesd) * 100;
        return percentage;
    }

        public static void main(String[] args) throws InterruptedException {
    RaceEngine engine = new RaceEngine("Java is great");

    // Fresh engine, nothing typed
    System.out.println("Fresh accuracy: " + engine.getAccuracy() + " (expect -1)");

    // The counter table
    String[] inputs = {"J", "Jx", "Jxa", "J", "Ja", "Ja", ""};
    for (String input : inputs) {
        boolean accepted = engine.updateTypedSnip(input);
        System.out.println("\"" + input + "\" -> returns " + accepted
                + ", keystrokes = " + engine.getKeystrokes()
                + ", mistakes = " + engine.getMistakes());
    }
    System.out.println("Accuracy: " + engine.getAccuracy() + " (expect about 66.7)");

    // Full correct run on a fresh engine
    RaceEngine run = new RaceEngine("Java is great");
    String target = "Java is great";
    for (int i = 1; i <= target.length(); i++) {
        run.updateTypedSnip(target.substring(0, i));
        Thread.sleep(100);
    }
    System.out.println("Full run: " + run.getState() + " (expect FINISHED)"
            + ", accuracy = " + run.getAccuracy() + " (expect 100.0)"
            + ", time = " + run.timer() + " (expect about 1300)");

    // One-character target finishes on the first call
    RaceEngine tiny = new RaceEngine("x");
    tiny.updateTypedSnip("x");
    System.out.println("One-char target: " + tiny.getState() + " (expect FINISHED)");

    // Input after finish is rejected
    System.out.println("After finish: " + run.updateTypedSnip("Java is great!") + " (expect false)");
}
    
}
