package UI;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxUI;

/**
 * Applies the dark CodeRacer theme to a combo box: a rounded dark field, an orange chevron and a
 * popup list that uses the same colours instead of the grey Swing defaults.
 */

//Again AI SLOP
public final class ComboBoxStyler {

  private ComboBoxStyler() {
    // Utility class, no instances
  }

  /** Styles the combo box and its dropdown list. */
  public static void apply(JComboBox<String> comboBox) {
    // The UI is installed first, because installing it resets the colours of the combo box
    comboBox.setUI(new FlatComboBoxUI());
    comboBox.setFont(Theme.mono(14));
    comboBox.setForeground(Theme.ACCENT);
    comboBox.setBackground(Theme.BACKGROUND_ALT);
    comboBox.setOpaque(false);
    comboBox.setBorder(Theme.roundedBorder(Theme.SUBTLE, 8, 1, 8, 2));
    comboBox.setRenderer(new ThemedRenderer());
    comboBox.setMaximumRowCount(10);
    // A combo box does not need the focus border of the look and feel, it has its own
    comboBox.setFocusable(true);
  }

  /** Draws the rounded background of the combo box itself. */
  private static final class FlatComboBoxUI extends BasicComboBoxUI {

    private static final int RADIUS = 8;

    @Override
    public void update(Graphics graphics, JComponent component) {
      // Skip the square background that ComponentUI.update would paint
      paint(graphics, component);
    }

    @Override
    public void paint(Graphics graphics, JComponent component) {
      Graphics2D graphics2D = (Graphics2D) graphics.create();
      Theme.enableQuality(graphics2D);
      graphics2D.setColor(Theme.BACKGROUND_ALT);
      graphics2D.fillRoundRect(
          0, 0, component.getWidth() - 1, component.getHeight() - 1, RADIUS * 2, RADIUS * 2);
      graphics2D.dispose();
      super.paint(graphics, component);
    }

    @Override
    public void paintCurrentValueBackground(Graphics graphics, Rectangle bounds, boolean hasFocus) {
      // The rounded background is already painted in paint()
    }

    @Override
    protected JButton createArrowButton() {
      JButton arrow =
          new JButton() {
            @Override
            protected void paintComponent(Graphics graphics) {
              Graphics2D graphics2D = (Graphics2D) graphics.create();
              Theme.enableQuality(graphics2D);
              int centerX = getWidth() / 2;
              int centerY = getHeight() / 2;
              graphics2D.setColor(
                  getModel().isRollover() ? Theme.ACCENT_BRIGHT : Theme.ACCENT);
              graphics2D.setStroke(
                  new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
              graphics2D.drawLine(centerX - 4, centerY - 2, centerX, centerY + 2);
              graphics2D.drawLine(centerX, centerY + 2, centerX + 4, centerY - 2);
              graphics2D.dispose();
            }
          };
      arrow.setContentAreaFilled(false);
      arrow.setBorder(null);
      arrow.setOpaque(false);
      arrow.setFocusable(false);
      arrow.setRolloverEnabled(true);
      arrow.setPreferredSize(new Dimension(26, 26));
      arrow.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      return arrow;
    }
  }

  /** Draws the entries of the dropdown list in the theme colours. */
  private static final class ThemedRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(
        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      JLabel label =
          (JLabel)
              super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      label.setFont(Theme.mono(14));
      label.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

      if (index < 0) {
        // The value that is shown inside the combo box itself; the rounded background is behind it
        label.setOpaque(false);
        label.setForeground(Theme.ACCENT);
      } else if (isSelected) {
        label.setOpaque(true);
        label.setBackground(Theme.BACKGROUND);
        label.setForeground(Theme.ACCENT);
      } else {
        label.setOpaque(true);
        label.setBackground(Theme.BACKGROUND_ALT);
        label.setForeground(Theme.TEXT);
      }
      return label;
    }
  }
}
