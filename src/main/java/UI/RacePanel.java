package UI;
import database.TextDao;
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
    private TextDao txtdao;
    private RaceEngine currentRace;
    
    public RacePanel() {
        
    //call the constructor for textDao class
    txtdao = new TextDao();
    
    //Built in java code
    initComponents();
    
    
    
    
     setRaceMode(isOn);
     
            }
    
    
    private void newGame () {
        //Creates all classes when this is ran
        Snippet snip= txtdao.getRandomSnippet();
        String snippet= snip.getBody();
        currentRace = new RaceEngine(snippet);
        snippetPane.setText(snippet);
        
        //Important code!! also reason for many many bugs 
        typingField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        typingField.setTransferHandler(null);
        typingField.getDocument().addDocumentListener(new MyDocumentListener());
    }
    
    private void setRaceMode (boolean isPlaying) {
        
        if (!isPlaying) {
            
        // Set the snippet visible and set the start game button visible
        newRaceButton.setVisible(true);
        
        //Set invisible and invalidate the typing field and the end game button
        typingField.setVisible(false);
        typingField.invalidate();
        
        jButton1.setVisible(false);
        jButton1.invalidate();
        
        //Make irrelevant fields invisible to user
        
        // --> All counters
        jLabel1.setVisible(false);
        jLabel2.setVisible(false);
        jLabel3.setVisible(false);
        
        //The snippet
        snippetPane.setVisible(false);
        
        } 
        else 
        {
            
            
        //Calls the method that creates all needed classes 
        newGame();
        
        //Set 'End game' button visible to user and validate it
        jButton1.setVisible(true);
        jButton1.validate();
        
        //Set the start button invisible and invalid
        newRaceButton.setVisible(false);
        newRaceButton.invalidate();
        
        //Make relevant fields visible to user
        
        // --> All counters
        jLabel1.setVisible(true);
        jLabel2.setVisible(true);
        jLabel3.setVisible(true);
        
        
        // The typingfield and snippet
        snippetPane.setVisible(true);
        typingField.setVisible(true);
        typingField.validate();
        
        
        
   
        }
  
    }
    
    /* We need to use the class below because a document listener is the only way for the program to understand the changes in the text the user types
    
    */
    class MyDocumentListener implements DocumentListener {

        private void update(DocumentEvent e) {
            
            try {
                
                boolean isAccepted;
                String typedsnip;
                typedsnip = typingField.getText(0, e.getDocument().getLength());
                isAccepted = currentRace.updateTypedSnip(typedsnip);
                
               SwingUtilities.invokeLater(() -> {
                if (!isAccepted) {
                typingField.setText(currentRace.getLastAcceptedText());
                typingField.setCaretPosition(typingField.getText().length());
                }
                colorTypingField();
            });
               
            } catch (BadLocationException ex) {
                Logger.getLogger(MyDocumentListener.class.getName()).log(Level.SEVERE, null, ex);
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
    public void colorTypingField(){
        StyledDocument doc = typingField.getStyledDocument();
        if (!currentRace.comparison(typingField.getText().length())){
            javax.swing.text.Style style = typingField.addStyle("Green", null);
        typingField.setForeground(Color.GREEN);
            doc.setCharacterAttributes(typingField.getText().length()-1, typingField.getText().length(), typingField.getStyle("Green"), true); 
        }else{
            javax.swing.text.Style style = typingField.addStyle("Red", null);
        typingField.setForeground(Color.RED);
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                            .addComponent(jScrollPane2)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(102, 102, 102)
                        .addComponent(jLabel1)
                        .addGap(58, 58, 58)
                        .addComponent(jLabel2)
                        .addGap(62, 62, 62)
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(217, 217, 217)
                        .addComponent(jButton1)))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(32, 32, 32)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73)
                .addComponent(newRaceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(81, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    //Start button is pressed
    private void newRaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newRaceButtonActionPerformed
        isOn = true;
        setRaceMode(isOn);
    }//GEN-LAST:event_newRaceButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       isOn = false;
       setRaceMode(isOn);
    }//GEN-LAST:event_jButton1ActionPerformed
    
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
