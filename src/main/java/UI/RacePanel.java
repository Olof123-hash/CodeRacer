package UI;
import database.Database;
import logic.RaceEngine;
import model.Snippet;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.BorderLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;
import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.DefaultStyledDocument;
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
public class RacePanel extends javax.swing.JPanel {
    
    
    
   
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
    
    //DocumentListener shit !Very important code
    typingField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
    typingField.setTransferHandler(null);
    typingField.getDocument().addDocumentListener(new MyDocumentListener());
    
    //Calls racemode which is responsible for whats visible on the ui
    setRaceMode();
}
    //The dictator of the ui
    private void setRaceMode () {
    //Kind of obvious code, makes buttons and fields visible or invisible whether or not the user has pressed the play button.
    snippetPane.setVisible(currentPanel == PanelState.RUNNING);
    typingField.setVisible(currentPanel == PanelState.RUNNING );
    snippetPan.setVisible(currentPanel == PanelState.RUNNING);
    typingPane.setVisible(currentPanel == PanelState.RUNNING);
    endRaceButton.setVisible(currentPanel == PanelState.RUNNING );
    wpmLabel.setVisible(currentPanel == PanelState.RUNNING);
    accuracyLabel.setVisible(currentPanel == PanelState.RUNNING);
    mistakesLabel.setVisible(currentPanel == PanelState.RUNNING);     
    jLabel1.setVisible(currentPanel == PanelState.RUNNING);
    newRaceButton.setVisible(currentPanel == PanelState.FINISHED || currentPanel == PanelState.NOT_STARTED);
    
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
    private void newGame () {
        // Calls the Snippet class and initializes it with a random snippet from the Database class. 
        Snippet snip= db.getRandomSnippet();
        //Creates a RaceEngine object by calling the RaceEngine constructor with the snippet from the previous lines.
        currentRace = new RaceEngine(snip.getBody());
        //Sets the text of the snippetPane with that of the current snippet
        snippetPane.setText(snip.getBody());
        //This calls the function that is responsible for showing statistics on the screen
        startUiTimer();
        
        //Sets the state to be running
        currentPanel = PanelState.RUNNING;
        setRaceMode();
        
       // Closes the game over window if it exists
       if (overWindow != null) {
           overWindow.dispose();
       }
    }
        
    private void gameOver () {
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
        wpmLabel.setText("WPM" + " " +  currentRace.getWPM());
        }
    }
    
    //Same here
    private void updateLabels() {
        if (currentPanel == PanelState.RUNNING) {
        jLabel1.setText("Time" + " " + currentRace.getTime());
        accuracyLabel.setText("Accuracy" + " " + currentRace.getAccuracy());
        mistakesLabel.setText("Mistakes" + " " + currentRace.getMistakes());
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
    
    //Method that colours the text 
    private void colorTypingField(){
        String acceptedText =currentRace.getLastAcceptedText();
        StyledDocument doc = typingField.getStyledDocument();
        SimpleAttributeSet greyText = new SimpleAttributeSet();
        StyleConstants.setForeground(greyText,Color.LIGHT_GRAY);
        doc.setCharacterAttributes(0, acceptedText.length(), greyText, true);
        if (!acceptedText.isEmpty() && currentRace.getState()!= RaceState.FINISHED){
            int thatPosition= acceptedText.length()-1;
        if (currentRace.comparison(thatPosition)){
        SimpleAttributeSet greenText = new SimpleAttributeSet();
        StyleConstants.setForeground(greenText, Color.GREEN);
        doc.setCharacterAttributes(thatPosition, 1,greenText , true); 
        }else{
         SimpleAttributeSet redText = new SimpleAttributeSet();
        StyleConstants.setForeground(redText, Color.RED);
         doc.setCharacterAttributes(thatPosition, 1, redText, true); 
        }
        }
    }
    
     public static void main(String[] args){
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

        snippetPane.setEditable(false);
        snippetPane.setFont(new java.awt.Font("Courier New", 0, 18)); // NOI18N
        snippetPan.setViewportView(snippetPane);

        newRaceButton.setText("Start New Race");
        newRaceButton.addActionListener(this::newRaceButtonActionPerformed);

        typingPane.setViewportView(typingField);

        wpmLabel.setText("WPM");

        accuracyLabel.setText("Time");

        mistakesLabel.setText("Accuracy");

        endRaceButton.setText("End Race");
        endRaceButton.addActionListener(this::endRaceButtonActionPerformed);

        jLabel1.setText("Time");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(endRaceButton)
                .addGap(800, 800, 800))
            .addGroup(layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addComponent(wpmLabel)
                .addGap(58, 58, 58)
                .addComponent(accuracyLabel)
                .addGap(62, 62, 62)
                .addComponent(mistakesLabel)
                .addGap(42, 42, 42)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(900, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(snippetPan, javax.swing.GroupLayout.PREFERRED_SIZE, 786, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(420, 420, 420))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(typingPane, javax.swing.GroupLayout.PREFERRED_SIZE, 974, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(330, 330, 330))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(newRaceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(888, 888, 888))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wpmLabel)
                    .addComponent(accuracyLabel)
                    .addComponent(mistakesLabel)
                    .addComponent(jLabel1))
                .addGap(260, 260, 260)
                .addComponent(snippetPan, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(typingPane, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66)
                .addComponent(newRaceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(174, 174, 174)
                .addComponent(endRaceButton)
                .addContainerGap(203, Short.MAX_VALUE))
        );
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
    void updateText(String wpm,String ks, String m, String a) {

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
