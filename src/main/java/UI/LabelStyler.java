package UI;

import javax.swing.JLabel;

/**
 * Gives labels the font and the colour that belong to their role in the interface. Keeping this in
 * one class means a caption looks the same on every screen.
 */
public final class LabelStyler {

  private LabelStyler() {
    // Utility class, no instances
  }

  /** Normal text, for example the snippet or a message. */
  public static void apply(JLabel label) {
    label.setFont(Theme.ui(14));
    label.setForeground(Theme.TEXT);
  }

  /** Quiet text, for example the "Select Language" hint next to the combo box. */
  public static void applyMuted(JLabel label) {
    label.setFont(Theme.ui(13));
    label.setForeground(Theme.TEXT_MUTED);
  }

  /** Text that should draw attention, for example a section header. */
  public static void applyAccent(JLabel label) {
    label.setFont(Theme.uiBold(13));
    label.setForeground(Theme.ACCENT);
  }

  /** A live statistic such as the WPM or the timer, shown in the monospaced font. */
  public static void applyStat(JLabel label) {
    label.setFont(Theme.monoBold(16));
    label.setForeground(Theme.TEXT);
  }

  /** The title of a window, in the monospaced font with the accent colour. */
  public static void applyTitle(JLabel label, int size) {
    label.setFont(Theme.monoBold(size));
    label.setForeground(Theme.ACCENT);
  }

  /** A small caption above or next to a value. */
  public static void applyCaption(JLabel label) {
    label.setFont(Theme.monoBold(12));
    label.setForeground(Theme.TEXT_MUTED);
  }
}
