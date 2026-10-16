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
import javax.swing.text.DefaultStyledDocument;
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
   
     public enum PanelState {
        NOT_STARTED, RUNNING, FINISHED;
    }
    private PanelState currentPanel = PanelState.NOT_STARTED;
    /**
     * Creates new form RacePanel
     */
    
    //Initialize classes
    
    Database db = new Database();
    private RaceEngine currentRace;
    
    public RacePanel() {
    //Built in java code
    initComponents();
    
    typingField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
    typingField.setTransferHandler(null);
    typingField.getDocument().addDocumentListener(new MyDocumentListener());
    //Calls racemode
    
    setRaceMode();
}
    //Method that handles what is visible on the UI
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
    newRaceButton.setVisible(currentPanel == PanelState.FINISHED || currentPanel == PanelState.NOT_STARTED);
    //This is always going to be false no matter if the user has pressed the play button or not, this is only visible when the user has ended a game so before pressing the play button again
    //GameOverField.setVisible(currentRace.currentState == RaceEngine.RaceState.FINNISHED);
    }
       //Creates all classes when this is ran
        private void newGame () {
        // Calls the Snippet class and initializes it with a random snippet from the Database class. 
        Snippet snip= db.getRandomSnippet();
        //Creates a RaceEngine object by calling the RaceEngine constructor with the snippet from the previous lines.
        currentRace = new RaceEngine(snip.getBody());
        //Sets the text of the snippetPane with that of the current snippet
        snippetPane.setText(snip.getBody());
        
        //Sets the state to be running
        currentPanel = PanelState.RUNNING;
        setRaceMode();
    }
    
    private void gameOver () {
       currentPanel = PanelState.FINISHED;
       typingField.setText("");
       setRaceMode();
       //GameOverField.setVisible(true)
    }
    /* We need to use the class below because a document listener is the only way for the program to understand the changes in the text the user types
    
    */
 
    enum gameState {
        NOT_STARTED, PLAYING, FINNISHED
    }
    class MyDocumentListener implements DocumentListener {
        
        private boolean isAccepted;

        private void update(DocumentEvent e) {
          
           if (currentPanel == PanelState.RUNNING) {
             
            try {
                
                String typedsnip = typingField.getText(0, e.getDocument().getLength());
                // calls a method in RaceEngine that returns true or false
                isAccepted = currentRace.updateTypedSnip(typedsnip);
                
                
               //research what this does!
               SwingUtilities.invokeLater(() -> {
                   
                       wpmLabel.setText("WPM" + " " +  currentRace.getWPM());
                       accuracyLabel.setText("Accuracy" + " " + currentRace.getAccuracy());
                       mistakesLabel.setText("Mistakes" + " " + currentRace.getMistakes());
                   
                   if (currentRace.getState() == RaceEngine.RaceState.FINISHED) {
                    gameOver();
                    //Updates the (currently invisible) text area thats shown after the game with the statistics
                    updateText(currentRace.getWPM(), currentRace.getKeystrokes(), currentRace.getMistakes(), currentRace.getAccuracy());
                }
                   
                if (!isAccepted) {
                // Calls a method in RaceEnginge that returns the typedsnip, so the text of the typingfield is now the typedSnip
                typingField.setText(currentRace.getLastAcceptedText());
                
                // From Claude --> This is Java Swing code that moves the text cursor (caret) to the very end of a text field.
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
    public void colorTypingField(){
        StyledDocument doc = typingField.getStyledDocument();
        if (currentRace.comparison(typingField.getText().length())){
        javax.swing.text.Style style = typingField.addStyle("Green", null);
        doc.setCharacterAttributes(typingField.getText().length()-1, typingField.getText().length(), typingField.getStyle("Green"), true); 
        }else{
         javax.swing.text.Style style = typingField.addStyle("Red", null);
         doc.setCharacterAttributes(typingField.getText().length()-1, typingField.getText().length(), typingField.getStyle("Red"), true); 
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(186, 186, 186)
                        .addComponent(newRaceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(217, 217, 217)
                        .addComponent(endRaceButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(102, 102, 102)
                        .addComponent(wpmLabel)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addComponent(accuracyLabel)
                                .addGap(62, 62, 62)
                                .addComponent(mistakesLabel))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(snippetPan, javax.swing.GroupLayout.PREFERRED_SIZE, 786, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(typingPane, javax.swing.GroupLayout.PREFERRED_SIZE, 974, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(689, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wpmLabel)
                    .addComponent(accuracyLabel)
                    .addComponent(mistakesLabel))
                .addGap(160, 160, 160)
                .addComponent(snippetPan, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(457, 457, 457)
                .addComponent(endRaceButton)
                .addGap(18, 18, 18)
                .addComponent(typingPane, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75)
                .addComponent(newRaceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    //Adds the statistics to the text area shown after the user types the last character
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
    private javax.swing.JLabel mistakesLabel;
    private javax.swing.JButton newRaceButton;
    private javax.swing.JScrollPane snippetPan;
    private javax.swing.JTextPane snippetPane;
    private javax.swing.JTextPane typingField;
    private javax.swing.JScrollPane typingPane;
    private javax.swing.JLabel wpmLabel;
    // End of variables declaration//GEN-END:variables
}
