/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UI;
import database.Database;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author mathi
 */
public class HistoryPanel extends javax.swing.JFrame {
    
    //Declaring fields
    Database database = new Database();
    JPanel list = new JPanel();
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(HistoryPanel.class.getName());

    
    //This is AI generated and code needs to be commented!!!
    public HistoryPanel() {
        setUndecorated(true);
        WindowStyler.apply(this);
        initComponents();

       

       
        GroupLayout layout = (GroupLayout) getContentPane().getLayout();
        JPanel history = buildHistoryPanel();
        history.setPreferredSize(new Dimension(650, 450));
        
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        // The minimise, fullscreen and close buttons of the window sit in its top right corner
        getContentPane().add(WindowControls.titleBar(this), BorderLayout.NORTH);
        getContentPane().add(history, BorderLayout.CENTER);
     

        pack();
        setLocationRelativeTo(null);
    }
    
    /** Builds the panel with the title and the scrollable list of played games. */
 
    private JPanel buildHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));

        JLabel heading = new JLabel("TYPING HISTORY");
        LabelStyler.applyTitle(heading, 17);
        panel.add(heading, BorderLayout.NORTH);

        // The constructor of Database without a language reads the games that were played, and
        // getDatabase returns them as readable lines, in the order in which they were played.
      
        
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        formatGames(list, database.getDatabase());

        JScrollPane scroll = new JScrollPane(list);
        TextStyler.applySurfaceScrollPane(scroll);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    /** Prefixes every game that comes from the database with its number. */
    private String formatGames(JPanel list, Map<Integer, String> games) {
        if (games.isEmpty()) {
             list.add(new JLabel("No races played yet Finish a race and the result will show up here."));
             
        }
        StringBuilder text = new StringBuilder();
        int number = 1;
        for (Map.Entry<Integer, String> entry : games.entrySet()) {
            
            JTextArea label = new JTextArea(String.format("#%02d   %s", number++, entry.getValue()));
            label.setEditable(false);
            label.setOpaque(false);
            
            
            // Every row gets its own small "x" button, so a single game can be picked out.
            JButton deleteButton = new JButton("x");
            ButtonStyler.applyRowDelete(deleteButton);
            deleteButton.addActionListener(event -> { database.deleteEntry(); 
            refresh();
            });
            
            // The "x" is invisible until the mouse is over the row. It sits in a holder with a
            // fixed size, so the row does not change its height when the button comes and goes.
            JPanel deleteHolder = new JPanel(new BorderLayout());
            deleteHolder.setOpaque(false);
            Dimension holderSize = new Dimension(26, 22);
            deleteHolder.setPreferredSize(holderSize);
            deleteHolder.setMinimumSize(holderSize);
            deleteHolder.setMaximumSize(holderSize);
            deleteHolder.add(deleteButton, BorderLayout.CENTER);
            
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            row.add(label, BorderLayout.CENTER);
            row.add(deleteHolder, BorderLayout.EAST);
            showDeleteButtonOnHover(row, label, deleteHolder, deleteButton);
            list.add(row);
            
            text.append(System.lineSeparator());
            if (number <= games.size()) {
                text.append(System.lineSeparator());
            }
        }
        return text.toString();
    }
    
    /**
     * Shows the "x" of a row while the mouse is somewhere inside that row. Moving from the text to
     * the button also fires an exit event, so the mouse only counts as gone once the pointer has
     * left the whole row.
     */
    private void showDeleteButtonOnHover(JPanel row, JTextArea label, JPanel holder, JButton deleteButton) {
        deleteButton.setVisible(false);
        MouseAdapter hover = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                deleteButton.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent event) {
                Point point = SwingUtilities.convertPoint(event.getComponent(), event.getPoint(), row);
                if (!row.contains(point)) {
                    deleteButton.setVisible(false);
                }
            }
        };
        row.addMouseListener(hover);
        label.addMouseListener(hover);
        holder.addMouseListener(hover);
        deleteButton.addMouseListener(hover);
    }

    private void refresh() {
    list.removeAll();                          // clear the old rows
    Map<Integer, String> games = database.getDatabase();  // re-read from DB (use your real method)
    formatGames(list, games);    // rebuilds the rows
    // if you show `text` somewhere (e.g. the "No races played yet" message), set it here
    list.revalidate();
    list.repaint();
    }
    
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 428, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 321, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new HistoryPanel().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
