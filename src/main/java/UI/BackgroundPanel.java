/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.Graphics;
import java.net.URL;
import java.awt.Color;
import java.awt.GridBagLayout;

/**
 *
 * @author maril
 */
public class BackgroundPanel extends JPanel {

    boolean showImage = true;
    BufferedImage image;

    public BackgroundPanel() {
        setLayout(new GridBagLayout());
        try {
            URL url = getClass().getResource("/backgroundstartpanel.jpg");
            image = ImageIO.read(url);
        } catch (IOException | IllegalArgumentException e) {
            image = null;
        }
    }

    public void setImage() {
        showImage = true;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null && showImage != false) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(10, 5, 15));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
