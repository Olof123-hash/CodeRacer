package UI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 * Gives a slider the look of the rest of the interface: a thin rounded track, an accent coloured
 * part that shows how far the handle is, and a round accent handle. The grey Swing look is replaced
 * in the same way as {@link ComboBoxStyler} and {@link ToggleSwitchStyler} replace theirs.
 *
 * <p>It is still a normal {@link JSlider} underneath, so dragging, the arrow keys and the change
 * listeners of the settings window keep working.
 */
public final class SliderStyler {

  // The height of the track line
  private static final int TRACK_HEIGHT = 4;
  // The diameter of the round handle
  private static final int THUMB_SIZE = 16;

  private SliderStyler() {
    // Utility class, no instances
  }

  /**
   * Styles a horizontal slider. Calling it again on a slider that is already styled only repaints
   * it; it never installs a second UI.
   *
   * <p>That guard matters: installing a UI uninstalls the previous one, and the previous one sets
   * its own {@code slider} field to null. The settings window re-styles itself while the handle of
   * the slider is being dragged (every value change is applied immediately), so without the guard
   * the old UI would lose its slider while its own mouse listener is still running and pressing the
   * handle would throw a NullPointerException. The painting below reads the {@link Theme} colours
   * every time it runs, so a plain repaint is all a colour scheme change needs.
   */
  public static void apply(JSlider slider) {
    if (!(slider.getUI() instanceof FlatSliderUI)) {
      slider.setUI(new FlatSliderUI(slider));
    }
    slider.setOpaque(false);
    slider.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    slider.setPreferredSize(new Dimension(170, 26));
    slider.repaint();
  }

  /** Paints the track and the handle of the slider, and skips the default Swing painting. */
  private static final class FlatSliderUI extends BasicSliderUI {

    private FlatSliderUI(JSlider slider) {
      super(slider);
    }

    @Override
    protected Dimension getThumbSize() {
      return new Dimension(THUMB_SIZE, THUMB_SIZE);
    }

    @Override
    public void paintTrack(Graphics graphics) {
      Graphics2D graphics2D = (Graphics2D) graphics.create();
      Theme.enableQuality(graphics2D);

      int centerY = trackRect.y + (trackRect.height - TRACK_HEIGHT) / 2;
      int radius = TRACK_HEIGHT;
      // The empty part of the track is a dark surface, so the slider is visible on the window
      graphics2D.setColor(Theme.BACKGROUND_ALT);
      graphics2D.fillRoundRect(trackRect.x, centerY, trackRect.width, TRACK_HEIGHT, radius, radius);

      // The part in front of the handle is accent coloured, which shows the value at a glance
      int handleCenter = thumbRect.x + thumbRect.width / 2;
      graphics2D.setColor(slider.isEnabled() ? Theme.ACCENT : Theme.SUBTLE);
      graphics2D.fillRoundRect(
          trackRect.x,
          centerY,
          Math.max(radius, handleCenter - trackRect.x),
          TRACK_HEIGHT,
          radius,
          radius);

      graphics2D.setColor(Theme.SUBTLE);
      graphics2D.setStroke(new BasicStroke(1.0f));
      graphics2D.drawRoundRect(
          trackRect.x, centerY, trackRect.width - 1, TRACK_HEIGHT - 1, radius, radius);
      graphics2D.dispose();
    }

    @Override
    public void paintThumb(Graphics graphics) {
      Graphics2D graphics2D = (Graphics2D) graphics.create();
      Theme.enableQuality(graphics2D);

      Color handle;
      if (!slider.isEnabled()) {
        handle = Theme.SUBTLE;
      } else if (slider.getValueIsAdjusting()) {
        // Bright while the player drags it, so the handle is easy to follow
        handle = Theme.ACCENT_BRIGHT;
      } else {
        handle = Theme.ACCENT;
      }
      graphics2D.setColor(handle);
      graphics2D.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
      // A dark ring separates the handle from the track it sits on
      graphics2D.setColor(Theme.BACKGROUND);
      graphics2D.setStroke(new BasicStroke(1.6f));
      graphics2D.drawOval(thumbRect.x + 1, thumbRect.y + 1, thumbRect.width - 2, thumbRect.height - 2);
      graphics2D.dispose();
    }

    @Override
    public void paintFocus(Graphics graphics) {
      // The handle already shows that the slider has the focus, so the dotted rectangle is skipped
    }

    @Override
    public void paintTicks(Graphics graphics) {
      // No ticks: the size is shown as a number next to the slider
    }

    @Override
    public void paintLabels(Graphics graphics) {
      // No labels under the slider for the same reason
    }
  }
}
