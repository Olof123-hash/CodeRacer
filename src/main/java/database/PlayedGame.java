/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

/**
 *
 * @author mathi
 */
public class PlayedGame {
    private String typedText;
    private double wpm;
    private double time;
    private double accuracy;
    
    
    public PlayedGame(String typedText, double wpm, double time, double accuracy) {
        this.typedText = typedText;
        this.wpm = wpm;
        this.time = time;
        this.accuracy = accuracy;
    }

    
    public String getTypedText() {
        return typedText;
    }
    
    public double getWpm(){
        return wpm;
    }
    
    public double getTime() {
        return time;
    }
    
    public double getAccuracy(){
     return accuracy;   
    }
    
       public void InsertData() { 
       
       } 
}