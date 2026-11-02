/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this
 * license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
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
    }

    this.gsonlist = gson.fromJson(readFileFromResource(languages.get(language)), listType);
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
}
