/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 * @author mathi
 */
public class PlayedGamesDatabase extends PlayedGame {
    private static final File theFile = new File("PlayedGames.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    
   
    public PlayedGamesDatabase(String typedText, double wpm, double time, double accuracy) {
        super(typedText, wpm, time, accuracy);
    }
    
    
    @Override
    public void InsertData() {
       
       ArrayList<PlayedGamesDatabase> data = new ArrayList<>();     
        
        if (theFile.exists() && theFile.length() > 0) {
            try (FileReader reader = new FileReader(theFile)) { 
                 Type type = new TypeToken<ArrayList<PlayedGamesDatabase>>(){}.getType();
                 data = gson.fromJson(reader, type);
                 
            } catch (IOException ex) {
                System.getLogger(PlayedGamesDatabase.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        
        }
        
        data.add(this);
        
        //Writing the full list back
        try (FileWriter fileWriter = new FileWriter(theFile);){
        gson.toJson(data, fileWriter);
        } catch (Exception e) {
              e.printStackTrace();
        }
        
        finally {
            System.out.println("Added succesfully");
        }
        
    }
}
