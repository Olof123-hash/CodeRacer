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
    private int mistakes;
    
    
    public PlayedGame(String typedText, double wpm, double time, double accuracy, int mistakes) {
        this.typedText = typedText;
        this.wpm = wpm;
        this.time = time;
        this.mistakes = mistakes;
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
    
    public int getMistakes() {
        return mistakes;
    }
    
    public void InsertData() { 
       
    } 
}