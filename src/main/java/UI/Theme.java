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

  // The colours of the active scheme. They are not final, because the player can switch schemes
  // while the application is running; Theme.applyScheme replaces them and the screens re-apply
  // their stylers. Everything that paints reads these fields at paint time, so it follows a new
  // scheme without any extra work.

  /** The colour of the window background. */
  public static Color BACKGROUND = new Color(0x0a0a0a);
  /** A slightly lighter background, used for panels, fields and buttons. */
  public static Color BACKGROUND_ALT = new Color(0x141414);
  /** The background that appears when the mouse hovers over a component. */
  public static Color SURFACE = new Color(0x1f1f1f);
  /** The colour of borders, scrollbar thumbs and other quiet details. */
  public static Color SUBTLE = new Color(0x5c5c5c);
  /** The normal text colour. */
  public static Color TEXT = new Color(0xd8d4cc);
  /** The text colour for hints and captions. */
  public static Color TEXT_MUTED = new Color(0x8f8a82);
  /** The main accent colour of the application. */
  public static Color ACCENT = new Color(0xff8a1f);
  /** The accent colour that is used when the mouse hovers over something. */
  public static Color ACCENT_BRIGHT = new Color(0xffa64d);
  /** A dark version of the accent, used for filled areas. */
  public static Color ACCENT_DIM = new Color(0x7a3f06);

  // The four colours of typing are settings of their own, so they do not change when the player
  // switches scheme. They are not final either, because the settings window can change them while
  // the race screen is open; Theme.applyTypingColours replaces them.

  /** The colour of the caret in the typing pane. */
  public static Color CARET = new Color(0xff8a1f);
  /** The colour of a character that the player typed correctly. */
  public static Color TYPED_CORRECT = new Color(0x4ade80);
  /** The colour of a character that the player typed wrongly. */
  public static Color TYPED_ERROR = new Color(0xd1334a);
  /** The colour of the text that the player has not typed yet, so everything left to type. */
  public static Color UNTYPED_TEXT = new Color(0xd8d4cc);

  /** The colour of a mistake in the interface, for example the close button of a window. */
  public static final Color ERROR = new Color(0xd1334a);
  /** The dark red that is used as the background of a dangerous button. */
  public static final Color ERROR_DIM = new Color(0x6b1a26);

  // The scheme that is active now, so the settings window can show which one is selected
  private static ColourScheme currentScheme = ColourScheme.EMBER;

  // ------------------------------------------------------------------------- Applying a scheme

  /** The scheme that is active at the moment. */
  public static ColourScheme currentScheme() {
    return currentScheme;
  }

  /**
   * Replaces the whole palette with the palette of a scheme. For {@link ColourScheme#CUSTOM} the
   * neutral colours of Ember are used and the accent is the colour the player picked, from which a
   * brighter and a darker version are derived so hover states and filled areas stay readable.
   *
   * <p>The defaults that Swing itself uses are set again as well, so components that are created
   * after the change (a popup list, for example) start with the new palette. Components that are
   * already on the screen have to be styled again by their window, they keep the colours that were
   * set when they were created.
   */
  public static void applyScheme(ColourScheme scheme, Color customAccent) {
    ColourScheme palette = scheme == null ? ColourScheme.EMBER : scheme;
    if (palette == ColourScheme.CUSTOM) {
      // A custom scheme keeps the neutral colours of Ember and only changes the accent family
      ColourScheme base = ColourScheme.EMBER;
      BACKGROUND = base.background();
      BACKGROUND_ALT = base.backgroundAlt();
      SURFACE = base.surface();
      SUBTLE = base.subtle();
      TEXT = base.text();
      TEXT_MUTED = base.textMuted();

      Color accent = customAccent == null ? base.accent() : customAccent;
      ACCENT = accent;
      ACCENT_BRIGHT = blend(accent, Color.WHITE, 0.30);
      ACCENT_DIM = blend(accent, base.background(), 0.55);
    } else {
      BACKGROUND = palette.background();
      BACKGROUND_ALT = palette.backgroundAlt();
      SURFACE = palette.surface();
      SUBTLE = palette.subtle();
      TEXT = palette.text();
      TEXT_MUTED = palette.textMuted();
      ACCENT = palette.accent();
      ACCENT_BRIGHT = palette.accentBright();
      ACCENT_DIM = palette.accentDim();
    }
    currentScheme = palette;
    installDefaults();
  }

  /**
   * Replaces the colours that are used while typing: the caret, the colour of text that was typed
   * right or wrong, and the colour of the text that is still waiting to be typed. They are not part
   * of a colour scheme, so switching scheme does not touch them; only the settings window does. A
   * null value keeps the colour that is in use, which is handy when only one of them changed.
   *
   * <p>The caret is set on the text panes by {@link TextStyler}, the other three are read by
   * {@code RacePanel.colorTypingField()} every time the text of the race changes.
   */
  public static void applyTypingColours(Color caret, Color correct, Color error, Color untyped) {
    if (caret != null) {
      CARET = caret;
    }
    if (correct != null) {
      TYPED_CORRECT = correct;
    }
    if (error != null) {
      TYPED_ERROR = error;
    }
    if (untyped != null) {
      UNTYPED_TEXT = untyped;
    }
  }

  /** Returns a colour that is mixed with another one; 0 keeps the first, 1 returns the second. */
  public static Color blend(Color from, Color to, double ratio) {
    double amount = Math.max(0.0, Math.min(1.0, ratio));
    return new Color(
        (int) Math.round(from.getRed() + (to.getRed() - from.getRed()) * amount),
        (int) Math.round(from.getGreen() + (to.getGreen() - from.getGreen()) * amount),
        (int) Math.round(from.getBlue() + (to.getBlue() - from.getBlue()) * amount),
        from.getAlpha());
  }

  /** The accent of a scheme, used for the swatches in the settings window. */
  public static Color accentOf(ColourScheme scheme, Color customAccent) {
    if (scheme == ColourScheme.CUSTOM) {
      return customAccent == null ? ColourScheme.EMBER.accent() : customAccent;
    }
    return scheme == null ? ColourScheme.EMBER.accent() : scheme.accent();
  }

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
