package UI;
import database.Database;
import logic.RaceEngine;
import model.Snippet;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;
import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import logic.RaceEngine.RaceState;
import javax.swing.text.StyleConstants;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
/**
 *
 * @author maril
 */
//TODO CHANGE THE NAMES OF ALL BUTTONS AND CONTAINERS ETC ETC 
public class RacePanel extends BackgroundPanel {

    private final double positionx = 0.10;
    private final double positiony = 0.271429;
    private final double width = 0.666667;
    private final double height = 0.142857;
    private final double positiony1 = 0.457143;
    private final double height2 = 0.071429;
//Enumerator to handle the 3 different states that the player can be in

    public enum PanelState {
        NOT_STARTED, RUNNING, FINISHED;
    }
    //Sets the current panel state at the very start of the application being launched to not_started
    private PanelState currentPanel = PanelState.NOT_STARTED;
    //Initializes the game over screen
    private GameOver overWindow;
    //Initializes the two methods responsible for the refreshing of the values when we dynamically display the statistics 
    private javax.swing.Timer uiTimer;
    private javax.swing.Timer uiTimer2;
    //Initialize classes
    Database db = new Database();
    public RaceEngine currentRace;

    //Constructor
    public RacePanel() {
        //Built in java code
        initComponents();
        //display of end race button
        endRaceButton.setOpaque(false);
        endRaceButton.setBorder(null);
        endRaceButton.setBorderPainted(false);
        endRaceButton.setContentAreaFilled(true);
        endRaceButton.setBackground(Color.BLACK);
        endRaceButton.setForeground(Color.RED);
        //display of stats labels
        wpmLabel.setForeground(Color.WHITE);
        accuracyLabel.setForeground(Color.WHITE);
        mistakesLabel.setForeground(Color.WHITE);
        jLabel1.setForeground(Color.WHITE);
        //dispay of snippetPane
        snippetPane.setOpaque(false);
        snippetPane.setBorder(null);
        snippetPan.setOpaque(false);
        snippetPan.getViewport().setOpaque(false);
        snippetPan.setBorder(null);
        snippetPane.setForeground(Color.ORANGE);
        //display of typingField 
        typingField.setOpaque(false);
        typingField.setBorder(null);
        typingPane.setOpaque(false);
        typingPane.getViewport().setOpaque(false);
        typingPane.setBorder(null);
        typingField.setCaretColor(Color.PINK);
        //display of startbutton
        newRaceButton.setFocusPainted(false);
        newRaceButton.setOpaque(false);
        newRaceButton.setBorderPainted(false);
        newRaceButton.setContentAreaFilled(true);
        newRaceButton.setBackground(Color.BLACK);
        newRaceButton.setForeground(Color.getHSBColor(0.90f, 1.0f, 1.0f));
        //DocumentListener shit !Very important code
        
        typingField.setTransferHandler(null);
        typingField.getDocument().addDocumentListener(new MyDocumentListener());

        //Calls racemode which is responsible for whats visible on the ui
        setRaceMode();
    }

    //The dictator of the ui
    private void setRaceMode() {
        //Kind of obvious code, makes buttons and fields visible or invisible whether or not the user has pressed the play button.
        snippetPane.setVisible(currentPanel == PanelState.RUNNING);
        typingField.setVisible(currentPanel == PanelState.RUNNING);
        snippetPan.setVisible(currentPanel == PanelState.RUNNING);
        typingPane.setVisible(currentPanel == PanelState.RUNNING);
        endRaceButton.setVisible(currentPanel == PanelState.RUNNING);
        wpmLabel.setVisible(currentPanel == PanelState.RUNNING);
        accuracyLabel.setVisible(currentPanel == PanelState.RUNNING);
        mistakesLabel.setVisible(currentPanel == PanelState.RUNNING);
        jLabel1.setVisible(currentPanel == PanelState.RUNNING);
        newRaceButton.setVisible(currentPanel == PanelState.FINISHED || currentPanel == PanelState.NOT_STARTED);
        boolean name = currentPanel == PanelState.NOT_STARTED;
        if (name) {
            setImage();
        }
        revalidate();
        //This is always going to be false no matter if the user has pressed the play button or not, this is only visible when the user has ended a game so before pressing the play button again
        //GameOverField.setVisible(currentRace.currentState == RaceEngine.RaceState.FINNISHED);
    }

    //This function is responsible for showing the statistics on screen, or more precisely this function is responsible for the refreshing of the functions that show the statistics on the screen
    private void startUiTimer() {
        uiTimer = new javax.swing.Timer(100, e -> updateLabels());
        uiTimer2 = new javax.swing.Timer(500, e -> updateWPM());

        uiTimer2.start();// every second
        uiTimer.start(); // every 5 seconds but currently has some bugs or idk it feels like it refreshes faster thane very 5s
    }

    //is ran when the user presses the start button
    private void newGame() {
        // Calls the Snippet class and initializes it with a random snippet from the Database class. 
        Snippet snip = db.getRandomSnippet();
        //Creates a RaceEngine object by calling the RaceEngine constructor with the snippet from the previous lines.
        currentRace = new RaceEngine(snip.getBody());
        //Sets the text of the snippetPane with that of the current snippet
        snippetPane.setText(snip.getBody());
        //This calls the function that is responsible for showing statistics on the screen
        startUiTimer();

        //Sets the state to be running
        currentPanel = PanelState.RUNNING;
        setRaceMode();

        //this line makes the player able to type immediately without having to click in the typing area
        typingField.requestFocusInWindow();

        // Closes the game over window if it exists
        if (overWindow != null) {
            overWindow.dispose();
        }
    }

    private void gameOver() {
        //changes the state of the race and calls the ui dictator
        currentPanel = PanelState.FINISHED;
        //removes the typed text from the field
        typingField.setText("");
        setRaceMode();

        //makes the game over window visible with statistics
        overWindow = new GameOver(currentRace);
        overWindow.setVisible(true);

        uiTimer.stop();
        uiTimer2.stop();
        //GameOverField.setVisible(true)
    }

    //Simple function that just uses the getter in RaceEnginge
    private void updateWPM() {
        if (currentPanel == PanelState.RUNNING) {
            wpmLabel.setText("WPM" + " " + currentRace.getWPM());
        }
    }

    //Same here
    private void updateLabels() {
        if (currentPanel == PanelState.RUNNING) {
            jLabel1.setText("Time" + " " + currentRace.getTime());
            mistakesLabel.setText("Mistakes" + " " + currentRace.getMistakes());
            accuracyLabel.setText("Accuracy" + " " + currentRace.getAccuracy() + "%");
        }
    }

    /* We need to use the class below because a document listener is the only way for the program to understand the changes in the text the user types
     */
    //Heart of the code but this is familiar to you already
    class MyDocumentListener implements DocumentListener {

        private void update(DocumentEvent e) {

            if (currentPanel == PanelState.RUNNING) {

                try {

                    String typedsnip = typingField.getText(0, e.getDocument().getLength());
                    // calls a method in RaceEngine that returns true or false
                    boolean isAccepted = currentRace.updateTypedSnip(typedsnip);

                    //research what this does!
                    SwingUtilities.invokeLater(() -> {

                        if (currentRace.getState() == RaceEngine.RaceState.FINISHED) {
                            colorTypingField();
                            gameOver();

                            //updateText(currentRace.getWPM(), currentRace.getKeystrokes(), currentRace.getMistakes(), currentRace.getAccuracy());
                        }

                        if (!isAccepted) {
                            // Calls a method in RaceEnginge that returns the typedsnip, so the text of the typingfield is now the typedSnip
                            typingField.setText(currentRace.getLastAcceptedText());

                            // This is Java Swing code that moves the text cursor (caret) to the very end of a text field.
                            typingField.setCaretPosition(typingField.getText().length());
                        }
                        // calls the method that colours the text
                        colorTypingField();
                    });

                } catch (BadLocationException ex) {
                    Logger.getLogger(MyDocumentListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            update(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            update(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    }

    @Override
    public void doLayout() {
        super.doLayout(); // let the designer layout do its normal work first
        if (currentPanel != PanelState.NOT_STARTED && currentPanel != PanelState.FINISHED) {
            int x = (int) (getWidth() * positionx);
            int y = (int) (getHeight() * positiony);
            int width1 = (int) (width * getWidth());
            int height1 = (int) (height * getHeight());
            snippetPan.setBounds(x, y, width1, height1);

            x = (int) (getWidth() * positionx);
            y = (int) (getHeight() * positiony1);
            width1 = (int) (width * getWidth());
            height1 = (int) (height2 * getHeight());
            typingPane.setBounds(x, y, width1, height1);
        }
        if (currentPanel != PanelState.RUNNING) {
            int x = (getWidth() - newRaceButton.getWidth()) / 2;
            int y = (getHeight() - newRaceButton.getHeight()) / 2;
            newRaceButton.setLocation(x, y);
        }
    }

    //Method that colours the text 
    private void colorTypingField() {
        String acceptedText = currentRace.getLastAcceptedText();
        StyledDocument doc = typingField.getStyledDocument();
        SimpleAttributeSet greyText = new SimpleAttributeSet();
        StyleConstants.setForeground(greyText, Color.LIGHT_GRAY);
        doc.setCharacterAttributes(0, acceptedText.length(), greyText, true);
        if (!acceptedText.isEmpty() && currentRace.getState() != RaceState.FINISHED) {
            int thatPosition = acceptedText.length() - 1;
            if (currentRace.comparison(thatPosition)) {
                SimpleAttributeSet greenText = new SimpleAttributeSet();
                StyleConstants.setForeground(greenText, Color.GREEN);
                doc.setCharacterAttributes(thatPosition, 1, greenText, true);
            } else {
                SimpleAttributeSet redText = new SimpleAttributeSet();
                StyleConstants.setForeground(redText, Color.RED);
                doc.setCharacterAttributes(thatPosition, 1, redText, true);
            }
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.JFrame frame = new javax.swing.JFrame("RacePanel test");
            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            frame.add(new RacePanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        snippetPan = new javax.swing.JScrollPane();
        snippetPane = new javax.swing.JTextPane();
        newRaceButton = new javax.swing.JButton();
        typingPane = new javax.swing.JScrollPane();
        typingField = new javax.swing.JTextPane();
        wpmLabel = new javax.swing.JLabel();
        accuracyLabel = new javax.swing.JLabel();
        mistakesLabel = new javax.swing.JLabel();
        endRaceButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setForeground(new java.awt.Color(0, 0, 0));
        setFont(new java.awt.Font("Courier New", 0, 14)); // NOI18N
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        snippetPane.setEditable(false);
        snippetPane.setFont(new java.awt.Font("Consolas", 0, 28)); // NOI18N
        snippetPan.setViewportView(snippetPane);

        add(snippetPan, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 190, 800, 100));

        newRaceButton.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        newRaceButton.setText("Start New Race");
        newRaceButton.setAlignmentX(0.5F);
        newRaceButton.addActionListener(this::newRaceButtonActionPerformed);
        add(newRaceButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 270, -1, 51));

        typingField.setFont(new java.awt.Font("Consolas", 0, 24)); // NOI18N
        typingPane.setViewportView(typingField);

        add(typingPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 320, 800, 50));

        wpmLabel.setFont(new java.awt.Font("Perpetua Titling MT", 1, 18)); // NOI18N
        wpmLabel.setText("WPM");
        add(wpmLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(102, 62, -1, -1));

        accuracyLabel.setFont(new java.awt.Font("Perpetua Titling MT", 1, 18)); // NOI18N
        accuracyLabel.setText("Time");
        add(accuracyLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, -1, -1));

        mistakesLabel.setFont(new java.awt.Font("Perpetua Titling MT", 1, 18)); // NOI18N
        mistakesLabel.setText("Accuracy");
        add(mistakesLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 60, -1, -1));

        endRaceButton.setFont(new java.awt.Font("Consolas", 1, 18)); // NOI18N
        endRaceButton.setText("End Race");
        endRaceButton.addActionListener(this::endRaceButtonActionPerformed);
        add(endRaceButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 460, -1, -1));

        jLabel1.setFont(new java.awt.Font("Perpetua Titling MT", 1, 18)); // NOI18N
        jLabel1.setText("Time");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 60, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    //Start button is pressed
    private void newRaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newRaceButtonActionPerformed
        newGame();
    }//GEN-LAST:event_newRaceButtonActionPerformed

    // End Game button is pressed
    private void endRaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endRaceButtonActionPerformed
        gameOver();
    }//GEN-LAST:event_endRaceButtonActionPerformed

    //Right now does nothing since we dont have this textarea anymore, i kept it here because its easy to copy this method it acts as a good building block
    void updateText(String wpm, String ks, String m, String a) {

        StringBuilder finalString = new StringBuilder();

        finalString.append("WPM: ").append(wpm).append(" ");
        finalString.append("Keystrokes: ").append(ks).append(" ");
        finalString.append("Accuracy: ").append(a).append(" ");
        finalString.append("Mistakes: ").append(m);

        //GameOverField.setText(finalString.toString());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accuracyLabel;
    private javax.swing.JButton endRaceButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel mistakesLabel;
    private javax.swing.JButton newRaceButton;
    private javax.swing.JScrollPane snippetPan;
    private javax.swing.JTextPane snippetPane;
    private javax.swing.JTextPane typingField;
    private javax.swing.JScrollPane typingPane;
    private javax.swing.JLabel wpmLabel;
    // End of variables declaration//GEN-END:variables
}
