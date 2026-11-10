package UI;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * The three buttons that a decorated window would get from the title bar of the operating system:
 * minimise, fullscreen and close. They are drawn as a quiet row of icons in the top right corner of
 * the window, where the close button turns red while the mouse is over it, just like a real title
 * bar.
 *
 * <p>The popup windows of the application are undecorated, so without this row they could only be
 * closed with the Escape key. {@link #titleBar(JFrame)} builds the row that the windows add at the
 * top of their content.
 */
public final class WindowControls extends JPanel {

  // The middle button switches between the maximise and the restore icon
  private static final int MINIMISE = 0;
  private static final int MAXIMISE = 1;
  private static final int RESTORE = 2;
  private static final int CLOSE = 3;

  private static final int BUTTON_SIZE = 32;
  private static final int ICON_SIZE = 10;

  private final JFrame frame;
  private final ControlButton fullscreenButton;

  private WindowControls(JFrame frame) {
    super(new FlowLayout(FlowLayout.RIGHT, 2, 2));
    this.frame = frame;
    setOpaque(false);

    ControlButton minimiseButton = new ControlButton(MINIMISE, Theme.SURFACE);
    minimiseButton.setToolTipText("Minimise");
    minimiseButton.addActionListener(event -> frame.setState(JFrame.ICONIFIED));

    fullscreenButton = new ControlButton(MAXIMISE, Theme.SURFACE);
    fullscreenButton.setToolTipText("Fullscreen");
    fullscreenButton.addActionListener(
        event -> {
          WindowStyler.toggleFullscreen(frame);
          refreshFullscreenIcon();
        });

    ControlButton closeButton = new ControlButton(CLOSE, Theme.ERROR);
    closeButton.setToolTipText("Close");
    closeButton.addActionListener(event -> frame.dispose());

    add(minimiseButton);
    add(fullscreenButton);
    add(closeButton);
  }

  /**
   * Builds the row of window buttons with the icons in the top right corner. The windows add this
   * row at the top of their content.
   */
  public static JPanel titleBar(JFrame frame) {
    JPanel bar = new JPanel(new BorderLayout());
    bar.setOpaque(false);
    bar.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 6));
    bar.add(new WindowControls(frame), BorderLayout.EAST);
    return bar;
  }

  /** Shows the restore icon while the window fills the screen and the maximise icon otherwise. */
  private void refreshFullscreenIcon() {
    fullscreenButton.setShape(WindowStyler.isFullscreen(frame) ? RESTORE : MAXIMISE);
  }

  /**
   * One of the three buttons. Swing only paints the icon, the row itself is transparent, so the
   * dark window behind it stays visible.
   */
  private static final class ControlButton extends JButton {

    private final Color hoverFill;
    private int shape;
    private boolean hovered;

    private ControlButton(int shape, Color hoverFill) {
      this.shape = shape;
      this.hoverFill = hoverFill;
      setContentAreaFilled(false);
      setBorderPainted(false);
      setFocusPainted(false);
      setOpaque(false);
      setRolloverEnabled(false);
      setBorder(BorderFactory.createEmptyBorder());
      setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

      MouseAdapter hover =
          new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
              hovered = true;
              repaint();
            }

            @Override
            public void mouseExited(MouseEvent event) {
              hovered = false;
              repaint();
            }
          };
      addMouseListener(hover);
    }

    private void setShape(int shape) {
      if (this.shape != shape) {
        this.shape = shape;
        repaint();
      }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
      Graphics2D graphics2D = (Graphics2D) graphics.create();
      Theme.enableQuality(graphics2D);

      int width = Math.max(1, getWidth());
      int height = Math.max(1, getHeight());
      // The rounded background only appears under the mouse, like the buttons of a title bar
      if (hovered) {
        graphics2D.setColor(getModel().isPressed() ? Theme.darker(hoverFill) : hoverFill);
        graphics2D.fillRoundRect(1, 1, width - 2, height - 2, 8, 8);
      }

      // The close button becomes brighter on its red background, the other two stay muted
      boolean onClose = shape == CLOSE;
      graphics2D.setColor(hovered && onClose ? Theme.TEXT : Theme.TEXT_MUTED);
      graphics2D.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      drawIcon(graphics2D, width, height);
      graphics2D.dispose();
    }

    private void drawIcon(Graphics2D graphics2D, int width, int height) {
      double centerX = width / 2.0;
      double centerY = height / 2.0;
      double half = ICON_SIZE / 2.0;
      switch (shape) {
        case MINIMISE:
          // A single short line
          graphics2D.draw(new Line2D.Double(centerX - half, centerY, centerX + half, centerY));
          break;
        case MAXIMISE:
          // One square
          graphics2D.draw(
              new Rectangle2D.Double(
                  centerX - half, centerY - half, ICON_SIZE - 1, ICON_SIZE - 1));
          break;
        case RESTORE:
          // Two overlapping squares, the back one offset up and to the right
          graphics2D.draw(
              new Rectangle2D.Double(
                  centerX - half + 2.0, centerY - half, ICON_SIZE - 3.0, ICON_SIZE - 2.0));
          graphics2D.draw(
              new Rectangle2D.Double(
                  centerX - half, centerY - half + 2.0, ICON_SIZE - 3.0, ICON_SIZE - 2.0));
          break;
        case CLOSE:
        default:
          // Two crossing lines
          double d = half - 1.0;
          graphics2D.draw(
              new Line2D.Double(centerX - d, centerY - d, centerX + d, centerY + d));
          graphics2D.draw(
              new Line2D.Double(centerX - d, centerY + d, centerX + d, centerY - d));
          break;
      }
    }
  }
}
