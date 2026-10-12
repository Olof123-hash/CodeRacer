/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.util.List;
import com.google.gson.Gson;
import model.Snippet;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.Random;

/**
 *
 * @author maril
 */
public class TextDao {

    Random position = new Random();
    Gson gson = new Gson();

    static String readFileFromResource(String filePath) {
        InputStream inputStream = Snippet.class.getResourceAsStream(filePath);
        String result = null;
        if (inputStream != null) {
            result = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining("\n"));
        }
        return result;
    }

    Type listType = new TypeToken<List<Snippet>>() {
    }.getType();
    List<Snippet> gsonlist = gson.fromJson(readFileFromResource("/DB.json"), listType);

    public Snippet getRandomSnippet() {
        if (gsonlist != null) {
            int r1 = position.nextInt(gsonlist.size());
            Snippet snip = gsonlist.get(r1);
            return snip;
        } else {
            System.out.println("Snippet list is empty");
            return null;
        }
    }
    
  
}


