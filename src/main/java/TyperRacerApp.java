
import UI.RacePanel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

/**
 *
 * @author maril
 */
public class TyperRacerApp {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.JFrame frame = new javax.swing.JFrame("CodeRacer");
            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            frame.add(new RacePanel());
            frame.pack();
            //The window can not be made smaller than the layout of the designer
            frame.setMinimumSize(frame.getPreferredSize());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        });
}
}
