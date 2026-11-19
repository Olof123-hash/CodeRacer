package database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import model.Settings;

/**
 * Reads and writes the player's preferences in {@code Settings.json}, next to {@code
 * PlayedGames.json}.
 *
 * <p>The rest of the application works with one {@link Settings} object in memory. This class is
 * only the storage layer: it loads that object once at startup and writes it whenever the settings
 * window changes something. A missing, empty or broken file falls back to the defaults, and a save
 * writes to a temporary file first so an interrupted write can not leave half a JSON document
 * behind.
 */
public final class SettingsStore {

  /** The file the settings are saved to, in the working directory of the application. */
  public static final String FILE_NAME = "Settings.json";

  private static final Path FILE = Path.of(FILE_NAME);

  // Pretty printing keeps the file readable when it is opened by hand
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private SettingsStore() {
    // Utility class, no instances
  }

  /**
   * Reads the settings from {@code Settings.json}. When the file is missing, empty or broken, a
   * fresh object with the defaults is returned, so a first start and a damaged file both end up in
   * a working application.
   */
  public static Settings load() {
    if (!Files.isRegularFile(FILE)) {
      return new Settings();
    }
    try {
      String json = Files.readString(FILE, StandardCharsets.UTF_8);
      Settings settings = GSON.fromJson(json, Settings.class);
      if (settings == null) {
        return new Settings();
      }
      // Gson writes fields directly, so the clamping and masking of the setters has to run once
      settings.normalise();
      return settings;
    } catch (IOException | RuntimeException e) {
      // A settings file that can not be read should never stop the game from starting
      return new Settings();
    }
  }

  /** Writes the current settings to {@code Settings.json}. */
  public static void save(Settings settings) {
    if (settings == null) {
      return;
    }
    Path temporary = FILE.resolveSibling(FILE.getFileName() + ".tmp");
    try {
      Files.writeString(temporary, GSON.toJson(settings), StandardCharsets.UTF_8);
      try {
        Files.move(
            temporary,
            FILE,
            StandardCopyOption.REPLACE_EXISTING,
            StandardCopyOption.ATOMIC_MOVE);
      } catch (AtomicMoveNotSupportedException e) {
        // Some file systems do not support an atomic move; a normal replace is still safe enough
        Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      // Not being able to save must not crash the game; the value still applies in memory
      e.printStackTrace();
      try {
        Files.deleteIfExists(temporary);
      } catch (IOException ignored) {
        // Nothing else can be done about a leftover temporary file
      }
    }
  }
}
