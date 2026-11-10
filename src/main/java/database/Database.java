/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this
 * license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
    }

    this.gsonlist = gson.fromJson(readFileFromResource(languages.get(language)), listType);
  }
  
  
  
  /** Reads the games that were played and saved to PlayedGames.json. */
  public Database() {
    File file = new File("PlayedGames.json");
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

  public Snippet getRandomSnippet() {

    if (this.gsonlist != null) {
      int r1 = position.nextInt(this.gsonlist.size());
      Snippet snip = this.gsonlist.get(r1);
      return snip;
    } else {
      System.out.println("Snippet list is empty");
      return null;
    }
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
  
  public void deleteEntry() {
  System.out.println("Works so far");
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
