package UI;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import javax.swing.Icon;
import javax.swing.JPanel;

/**
 * The settings window. It is opened from the race screen in the same way as the history and the
 * game over window, but the content is still empty: apart from the close and the fullscreen
 * button there is nothing in it yet.
 *
 * <p>The window is undecorated like every other window of the application, so it gets its dark
 * border and its accent line from {@link WindowStyler} and is closed with Escape or Ctrl+W.
 */
public class SettingsPanel extends javax.swing.JFrame {

  /** The size of the window while there is nothing in it yet. */
  private static final Dimension WINDOW_SIZE = new Dimension(520, 420);

  /** Creates the blank settings window. */
  public SettingsPanel() {
    setUndecorated(true);
    // The window chrome has to be installed before the window is packed, so the frame is included
    // in its size.
    WindowStyler.apply(this);

    buildWindow();
    pack();
    setLocationRelativeTo(null);
  }

  /**
   * Builds the window: the minimise, fullscreen and close buttons of the window in its top right
   * corner and an empty area for the settings that will come later.
   */
  private void buildWindow() {
    JPanel content = new JPanel(new BorderLayout());
    content.setOpaque(false);
    content.setPreferredSize(WINDOW_SIZE);
    content.add(WindowControls.titleBar(this), BorderLayout.NORTH);

    // The settings themselves are not there yet, so the middle stays empty
    JPanel settings = new JPanel(new BorderLayout());
    settings.setOpaque(false);
    content.add(settings, BorderLayout.CENTER);

    setContentPane(content);
  }

  /**
   * The gear that is drawn on the button that opens this window. It is painted instead of being
   * written as a character, because not every font of the theme has a gear glyph.
   */
  public static Icon gearIcon() {
    return new GearIcon();
  }

  /** A small gear: an outer circle with eight teeth and a hub in the middle. */
  private static final class GearIcon implements Icon {

    private static final int SIZE = 14;
    // The radii are chosen so that the teeth still fit inside the 14 by 14 pixel icon
    private static final double OUTER_RADIUS = 4.0;
    private static final double TOOTH_RADIUS = 6.0;
    private static final double INNER_RADIUS = 1.6;

    @Override
    public int getIconWidth() {
      return SIZE;
    }

    @Override
    public int getIconHeight() {
      return SIZE;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
      Graphics2D graphics2D = (Graphics2D) graphics.create();
      Theme.enableQuality(graphics2D);
      graphics2D.translate(x + SIZE / 2.0, y + SIZE / 2.0);
      // The icon follows the colour of the button, so it stays readable on hover as well
      Color color = component == null ? Theme.TEXT_MUTED : component.getForeground();
      graphics2D.setColor(color);
      graphics2D.setStroke(new BasicStroke(1.5f));
      graphics2D.draw(
          new Ellipse2D.Double(-OUTER_RADIUS, -OUTER_RADIUS, OUTER_RADIUS * 2, OUTER_RADIUS * 2));
      // Eight teeth, each one rotated an eighth of a full turn further than the last
      for (int tooth = 0; tooth < 8; tooth++) {
        graphics2D.rotate(Math.PI / 4);
        graphics2D.draw(new Line2D.Double(0, -OUTER_RADIUS, 0, -TOOTH_RADIUS));
      }
      graphics2D.draw(
          new Ellipse2D.Double(-INNER_RADIUS, -INNER_RADIUS, INNER_RADIUS * 2, INNER_RADIUS * 2));
      graphics2D.dispose();
    }
  }
}
