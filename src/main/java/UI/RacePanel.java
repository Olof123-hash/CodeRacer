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
public class RacePanel extends javax.swing.JPanel {
    
    boolean isOn = false;
    

    /**
     * Creates new form RacePanel
     */
    
    //Initialize classes
    private Database db;
    private RaceEngine currentRace;
    
    public RacePanel() {
    //Built in java code
    initComponents();
    //Calls racemode
    setRaceMode(isOn);
     }
    
    //Creates all classes when this is ran
    private void newGame () {
       
        //Creates Database object
        db = new Database();
        // Calls the Snippet class and initializes it with a random snippet from the Database class. 
        Snippet snip= db.getRandomSnippet();
        //Creates a RaceEngine object by calling the RaceEngine constructor with the snippet from the previous lines.
        currentRace = new RaceEngine(snip.getBody());
        
        
        //Sets the text of the snippetPane with that of the current snippet
        snippetPane.setText(snip.getBody());
        
        //Important code!! also reason for many many bugs 
        typingField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        typingField.setTransferHandler(null);
        typingField.getDocument().addDocumentListener(new MyDocumentListener());
        
        
    }
    
    //Method that handles what is visible on the UI
    
    private void setRaceMode (boolean isPlaying) {
        
   //Creates all classes when this is ran
    if (isPlaying) {
        newGame();   
    }
    
    //Kind of obvious code, makes buttons and fields visible or invisible whether or not the user has pressed the play button.
    snippetPane.setVisible(isPlaying);
    typingField.setVisible(isPlaying);
    jScrollPane1.setVisible(isPlaying);
    jScrollPane2.setVisible(isPlaying);
    jButton1.setVisible(isPlaying);
    jLabel1.setVisible(isPlaying);
    jLabel2.setVisible(isPlaying);
    jLabel3.setVisible(isPlaying);
    newRaceButton.setVisible(!isPlaying);
    
    //This is always going to be false no matter if the user has pressed the play button or not, this is only visible when the user has ended a game so before pressing the play button again
    //GameOverField.setVisible(false);
    
   
    
   
    }
    
    
    private void gameOver () {
        
       isOn = false;
       setRaceMode(isOn);
       
      
       //GameOverField.setVisible(true);
      
    
    }
    /* We need to use the class below because a document listener is the only way for the program to understand the changes in the text the user types
    
    */
    class MyDocumentListener implements DocumentListener {
        
        

        private void update(DocumentEvent e) {
            

           if (isOn) {
            try {
                
                    if (currentRace.getState() == RaceEngine.RaceState.FINISHED) {
                    gameOver();
                    
                    //Updates the (currently invisible) text area thats shown after the game with the statistics
                    updateText(currentRace.getWPM(), currentRace.getKeystrokes(), currentRace.getMistakes(), currentRace.getAccuracy());
                  
                  
                }
               
                
                boolean isAccepted;
                String typedsnip = typingField.getText(0, e.getDocument().getLength());
                // calls a method in RaceEngine that returns true or false
                isAccepted = currentRace.updateTypedSnip(typedsnip);
                
               SwingUtilities.invokeLater(() -> {
                   
                
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

        jScrollPane1 = new javax.swing.JScrollPane();
        snippetPane = new javax.swing.JTextPane();
        newRaceButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        typingField = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setFont(new java.awt.Font("Courier New", 0, 14)); // NOI18N

        snippetPane.setEditable(false);
        snippetPane.setFont(new java.awt.Font("Courier New", 0, 18)); // NOI18N
        jScrollPane1.setViewportView(snippetPane);

        newRaceButton.setText("Start New Race");
        newRaceButton.addActionListener(this::newRaceButtonActionPerformed);

        jScrollPane2.setViewportView(typingField);

        jLabel1.setText("WPM");

        jLabel2.setText("Time");

        jLabel3.setText("Accuracy");

        jButton1.setText("End Race");
        jButton1.addActionListener(this::jButton1ActionPerformed);

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
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 974, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(194, 194, 194)
                                .addComponent(jButton1))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(102, 102, 102)
                        .addComponent(jLabel1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addComponent(jLabel2)
                                .addGap(62, 62, 62)
                                .addComponent(jLabel3))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 786, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(688, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(160, 160, 160)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(457, 457, 457)
                .addComponent(jButton1)
                .addGap(20, 20, 20)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73)
                .addComponent(newRaceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    //Start button is pressed
    private void newRaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newRaceButtonActionPerformed
        isOn = true;
        setRaceMode(isOn);
    }//GEN-LAST:event_newRaceButtonActionPerformed

    
    // End Game button is pressed
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       isOn = false;
       setRaceMode(isOn);
    }//GEN-LAST:event_jButton1ActionPerformed
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
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton newRaceButton;
    private javax.swing.JTextPane snippetPane;
    private javax.swing.JTextPane typingField;
    // End of variables declaration//GEN-END:variables
}
