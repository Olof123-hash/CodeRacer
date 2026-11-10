package UI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * The single place where the colours, the fonts and the borders of the whole application are
 * defined. Every styler (ButtonStyler, LabelStyler, ...) uses these values, so the look of the
 * application can be changed here without touching the screens themselves.
 *
 * <p>The palette is a dark "code editor" theme: near black backgrounds, an off white text colour
 * and one orange accent colour that is used for everything the player is supposed to look at.
 */
public final class Theme {

  private Theme() {
    // Utility class, no instances
  }

  // ------------------------------------------------------------------------------- Palette

  /** The colour of the window background. */
  public static final Color BACKGROUND = new Color(0x0a0a0a);
  /** A slightly lighter background, used for panels, fields and buttons. */
  public static final Color BACKGROUND_ALT = new Color(0x141414);
  /** The background that appears when the mouse hovers over a component. */
  public static final Color SURFACE = new Color(0x1f1f1f);
  /** The colour of borders, scrollbar thumbs and other quiet details. */
  public static final Color SUBTLE = new Color(0x5c5c5c);
  /** The normal text colour. */
  public static final Color TEXT = new Color(0xd8d4cc);
  /** The text colour for hints and captions. */
  public static final Color TEXT_MUTED = new Color(0x8f8a82);
  /** The main accent colour of the application. */
  public static final Color ACCENT = new Color(0xff8a1f);
  /** The accent colour that is used when the mouse hovers over something. */
  public static final Color ACCENT_BRIGHT = new Color(0xffa64d);
  /** A dark version of the accent, used for filled areas. */
  public static final Color ACCENT_DIM = new Color(0x7a3f06);
  /** The colour of text that was typed correctly. */
  public static final Color SUCCESS = new Color(0x4ade80);
  
  /** The colour of text that was typed correctly. */
  public static final Color GREEN = new Color(0, 255, 0);
  /** The colour of a mistake. */
  public static final Color ERROR = new Color(0xd1334a);
  /** The dark red that is used as the background of a dangerous button. */
  public static final Color ERROR_DIM = new Color(0x6b1a26);

  // --------------------------------------------------------------------------------- Fonts

  // If the team drops font files in src/main/resources/fonts they are picked up automatically.
  private static final String MONO_REGULAR = "/fonts/RobotoMono-Regular.ttf";
  private static final String MONO_BOLD = "/fonts/RobotoMono-Bold.ttf";
  private static final String UI_REGULAR = "/fonts/MonaSans-Regular.ttf";
  private static final String UI_BOLD = "/fonts/MonaSans-Bold.ttf";

  private static final String[] MONO_FALLBACK = {
    "JetBrains Mono", "Roboto Mono", "Cascadia Mono", "Consolas", "Courier New", "Monospaced"
  };
  private static final String[] UI_FALLBACK = {
    "Mona Sans", "Segoe UI", "Inter", "Tahoma", "SansSerif"
  };

  // Loaded fonts are cached, so a font file is only read once
  private static final Map<String, Font> BUNDLED_FONTS = new HashMap<>();
  private static final Set<String> AVAILABLE_FAMILIES =
      new HashSet<>(
          Arrays.asList(
              GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));

  /** The monospaced font that is used for code, at the given size. */
  public static Font mono(int size) {
    return font(MONO_REGULAR, MONO_FALLBACK, Font.PLAIN, size);
  }

  /** The bold version of {@link #mono(int)}. */
  public static Font monoBold(int size) {
    return font(MONO_BOLD, MONO_FALLBACK, Font.BOLD, size);
  }

  /** The normal interface font, at the given size. */
  public static Font ui(int size) {
    return font(UI_REGULAR, UI_FALLBACK, Font.PLAIN, size);
  }

  /** The bold version of {@link #ui(int)}. */
  public static Font uiBold(int size) {
    return font(UI_BOLD, UI_FALLBACK, Font.BOLD, size);
  }

  private static Font font(String resource, String[] fallback, int style, int size) {
    Font loaded = bundledFont(resource);
    if (loaded != null) {
      // The file is the regular weight, other weights are derived from it
      return loaded.deriveFont(style, (float) size);
    }
    return new Font(firstAvailableFamily(fallback), style, size);
  }

  /** Reads a font from the resources folder and returns null when it is not there. */
  private static Font bundledFont(String resource) {
    if (!BUNDLED_FONTS.containsKey(resource)) {
      BUNDLED_FONTS.put(resource, loadFont(resource));
    }
    return BUNDLED_FONTS.get(resource);
  }

  private static Font loadFont(String resource) {
    try (InputStream stream = Theme.class.getResourceAsStream(resource)) {
      if (stream == null) {
        return null;
      }
      return Font.createFont(Font.TRUETYPE_FONT, stream);
    } catch (FontFormatException | IOException e) {
      // A broken or missing font should never stop the game from starting
      return null;
    }
  }

  /** Returns the first font family of the list that is installed on this computer. */
  private static String firstAvailableFamily(String[] families) {
    for (String family : families) {
      if (AVAILABLE_FAMILIES.contains(family)) {
        return family;
      }
    }
    return families[families.length - 1];
  }

  // ------------------------------------------------------------------------------ Painting

  /** Turns on anti aliasing for a graphics object, so rounded shapes look smooth. */
  public static void enableQuality(Graphics2D graphics) {
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
  }

  /** Returns the same colour with a different transparency (0 = invisible, 255 = solid). */
  public static Color withAlpha(Color color, int alpha) {
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
  }

  /** Returns a darker version of a colour, used for the pressed state of a button. */
  public static Color darker(Color color) {
    return new Color(
        Math.max(0, (int) (color.getRed() * 0.8)),
        Math.max(0, (int) (color.getGreen() * 0.8)),
        Math.max(0, (int) (color.getBlue() * 0.8)),
        color.getAlpha());
  }

  // ------------------------------------------------------------------------------ Borders

  /** A rounded border without extra padding. */
  public static Border roundedBorder(Color color, int radius, int thickness) {
    return new RoundedBorder(color, radius, thickness, thickness, thickness);
  }

  /** A rounded border with extra space between the border and the content. */
  public static Border roundedBorder(
      Color color, int radius, int thickness, int horizontalPadding, int verticalPadding) {
    return new RoundedBorder(color, radius, thickness, horizontalPadding, verticalPadding);
  }

  /** A border that draws a rounded rectangle, used for buttons, combo boxes and cards. */
  public static final class RoundedBorder implements Border {

    private final Color color;
    private final int radius;
    private final int thickness;
    private final Insets insets;

    private RoundedBorder(
        Color color, int radius, int thickness, int horizontalPadding, int verticalPadding) {
      this.color = color;
      this.radius = radius;
      this.thickness = Math.max(1, thickness);
      this.insets =
          new Insets(
              this.thickness + verticalPadding,
              this.thickness + horizontalPadding,
              this.thickness + verticalPadding,
              this.thickness + horizontalPadding);
    }

    @Override
    public Insets getBorderInsets(Component component) {
      return insets;
    }

    @Override
    public boolean isBorderOpaque() {
      return false;
    }

    @Override
    public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
      Graphics2D graphics2D = (Graphics2D) graphics.create();
      enableQuality(graphics2D);
      graphics2D.setColor(color);
      graphics2D.setStroke(new BasicStroke(thickness));
      int offset = thickness / 2;
      graphics2D.drawRoundRect(
          x + offset,
          y + offset,
          width - thickness,
          height - thickness,
          radius * 2,
          radius * 2);
      graphics2D.dispose();
    }
  }

  // ------------------------------------------------------------------------ Global defaults

  /**
   * Sets the colours that Swing components use internally, for example the selection colour of a
   * combo box popup or the colour of a tooltip. This has to be called once before the first window
   * is created.
   */
  public static void installDefaults() {
    UIManager.put("Panel.background", BACKGROUND);
    UIManager.put("OptionPane.background", BACKGROUND_ALT);
    UIManager.put("OptionPane.messageForeground", TEXT);

    UIManager.put("ToolTip.background", BACKGROUND_ALT);
    UIManager.put("ToolTip.foreground", TEXT);
    UIManager.put("ToolTip.font", ui(12));

    UIManager.put("List.background", BACKGROUND_ALT);
    UIManager.put("List.foreground", TEXT);
    UIManager.put("List.selectionBackground", BACKGROUND);
    UIManager.put("List.selectionForeground", ACCENT);

    UIManager.put("ComboBox.background", BACKGROUND_ALT);
    UIManager.put("ComboBox.foreground", TEXT);
    UIManager.put("ComboBox.selectionBackground", BACKGROUND);
    UIManager.put("ComboBox.selectionForeground", ACCENT);

    UIManager.put("TextField.caretForeground", ACCENT);
    UIManager.put("TextArea.caretForeground", ACCENT);
    UIManager.put("TextPane.caretForeground", ACCENT);

    UIManager.put("ScrollBar.background", BACKGROUND);
    UIManager.put("ScrollBar.thumb", SUBTLE);
    UIManager.put("ScrollBar.track", BACKGROUND);
  }
}
