package database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Resolves writable per-user storage for settings and race history. */
public final class AppDataPaths {

  private static final Path DATA_DIRECTORY =
      Path.of(System.getProperty("user.home"), ".coderacer");

  private AppDataPaths() {}

  /** Returns a data file path, migrating a legacy working-directory file on first use. */
  public static Path file(String fileName) {
    Path destination = DATA_DIRECTORY.resolve(fileName);
    if (Files.exists(destination)) {
      return destination;
    }

    try {
      Files.createDirectories(DATA_DIRECTORY);
      Path legacy = Path.of(fileName).toAbsolutePath();
      if (Files.isRegularFile(legacy)) {
        Files.copy(legacy, destination, StandardCopyOption.COPY_ATTRIBUTES);
      }
    } catch (IOException e) {
      // A later read or save reports the storage error without preventing the app from starting.
    }
    return destination;
  }
}
