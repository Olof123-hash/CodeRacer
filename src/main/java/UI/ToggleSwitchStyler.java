package UI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicCheckBoxUI;

/**
 * Gives a check box the look of a small switch instead of the square tick box that Swing draws by
 * default: a rounded track that fills with the accent colour when the setting is on, with a knob
 * that slides to the other side.
 *
 * <p>It is still a normal {@link JCheckBox} underneath, so the keyboard, the accessible context and
 * the action listeners of the settings window keep working. Only the painting is replaced, in the
 * same way as {@link ButtonStyler} and {@link ComboBoxStyler} replace the painting of their
 * components.
 */
public final class ToggleSwitchStyler {

  // The size of the switch itself, without the padding around it
  private static final int TRACK_WIDTH = 38;
  private static final int TRACK_HEIGHT = 20;
  // The space between the edge of the track and the knob
  private static final int KNOB_INSET = 3;
  // Space kept around the track so the focus ring is not clipped
  private static final int PADDING = 4;

  private ToggleSwitchStyler() {
    // Utility class, no instances
  }

  /** Styles the check box as a switch. Its text is not used, the row around it has the caption. */
  public static void apply(JCheckBox checkBox) {
    checkBox.setUI(new SwitchUI());
    checkBox.setOpaque(false);
    checkBox.setContentAreaFilled(false);
    checkBox.setBorderPainted(false);
    checkBox.setFocusPainted(false);
    checkBox.setRolloverEnabled(true);
    checkBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    checkBox.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
    checkBox.setPreferredSize(
        new Dimension(TRACK_WIDTH + PADDING * 2, TRACK_HEIGHT + PADDING * 2));
  }

  /** Paints the track and the knob, and skips the square background and the tick of Swing. */
  private static final class SwitchUI extends BasicCheckBoxUI {

    @Override
    public void update(Graphics graphics, JComponent component) {
      // Skip the square background that ComponentUI.update would paint
      paint(graphics, component);
    }

    @Override
    public void paint(Graphics graphics, JComponent component) {
      AbstractButton button = (AbstractButton) component;
      Graphics2D graphics2D = (Graphics2D) graphics.create();
      Theme.enableQuality(graphics2D);

      boolean on = button.getModel().isSelected();
      boolean enabled = button.isEnabled();
      boolean hovered = enabled && button.getModel().isRollover();

      Insets insets = button.getInsets();
      int availableWidth = button.getWidth() - insets.left - insets.right;
      int availableHeight = button.getHeight() - insets.top - insets.bottom;
      int x = insets.left + Math.max(0, (availableWidth - TRACK_WIDTH) / 2);
      int y = insets.top + Math.max(0, (availableHeight - TRACK_HEIGHT) / 2);
      int radius = TRACK_HEIGHT;

      // The track is filled with the accent colour while the switch is on, and stays a quiet dark
      // surface with a thin outline while it is off, so both states are visible on the window.
      Color track;
      if (!enabled) {
        track = Theme.BACKGROUND_ALT;
      } else if (on) {
        track = hovered ? Theme.ACCENT_BRIGHT : Theme.ACCENT;
      } else {
        track = hovered ? Theme.SURFACE : Theme.BACKGROUND_ALT;
      }
      graphics2D.setColor(track);
      graphics2D.fillRoundRect(x, y, TRACK_WIDTH, TRACK_HEIGHT, radius, radius);

      if (!on) {
        graphics2D.setColor(enabled ? Theme.SUBTLE : Theme.BACKGROUND_ALT);
        graphics2D.setStroke(new BasicStroke(1.0f));
        graphics2D.drawRoundRect(x, y, TRACK_WIDTH - 1, TRACK_HEIGHT - 1, radius, radius);
      }

      // The knob: dark on the orange track, muted grey on the dark track, and the same size in
      // both states so the movement is only horizontal.
      int knob = TRACK_HEIGHT - KNOB_INSET * 2;
      int knobX = on ? x + TRACK_WIDTH - KNOB_INSET - knob : x + KNOB_INSET;
      Color knobColor;
      if (!enabled) {
        knobColor = Theme.SUBTLE;
      } else {
        knobColor = on ? Theme.BACKGROUND : Theme.TEXT_MUTED;
      }
      graphics2D.setColor(knobColor);
      graphics2D.fillOval(knobX, y + KNOB_INSET, knob, knob);

      // A rounded focus ring keeps the switch usable with the keyboard
      if (button.isFocusOwner() && enabled) {
        graphics2D.setColor(Theme.withAlpha(Theme.ACCENT, 150));
        graphics2D.setStroke(new BasicStroke(1.2f));
        graphics2D.drawRoundRect(
            x - 2, y - 2, TRACK_WIDTH + 3, TRACK_HEIGHT + 3, radius + 4, radius + 4);
      }

      graphics2D.dispose();
    }
  }
}
