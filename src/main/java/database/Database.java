/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this
 * license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import model.Snippet;

/**
 * @author maril
 */
public class Database {

  public String language;


  public List<Snippet> gsonlist;
  public HashMap<String, String> languages = new HashMap<>();
  // The games that were played, read by the constructor without a language
  private List<PlayedGamesDatabase> playedGames = new ArrayList<>();

  Random position = new Random();
  Gson gson = new Gson();

  public Database(String language) {

    // The key of the file that is read is kept, so getRandomSnippet can tell random words apart
    this.language = language;

    if (languages.isEmpty()) {
      languages.put("Random", "/Random.json");
      languages.put("Java", "/Java.json");
      languages.put("JavaScript", "/JavaScript.json");
      languages.put("Python", "/Python.json");
      languages.put("C++", "/C++.json");
      languages.put("Rust", "/Rust.json");
      languages.put("Go", "/Go.json");
      languages.put("Assembly", "/Assembly.json");
      languages.put("Test", "/test.json");
      // The two plain text sources, used when the player switched code snippets off. "Words" is a
      // list of single words that getRandomSnippet joins into a line, "Text" is a list of passages.
      languages.put("Words", "/Words.json");
      languages.put("Text", "/Text.json");
    }

    // An unknown key (for example from an old settings file) falls back to the mixed code list
    String resource = languages.getOrDefault(language, languages.get("Random"));
    String json = readFileFromResource(resource);
    if ("Text".equals(language)) {
      // Text.json wraps its passages in a top-level "posts" object; other snippet files are arrays.
      JsonObject root = JsonParser.parseString(json).getAsJsonObject();
      JsonArray posts = root.getAsJsonArray("posts");
      this.gsonlist = gson.fromJson(posts, listType);
    } else {
      this.gsonlist = gson.fromJson(json, listType);
    }
  }
  
  
  
  /** Reads the games that were played and saved to PlayedGames.json. */
  public Database() {
    File file = AppDataPaths.file("PlayedGames.json").toFile();
    if (!file.exists() || file.length() == 0) {
      return;
    }
    Type playedGamesType = new TypeToken<List<PlayedGamesDatabase>>() {}.getType();
    try (Reader reader = new FileReader(file)) {
      List<PlayedGamesDatabase> games = gson.fromJson(reader, playedGamesType);
      if (games != null) {
        this.playedGames = games;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static String readFileFromResource(String filePath) {
    InputStream inputStream = Snippet.class.getResourceAsStream(filePath);
    String result = null;
    if (inputStream != null) {
      result =
          new BufferedReader(new InputStreamReader(inputStream))
              .lines()
              .collect(Collectors.joining("\n"));
    }
    return result;
  }

  Type listType = new TypeToken<List<Snippet>>() {}.getType();

  public Snippet getSnippet() {

    if (this.gsonlist != null && !this.gsonlist.isEmpty()) {
      // The word list holds single words, a race always uses a line of them
      if ("Words".equals(language)) {
        return randomWords();
      }
      int r1 = position.nextInt(this.gsonlist.size());
      Snippet snip = this.gsonlist.get(r1);
      return snip;
    } else {
      System.out.println("Snippet list is empty");
      return null;
    }
  }

  /** Builds a line of random words from the word list, so a text race is long enough to type. */
  private Snippet randomWords() {
    // Between 10 and 14 words, which is about the length of the shorter code snippets
    int wordCount = 10 + position.nextInt(5);
    StringBuilder line = new StringBuilder();
    for (int word = 0; word < wordCount; word++) {
      if (word > 0) {
        line.append(' ');
      }
      line.append(this.gsonlist.get(position.nextInt(this.gsonlist.size())).getBody());
    }
    return new Snippet(line.toString());
  }
  
  /**
   * Returns the games that were played as readable lines, in the order in which they were played.
   * Every entry starts with the statistics of the game and has the first line of the typed code
   * below it, so the history screen can simply show the values of this map.
   */
  public Map<Integer, String> getDatabase() {
    // Linkedmap to keep entries in order
    Map<Integer, String> map = new LinkedHashMap<>();
    for (int i = 0; i < playedGames.size(); i++) {
      map.put(i, describe(playedGames.get(i)));
    }
    return map;
  }
  
  public void deleteEntry(int position) {
    playedGames.remove(position);
    
    //Saves the updated playedGames and writes it to the json file
     try (Writer writer = new FileWriter(AppDataPaths.file("PlayedGames.json").toFile())) {
    gson.toJson(playedGames, writer);
  } catch (IOException e) {
    e.printStackTrace();
  }
  }

  /** Turns one played game into the lines that the history screen shows. */
  private String describe(PlayedGame game) {
    String statistics =
        String.format(
            "WPM : %4d   ACCURACY : %3d%%  TIME : %3ds  MISTAKES: %3d",
            Math.round(game.getWpm()),
            Math.round(game.getAccuracy()),
            Math.round(game.getTime()),
            game.getMistakes());
    String typed = firstLine(game.getTypedText());
    if (typed.isEmpty()) {
      return statistics;
    }
    return statistics + "\n      " + typed;
  }

  /** Returns the first line of the typed text, shortened so it fits on the history screen. */
  private String firstLine(String text) {
    if (text == null || text.isBlank()) {
      return "";
    }
    String line = text.strip().split("\\R", 2)[0].strip();
    return line.length() > 60 ? line.substring(0, 57) + "..." : line;
  }
}
