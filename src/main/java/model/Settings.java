package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * The preferences of the player, as one object. The settings window writes into it, the race
 * screen reads from it, and it saves itself to {@code Settings.json} next to {@code
 * PlayedGames.json}.
 *
 * <p>The application creates one instance when it starts ({@link #load()}), passes it to the
 * settings window and keeps it: the getters always return what is in the file at the moment the
 * player changes something, because the settings window updates this object directly.
 *
 * <p>Every value is a plain String, boolean or int, so Gson can write and read the file without
 * any custom code. Unknown or missing values fall back to the defaults, a broken file never stops
 * the application from starting.
 */
public class Settings {

  /** The file the settings are saved to, in the working directory of the application. */
  public static final String FILE_NAME = "Settings.json";

  /** The scheme that is used when the player has not chosen one yet. */
  public static final String DEFAULT_SCHEME = "Ember (default)";

  /** The background of the race screen that is used when the player has not chosen one yet. */
  public static final String DEFAULT_BACKGROUND = "Black and grey (default)";

  /** The value of {@link #getTextSource()} that means random words. */
  public static final String TEXT_SOURCE_WORDS = "Random words";

  /** The value of {@link #getTextSource()} that means a passage from a text. */
  public static final String TEXT_SOURCE_PASSAGE = "Text passage";

  /** The language that is used when the player has not chosen one yet. */
  public static final String DEFAULT_LANGUAGE = "Random";

  /** The size of the snippet text that is used when the player has not chosen one yet. */
  public static final int DEFAULT_SNIPPET_FONT_SIZE = 22;

  /** The smallest snippet text the player can pick, below this the code is too small to read. */
  public static final int MIN_SNIPPET_FONT_SIZE = 12;

  /** The biggest snippet text the player can pick; bigger text simply scrolls more. */
  public static final int MAX_SNIPPET_FONT_SIZE = 40;

  // Pretty printing keeps the file readable when it is opened by hand
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  // The colour of the custom scheme until the player picks one, it is the accent of Ember
  /** The accent colour of the custom scheme until the player picks one. */
  public static final int DEFAULT_CUSTOM_ACCENT_RGB = 0xff8a1f;

  /** The colour of the blinking caret until the player picks one: the accent of Ember. */
  public static final int DEFAULT_CARET_COLOUR_RGB = 0xff8a1f;

  /** The colour of a character that was typed correctly until the player picks one. */
  public static final int DEFAULT_CORRECT_COLOUR_RGB = 0x4ade80;

  /** The colour of a character that was typed wrongly until the player picks one. */
  public static final int DEFAULT_ERROR_COLOUR_RGB = 0xd1334a;

  /** The colour of the text that is still waiting to be typed until the player picks one. */
  public static final int DEFAULT_TEXT_COLOUR_RGB = 0xd8d4cc;

  // The values that are saved; the defaults are the values the application started with. The three
  // typing colours are RGB values without alpha, so they are written as plain numbers in the file.
  private String scheme = DEFAULT_SCHEME;
  private String background = DEFAULT_BACKGROUND;
  private int customAccent = DEFAULT_CUSTOM_ACCENT_RGB;
  private boolean codeMode = true;
  private String textSource = TEXT_SOURCE_WORDS;
  private String language = DEFAULT_LANGUAGE;
  private int caretColour = DEFAULT_CARET_COLOUR_RGB;
  private int correctColour = DEFAULT_CORRECT_COLOUR_RGB;
  private int errorColour = DEFAULT_ERROR_COLOUR_RGB;
  private int textColour = DEFAULT_TEXT_COLOUR_RGB;
  private int snippetFontSize = DEFAULT_SNIPPET_FONT_SIZE;

  /**
   * Reads the settings from Settings.json. When the file is missing or broken, the defaults are
   * returned instead, so a first start and a damaged file both end up in a working application.
   */
  public static Settings load() {
    File file = new File(FILE_NAME);
    if (!file.exists() || file.length() == 0) {
      return new Settings();
    }
    try (Reader reader = new FileReader(file)) {
      Settings settings = GSON.fromJson(reader, Settings.class);
      if (settings == null) {
        return new Settings();
      }
      // Gson leaves a value null when the file contains a null instead of a name
      if (settings.scheme == null) {
        settings.scheme = DEFAULT_SCHEME;
      }
      if (settings.background == null) {
        settings.background = DEFAULT_BACKGROUND;
      }
      if (settings.textSource == null) {
        settings.textSource = TEXT_SOURCE_WORDS;
      }
      if (settings.language == null) {
        settings.language = DEFAULT_LANGUAGE;
      }
      return settings;
    } catch (IOException | RuntimeException e) {
      // A settings file that can not be read should never stop the game from starting
      return new Settings();
    }
  }

  /** Writes the current settings to Settings.json. */
  public void save() {
    try (Writer writer = new FileWriter(FILE_NAME)) {
      GSON.toJson(this, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** The name of the colour scheme, for example "Ember". */
  public String getScheme() {
    return scheme;
  }

  public void setScheme(String scheme) {
    this.scheme = scheme == null ? DEFAULT_SCHEME : scheme;
  }

  /**
   * The size of the snippet text, in points. It is kept inside the range that the settings window
   * offers, so a broken or old file can never make the text unreadable or huge.
   */
  public int getSnippetFontSize() {
    return snippetFontSize;
  }

  public void setSnippetFontSize(int snippetFontSize) {
    this.snippetFontSize =
        Math.max(MIN_SNIPPET_FONT_SIZE, Math.min(MAX_SNIPPET_FONT_SIZE, snippetFontSize));
  }

  /** The background of the race screen, for example "Black and grey". */
  public String getBackground() {
    return background;
  }

  public void setBackground(String background) {
    this.background = background == null ? DEFAULT_BACKGROUND : background;
  }

  /** The accent colour of the custom scheme, as the RGB value of a {@link java.awt.Color}. */
  public int getCustomAccent() {
    return customAccent;
  }

  public void setCustomAccent(int customAccent) {
    this.customAccent = customAccent;
  }

  /** Tells whether the player types code snippets, instead of plain text. */
  public boolean isCodeMode() {
    return codeMode;
  }

  public void setCodeMode(boolean codeMode) {
    this.codeMode = codeMode;
  }

  /**
   * The kind of plain text that is used: {@link #TEXT_SOURCE_WORDS} or
   * {@link #TEXT_SOURCE_PASSAGE}.
   */
  public String getTextSource() {
    return textSource;
  }

  public void setTextSource(String textSource) {
    this.textSource = textSource == null ? TEXT_SOURCE_WORDS : textSource;
  }

  /** The language of the code snippets, for example "Python" or "Random". */
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language == null ? DEFAULT_LANGUAGE : language;
  }

  /** The colour of the caret in the typing pane, as the RGB value of a {@link java.awt.Color}. */
  public int getCaretColour() {
    return caretColour;
  }

  public void setCaretColour(int caretColour) {
    this.caretColour = caretColour & 0xffffff;
  }

  /** The colour of a character that was typed correctly, as an RGB value. */
  public int getCorrectColour() {
    return correctColour;
  }

  public void setCorrectColour(int correctColour) {
    this.correctColour = correctColour & 0xffffff;
  }

  /** The colour of a character that was typed wrongly, as an RGB value. */
  public int getErrorColour() {
    return errorColour;
  }

  public void setErrorColour(int errorColour) {
    this.errorColour = errorColour & 0xffffff;
  }

  /**
   * The colour of the text that the player has not typed yet, as an RGB value. It is the general
   * colour of the snippet, the one every character has before it is typed.
   */
  public int getTextColour() {
    return textColour;
  }

  public void setTextColour(int textColour) {
    this.textColour = textColour & 0xffffff;
  }
}
