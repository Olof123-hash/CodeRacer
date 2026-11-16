/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this
 * license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import model.Settings;

/**
 * The panel behind the race screen: it paints the background image the player picked in the
 * settings window.
 *
 * <p>The backgrounds are the images in {@code src/main/resources/backgrounds}. The name that the
 * settings drop down shows is the key of the map below, so adding a background is one line here and
 * the settings window lists it without any change.
 *
 * @author maril
 */
public class BackgroundPanel extends JPanel {

  // Every background the settings window can pick: the name in the drop down -> the image next to
  // the other resources. An ordered map, so the drop down lists the backgrounds in this order. A
  // name that is not in here (the PDF and the "Choose your own" entries of the settings window,
  // for example) leaves the image that is shown alone.
  private static final Map<String, String> BACKGROUNDS = new LinkedHashMap<>();

  static {
    BACKGROUNDS.put(Settings.DEFAULT_BACKGROUND, "/backgrounds/greyblackpanel.jpg");
    BACKGROUNDS.put("White and blue", "/backgrounds/whitebluepanel.jpg");
    BACKGROUNDS.put("Green and black", "/backgrounds/greenandblackpanel.jpg");
    BACKGROUNDS.put("Navy and cyan", "/backgrounds/navycyanpanel.jpg");
    BACKGROUNDS.put("Black", "/backgrounds/navycyanpanel.jpg");
    BACKGROUNDS.put("White", "/backgrounds/navycyanpanel.jpg");
  }

  // True while the background image is painted, instead of the plain colour below it
  boolean showImage = true;

  // The image that is painted at the moment, it is read once in showBackground
  private BufferedImage image;

  public BackgroundPanel() {
    setLayout(new GridBagLayout());
    showBackground(Settings.DEFAULT_BACKGROUND);
  }

  /** The names of the backgrounds, in the order in which the settings window lists them. */
  public static String[] backgroundNames() {
    return BACKGROUNDS.keySet().toArray(new String[0]);
  }

  /**
   * Paints the background with the given name from now on. An unknown name is ignored, so the image
   * stays the one that is already shown; the entries of the settings window that are not a
   * background yet rely on that.
   */
  public void showBackground(String name) {
    String resource = BACKGROUNDS.get(name);
    if (resource == null) {
      return;
    }
    image = readImage(resource);
    repaint();
  }

  /** Reads an image from the resources folder, or returns null when it is not there. */
  private BufferedImage readImage(String resource) {
    try {
      URL url = getClass().getResource(resource);
      if (url == null) {
        return null;
      }
      return ImageIO.read(url);
    } catch (IOException e) {
      // A missing or broken image should never stop the game from starting, paintComponent fills
      // the panel with a plain colour instead
      return null;
    }
  }

  public void setImage() {
    showImage = true;
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (image != null && showImage) {
      g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    } else {
      g.setColor(new Color(10, 5, 15));
      g.fillRect(0, 0, getWidth(), getHeight());
    }
  }
}
