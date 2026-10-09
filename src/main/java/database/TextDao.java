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
/**
 *
 * @author maril
 */
public class TextDao {
    Gson gson= new Gson();
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

    Type listType = new TypeToken<List<Snippet>>() {}.getType();
    List<Snippet> gsonlist = gson.fromJson(readFileFromResource("/DB.json"), listType);
    public static void main(String[] args) {
    TextDao dao = new TextDao();
    System.out.println(dao.gsonlist.size());
}
}
