package logic;
import model.Snippet;
import database.TextDao;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author maril
 */
public class RaceEngine {
    public static void main(String[]args){
    TextDao dao = new TextDao();
    Snippet snip =dao.getRandomSnippet();
    }
}
