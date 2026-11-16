package UI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Gives every button of the application the same flat, rounded look. A button is painted by the
 * {@link StyledButtonUI} below, which fills a rounded rectangle instead of the square background
 * that Swing normally uses, so the dark background image keeps showing in the corners.
 *
 * <p>There are four variants:
 *
 * <ul>
 *   <li>{@link #applyPrimary(JButton)} – orange, for the one action that matters on a screen
 *   <li>{@link #applySecondary(JButton)} – dark with a border, for normal actions
 *   <li>{@link #applyGhost(JButton)} – only a border until the mouse hovers over it
 *   <li>{@link #applyDanger(JButton)} – red, for actions that end or delete something
 * </ul>
 */
public final class ButtonStyler {

  private ButtonStyler() {
    // Utility class, no instances
  }

  // Client properties that tell the button UI how to paint the button
  private static final String FILL = "coderacer.fill";
  private static final String FILL_HOVER = "coderacer.fillHover";
  private static final String BORDER = "coderacer.border";
  private static final String BORDER_HOVER = "coderacer.borderHover";
  private static final String TEXT_BASE = "coderacer.textBase";
  private static final String TEXT_HOVER = "coderacer.textHover";
  private static final String RADIUS = "coderacer.radius";
  private static final String HOVERED = "coderacer.hovered";
  private static final String STYLED = "coderacer.styled";

  /** The default button: dark with a subtle border. */
  public static void apply(JButton button) {
    applySecondary(button);
  }

  /** The most important button on the screen: filled with the accent colour. */
  public static void applyPrimary(JButton button) {
    style(
        button,
        Theme.ACCENT,
        Theme.ACCENT_BRIGHT,
        null,
        null,
        Theme.BACKGROUND,
        Theme.BACKGROUND,
        14,
        new Insets(12, 30, 12, 30));
    button.setFont(Theme.uiBold(16));
  }

  /** A normal action: dark background, light text and a border that lights up on hover. */
  public static void applySecondary(JButton button) {
    style(
        button,
        Theme.BACKGROUND_ALT,
        Theme.SURFACE,
        Theme.SUBTLE,
        Theme.ACCENT,
        Theme.TEXT,
        Theme.TEXT,
        10,
        new Insets(10, 22, 10, 22));
    button.setFont(Theme.uiBold(15));
  }

  /** A quiet action, for example closing a window: no fill until the mouse is over it. */
  public static void applyGhost(JButton button) {
    style(
        button,
        null,
        Theme.SURFACE,
        Theme.SUBTLE,
        Theme.ACCENT,
        Theme.TEXT_MUTED,
        Theme.TEXT,
        8,
        new Insets(7, 16, 7, 16));
    button.setFont(Theme.uiBold(13));
  }

  /**
   * The small "x" of a list row: no fill and no border, and filled with the error colour when the
   * mouse is over it. The owner of the row decides when it is visible at all.
   */
  public static void applyRowDelete(JButton button) {
    style(
        button,
        null,
        Theme.ERROR,
        null,
        Theme.ERROR,
        Theme.TEXT_MUTED,
        Theme.TEXT,
        5,
        new Insets(2, 8, 2, 8));
    button.setFont(Theme.uiBold(12));
  }

  /** An action that ends something: filled with the error colour on hover. */
  public static void applyDanger(JButton button) {
    style(
        button,
        Theme.ERROR_DIM,
        Theme.ERROR,
        Theme.ERROR,
        Theme.ERROR,
        Theme.TEXT,
        Theme.TEXT,
        10,
        new Insets(10, 22, 10, 22));
    button.setFont(Theme.uiBold(15));
  }

  private static void style(
      JButton button,
      Color fill,
      Color fillHover,
      Color border,
      Color borderHover,
      Color text,
      Color textHover,
      int radius,
      Insets margin) {
    button.setUI(new StyledButtonUI());
    // The rounded background is painted by the UI, so Swing must not paint a square one
    button.setOpaque(false);
    button.setContentAreaFilled(false);
    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setRolloverEnabled(true);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    // An empty border with the same padding as the margin: BasicButtonUI uses the border insets
    // for the preferred size and for the position of the text, the margin alone is not enough.
    button.setBorder(
        BorderFactory.createEmptyBorder(margin.top, margin.left, margin.bottom, margin.right));
    button.setMargin(margin);
    button.setForeground(text);

    button.putClientProperty(FILL, fill);
    button.putClientProperty(FILL_HOVER, fillHover);
    button.putClientProperty(BORDER, border);
    button.putClientProperty(BORDER_HOVER, borderHover);
    button.putClientProperty(TEXT_HOVER, textHover);
    button.putClientProperty(TEXT_BASE, text);
    button.putClientProperty(RADIUS, radius);

    // The hover listener is only added once, even when a button is styled twice
    if (button.getClientProperty(STYLED) == null) {
      button.putClientProperty(STYLED, Boolean.TRUE);
      button.addMouseListener(
          new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
              setHovered(button, true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
              setHovered(button, false);
            }
          });
    }
    button.repaint();
  }

  private static void setHovered(JButton button, boolean hovered) {
    button.putClientProperty(HOVERED, hovered);
    Color text = (Color) button.getClientProperty(TEXT_HOVER);
    if (text != null) {
      button.setForeground(hovered ? text : (Color) button.getClientProperty(TEXT_BASE));
    }
    button.repaint();
  }

  /**
   * Paints the rounded background and the rounded border of a button, then lets Swing paint the
   * text and the icon on top of it.
   */
  private static final class StyledButtonUI extends BasicButtonUI {

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

      boolean hovered = Boolean.TRUE.equals(button.getClientProperty(HOVERED));
      boolean pressed = button.getModel().isArmed() && button.getModel().isPressed();
      boolean active = hovered || pressed;

      Color fill = active ? (Color) button.getClientProperty(FILL_HOVER) : null;
      if (fill == null) {
        fill = (Color) button.getClientProperty(FILL);
      }
      Color border = active ? (Color) button.getClientProperty(BORDER_HOVER) : null;
      if (border == null) {
        border = (Color) button.getClientProperty(BORDER);
      }
      int radius = radius(button);

      int width = Math.max(1, button.getWidth());
      int height = Math.max(1, button.getHeight());

      if (!button.isEnabled()) {
        // A disabled button keeps the shape but loses its colour
        fill = Theme.BACKGROUND_ALT;
        border = Theme.ERROR_DIM;
      }

      if (fill != null) {
        graphics2D.setColor(pressed ? Theme.darker(fill) : fill);
        graphics2D.fillRoundRect(0, 0, width - 1, height - 1, radius * 2, radius * 2);
      }
      if (border != null) {
        graphics2D.setColor(border);
        graphics2D.setStroke(new BasicStroke(1.2f));
        graphics2D.drawRoundRect(1, 1, width - 3, height - 3, radius * 2, radius * 2);
      }
      // A rounded focus ring keeps the button usable with the keyboard
      if (button.isFocusOwner() && button.isEnabled()) {
        graphics2D.setColor(Theme.withAlpha(Theme.ACCENT, 150));
        graphics2D.setStroke(new BasicStroke(1.2f));
        graphics2D.drawRoundRect(3, 3, width - 7, height - 7, radius * 2, radius * 2);
      }

      graphics2D.dispose();

      // Text and icon are painted on top of the background
      super.paint(graphics, component);
    }

    @Override
    protected void paintButtonPressed(Graphics graphics, AbstractButton button) {
      // The pressed state is already part of the rounded background in paint()
    }

    @Override
    protected void paintFocus(
        Graphics graphics,
        AbstractButton button,
        Rectangle viewRect,
        Rectangle textRect,
        Rectangle iconRect) {
      // The focus ring is drawn in paint(), so the default rectangle is not needed
    }

    private int radius(AbstractButton button) {
      Object radius = button.getClientProperty(RADIUS);
      return radius instanceof Integer ? (Integer) radius : 12;
    }
  }
}
