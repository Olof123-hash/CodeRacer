/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.util.ArrayList;
/**
 *
 * @author maril
 */
public class Snippet {
    private ArrayList<String> body;
    public Snippet(ArrayList<String> newBody){
    this.body= newBody; 
}
    public ArrayList<String> getBody(){
    return this.body;
}
}
