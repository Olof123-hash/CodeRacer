package UI;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * The colour schemes the player can choose from in the settings window, and the palettes behind
 * them. A scheme holds every colour that {@link Theme} uses for the interface, so switching a
 * scheme changes the whole application at once.
 *
 * <p>The semantic colours (the red of a mistake and the green of a correct character) are the same
 * in every scheme, they are not part of a palette: a mistake should look like a mistake whatever
 * the player picked.
 *
 * <p>{@link #CUSTOM} has no palette of its own. Its neutrals are the ones of {@link #EMBER} and its
 * accent is the colour the player picked, see
 * {@link Theme#applyScheme(ColourScheme, Color)}.
 */
public enum ColourScheme {

  /** The original scheme: near black with an orange accent. */
  EMBER(
      "Ember",
      0x0a0a0a, 0x141414, 0x1f1f1f, 0x5c5c5c,
      0xd8d4cc, 0x8f8a82, 0xff8a1f, 0xffa64d, 0x7a3f06),

  /** A deep navy background with a bright blue accent. */
  OCEAN(
      "Ocean",
      0x061019, 0x0c1c29, 0x14303f, 0x3f5b6b,
      0xd6e3ea, 0x8298a5, 0x35b6ff, 0x6bcbff, 0x0f4a6b),

  /** A dark forest green with a fresh green accent. */
  FOREST(
      "Forest",
      0x08130c, 0x0f1f15, 0x18301f, 0x46604f,
      0xd9e6dc, 0x86988b, 0x4ade80, 0x7beba4, 0x1d5c34),

  /** A dark violet background with a lavender accent. */
  GRAPE(
      "Grape",
      0x0d0814, 0x171020, 0x241a33, 0x5b4a70,
      0xe0d8ea, 0x948aa3, 0xb07cff, 0xcba6ff, 0x4a2a7a),

  /** Warm browns with a sandy gold accent. */
  SAND(
      "Sand",
      0x14100a, 0x201a11, 0x2e2618, 0x6b5c45,
      0xeee4d2, 0xa3957d, 0xe0a94a, 0xf2c477, 0x6d4d16),

  /**
   * The accent colour the player picked themselves. The neutrals are the ones of {@link #EMBER};
   * only the accent family changes.
   */
  CUSTOM(
      "Custom",
      0x0a0a0a, 0x141414, 0x1f1f1f, 0x5c5c5c,
      0xd8d4cc, 0x8f8a82, 0xff8a1f, 0xffa64d, 0x7a3f06);

  private final String displayName;
  private final Color background;
  private final Color backgroundAlt;
  private final Color surface;
  private final Color subtle;
  private final Color text;
  private final Color textMuted;
  private final Color accent;
  private final Color accentBright;
  private final Color accentDim;

  ColourScheme(
      String displayName,
      int background,
      int backgroundAlt,
      int surface,
      int subtle,
      int text,
      int textMuted,
      int accent,
      int accentBright,
      int accentDim) {
    this.displayName = displayName;
    this.background = new Color(background);
    this.backgroundAlt = new Color(backgroundAlt);
    this.surface = new Color(surface);
    this.subtle = new Color(subtle);
    this.text = new Color(text);
    this.textMuted = new Color(textMuted);
    this.accent = new Color(accent);
    this.accentBright = new Color(accentBright);
    this.accentDim = new Color(accentDim);
  }

  /** The name that is shown in the settings window. */
  public String displayName() {
    return displayName;
  }

  public Color background() {
    return background;
  }

  public Color backgroundAlt() {
    return backgroundAlt;
  }

  public Color surface() {
    return surface;
  }

  public Color subtle() {
    return subtle;
  }

  public Color text() {
    return text;
  }

  public Color textMuted() {
    return textMuted;
  }

  public Color accent() {
    return accent;
  }

  public Color accentBright() {
    return accentBright;
  }

  public Color accentDim() {
    return accentDim;
  }

  /** Returns the scheme with the given display name; an unknown name falls back to Ember. */
  public static ColourScheme byName(String name) {
    if (name != null) {
      for (ColourScheme scheme : values()) {
        if (scheme.displayName.equalsIgnoreCase(name)) {
          return scheme;
        }
      }
    }
    return EMBER;
  }

  /** The display names of all schemes, in the order in which the settings window shows them. */
  public static String[] displayNames() {
    ColourScheme[] schemes = values();
    List<String> names = new ArrayList<>();
    for (ColourScheme scheme : schemes) {
      names.add(scheme.displayName);
    }
    return names.toArray(new String[0]);
  }
}
