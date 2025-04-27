/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

/**
 *
 * @author ehab
 */
public class DictEntry {

    public int doc_freq = 0; // number of documents that contain the term
    public int term_freq = 0; //number of times the term is mentioned in the collection
//=====================================================================
    //public HashSet<Integer> postingList;
    Posting pList = null;
    Posting last = null;
//------------------------------------------------

    boolean postingListContains(int i) {
        boolean found = false;
        Posting p = pList;
        while (p != null) {
            if (p.docId == i) {
                return true;
            }
            p = p.next;
        }
        return found;
    }
//------------------------------------------------

    int getPosting(int i) {
        int found = 0;
        Posting p = pList;
        while (p != null) {
            if (p.docId >= i) {
                if (p.docId == i) {
                    return p.dtf;  //return number of times term appear in document
                } else {
                    return 0;
                }
            }
            p = p.next;
        }
        return found;
    }
//------------------------------------------------
//The main difference between insert(int docId) and addPosting(int i)
// lies in how they handle duplicate document IDs and maintain term/document frequency.

    void addPosting(int i) {
        // pList = new Posting(i);
        if (pList == null) {
            pList = new Posting(i);
            last = pList;
        } else {
            last.next = new Posting(i);
            last = last.next;
        }
    }
// implement insert (int docId) method   //done
void insert(int docId) {
    if (pList == null) {
        // First posting in the list
        pList = new Posting(docId);
        last = pList;
        // First occurrence of this term in any document
        doc_freq = 1;
        term_freq = 1;
    } else {
        // Check if the docId already exists
        Posting current = pList;

        while (current != null) {
            if (current.docId == docId) {
                // If the document already exists, increase term frequency (dtf)
                current.dtf++;
                term_freq++;  // Increase term frequency across all documents
                return; // Stop, no need to add duplicate entry
            }
            current = current.next;
        }

        // If docId was not found, add a new posting at the end
        last.next = new Posting(docId);
        last = last.next;
        // Increase document frequency (new document contains this term)
        doc_freq++;
        term_freq++;  // Increase total term frequency
    }
}

    DictEntry() {
        //  postingList = new HashSet<Integer>();
    }

    DictEntry(int df, int tf) {
        doc_freq = df; 
        term_freq = tf;
    }

}
