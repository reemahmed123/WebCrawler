/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

/**
 *
 * @author ehab
 */
 
public class Posting {

    public Posting next = null;
    int docId;
    double dtf = 1.0;// Document term frequency

    Posting(int id, double t) {
        docId = id;
        dtf=t;
    }
    
    Posting(int id) {
        docId = id;
    }
}