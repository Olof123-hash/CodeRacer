package UI;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * Applies the theme to the text components of the application: the code panes of the race, the
 * text field of the game over screen and the list in the history window. The scroll bars are
 * restyled too, because the default Swing bars do not match a dark interface.
 */
public final class TextStyler {

  private TextStyler() {
    // Utility class, no instances
  }

  /** Styles a code pane, for example the snippet or the field the player types in. */
  public static void applyCodePane(JTextPane pane) {
    pane.setOpaque(false);
    pane.setBorder(null);
    pane.setFont(Theme.mono(18));
    pane.setForeground(Theme.TEXT);
    // The caret colour is one of the typing settings, so it does not come from the scheme
    pane.setCaretColor(Theme.CARET);
    pane.setSelectionColor(Theme.withAlpha(Theme.ACCENT, 70));
    pane.setSelectedTextColor(Theme.BACKGROUND);
    pane.setMargin(new Insets(0, 0, 0, 0));
  }

  /**
   * Styles the pane that receives the typing of the player. The snippet pane already draws every
   * typed character in its own colour, so the text of this pane is made invisible; only its caret
   * is visible.
   */
  public static void applyTypingPane(JTextPane pane) {
    applyCodePane(pane);
    pane.setForeground(Theme.withAlpha(Theme.TEXT, 0));
  }

  /** Styles a read only text area that shows a longer text, like the typing history. */
  public static void applyHistoryArea(JTextArea area) {
    area.setEditable(false);
    area.setFocusable(false);
    area.setOpaque(true);
    area.setBackground(Theme.BACKGROUND_ALT);
    area.setForeground(Theme.TEXT);
    area.setCaretColor(Theme.ACCENT);
    area.setSelectionColor(Theme.withAlpha(Theme.ACCENT, 70));
    area.setSelectedTextColor(Theme.BACKGROUND);
    area.setFont(Theme.mono(13));
    area.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
    area.setLineWrap(true);
    area.setWrapStyleWord(false);
  }

  /** Styles a single line text field, for example a value on the game over screen. */
  public static void applyStatValue(JTextField field) {
    field.setOpaque(false);
    field.setBorder(null);
    field.setEditable(false);
    field.setFocusable(false);
    field.setFont(Theme.monoBold(24));
    field.setForeground(Theme.ACCENT);
    field.setCaretColor(Theme.ACCENT);
    field.setSelectionColor(Theme.withAlpha(Theme.ACCENT, 70));
    field.setSelectedTextColor(Theme.BACKGROUND);
    field.setHorizontalAlignment(JTextField.RIGHT);
  }

  /** Makes a scroll pane transparent, so the background image stays visible behind it. */
  public static void applyScrollPane(JScrollPane scrollPane) {
    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);
    scrollPane.setBorder(null);
    scrollPane.setViewportBorder(null);
    applyScrollBar(scrollPane.getVerticalScrollBar());
    applyScrollBar(scrollPane.getHorizontalScrollBar());
  }

  /** Makes a scroll pane a solid dark surface with a rounded border, used for popups and lists. */
  public static void applySurfaceScrollPane(JScrollPane scrollPane) {
    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);
    scrollPane.setBorder(Theme.roundedBorder(Theme.SUBTLE, 8, 1));
    scrollPane.setViewportBorder(null);
    applyScrollBar(scrollPane.getVerticalScrollBar());
    applyScrollBar(scrollPane.getHorizontalScrollBar());
  }

  /** Replaces the look and feel scroll bar by a thin, rounded bar in the theme colours. */
  public static void applyScrollBar(JScrollBar scrollBar) {
    scrollBar.setUI(new FlatScrollBarUI());
    scrollBar.setOpaque(false);
    scrollBar.setPreferredSize(new Dimension(10, 10));
    scrollBar.setUnitIncrement(16);
    scrollBar.setBorder(BorderFactory.createEmptyBorder());
  }

  /** A scroll bar without arrows and with a rounded thumb. */
  private static final class FlatScrollBarUI extends BasicScrollBarUI {

    @Override
    protected JButton createDecreaseButton(int orientation) {
      return invisibleButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
      return invisibleButton();
    }

    private JButton invisibleButton() {
      JButton button = new JButton();
      Dimension zero = new Dimension(0, 0);
      button.setPreferredSize(zero);
      button.setMinimumSize(zero);
      button.setMaximumSize(zero);
      button.setFocusable(false);
      return button;
    }

    @Override
    protected void paintTrack(Graphics graphics, JComponent component, Rectangle trackBounds) {
      graphics.setColor(Theme.BACKGROUND);
      graphics.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }

    @Override
    protected void paintThumb(Graphics graphics, JComponent component, Rectangle thumbBounds) {
      if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
        return;
      }
      Graphics2D graphics2D = (Graphics2D) graphics.create();
      Theme.enableQuality(graphics2D);
      graphics2D.setColor(isThumbRollover() ? Theme.ACCENT : Theme.SUBTLE);
      int x = thumbBounds.x + 2;
      int y = thumbBounds.y + 2;
      int width = Math.max(1, thumbBounds.width - 4);
      int height = Math.max(1, thumbBounds.height - 4);
      graphics2D.fillRoundRect(x, y, width, height, 8, 8);
      graphics2D.dispose();
    }
  }
}
