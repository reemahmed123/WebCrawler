/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import java.io.*;
import java.util.*;

/**
 *
 * @author ehab
 */
public class Index5 {

    private final Stemmer stemmer = new Stemmer();
    //--------------------------------------------
    int N = 0;
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.

    public HashMap<String, DictEntry> index; // THe inverted index
    //--------------------------------------------

    public Index5() {
        sources = new HashMap<Integer, SourceRecord>();
        index = new HashMap<String, DictEntry>();
    }

    public void setN(int n) {
        N = n;
    }


    //---------------------------------------------
    public void printPostingList(Posting p) {
        // Iterator<Integer> it2 = hset.iterator();
        System.out.print("[");
        while (p != null) {
            /// -4- **** complete here ****
            // fix get rid of the last comma   //done
            if (p.next == null) {  // if document id is the last item in posting list
                System.out.print("" + p.docId);   //no comma printed
                break;
            }
            System.out.print("" + p.docId + ",");  //otherwise print comma
            p = p.next;
        }
        System.out.println("]");
    }

    //---------------------------------------------
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {// Iterate through each term in the dictionary
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry dd = (DictEntry) pair.getValue(); // Extract the dictionary entry

            // Print the term and its document frequency
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "]       =--> ");
            printPostingList(dd.pList);// Print the posting list associated with this term
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }

    //-----------------------------------------------
    public void sortIndex() {
        // Extract dictionary keys (words)
        String[] words = index.keySet().toArray(new String[0]);
        // Sort using your bubble sort function
        words = sort(words);
        // Create a new sorted dictionary
        HashMap<String, DictEntry> sortedIndex = new LinkedHashMap<>();
        // Insert words in sorted order
        for (String word : words) {
            sortedIndex.put(word, index.get(word));
        }
        // Replace the old index with the sorted one
        index = sortedIndex;
    }

    //----------------------------------------------------------------------------
    //Processes a single line of text, tokenizes it, and updates the inverted index.
    // This method filters stop words, applies stemming (not now) , and updates term frequencies.
    public int indexOneLine(String ln, int fid) {
        int flen = 0;//number of words in this line

        String[] words = ln.split("\\W+"); // Split the line into words based on non-word characters
        //   String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
        flen += words.length;
        for (String word : words) {
            word = word.toLowerCase();
            if (stopWord(word)) {// Skip stop words
                continue;
            }
            word = stemWord(word);  // Apply stemming to the word (not applied now )
            //System.out.println("Original: " + word + " → Stemmed: " + stemWord(word));

            // check to see if the word is not in the dictionary
            // if not add it
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }
            // add document id to the posting list
            // Check if the document ID is already in the posting list
            if (!index.get(word).postingListContains(fid)) {
                index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term 
                if (index.get(word).pList == null) {// If the posting list is empty, create a new one
                    index.get(word).pList = new Posting(fid);
                    index.get(word).last = index.get(word).pList;

                } else {// Append the new posting to the list
                    index.get(word).last.next = new Posting(fid);
                    index.get(word).last = index.get(word).last.next;
                }
            } else {
                // If the document already contains the word, increase its term frequency in this document
                index.get(word).last.dtf += 1;
            }
            //set the term_fteq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {

                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }

        }
        return flen;// Return the number of words processed
    }

//----------------------------------------------------------------------------

    //Determines whether a given word is a stop word.
    // Stop words are common words that are often excluded from indexing to improve efficiency.
    boolean stopWord(String word) {
        if (word.equals("the") || word.equals("to") || word.equals("be") || word.equals("for") || word.equals("from") || word.equals("in")
                || word.equals("a") || word.equals("into") || word.equals("by") || word.equals("or") || word.equals("and") || word.equals("that")) {
            return true;
        }
        if (word.length() < 2) {  //word of one char considered a stop word
            return true;
        }
        return false;

    }
//----------------------------------------------------------------------------  

    //Stemming is the process of reducing a word to its root form.
    //  This method currently returns the word unchanged.
    public String stemWord(String word) { //skip for now
        // Then in your stemWord:
        stemmer.addString(word);
        stemmer.stem();
        String root = stemmer.toString();
// Clear it for the next token:
        stemmer.setI(0);   // reset result length
        stemmer.setI_end(0);
        return root;
    }
    //---------------------------------
    String[] sort(String[] words) {  //bubble sort used to sort array of words
        //i use this function (implemented in code by doctor) in sort index function (which i implement it)
        boolean sorted = false;
        String sTmp;
        //-------------------------------------------------------
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < words.length - 1; i++) {
                int compare = words[i].compareTo(words[i + 1]);
                if (compare > 0) {
                    sTmp = words[i];
                    words[i] = words[i + 1];
                    words[i + 1] = sTmp;
                    sorted = false;
                }
            }
        }
        return words;
    }
//----------------------------------------------------

    /**
     * Build index directly from the Crawler’s output.
     * //param docs  map of documentId ("doc0", "doc1", …) → full extracted text
     * //param urls  map of documentId → original URL
     */
    public void buildIndexFromCrawler(Map<String, String> docs,
                                      Map<String, String> urls) {
        // Total documents
         N = docs.size();

        for (Map.Entry<String, String> entry : docs.entrySet()) {
            String docIdStr = entry.getKey();      // e.g. "doc0"
            String content = entry.getValue();    // the extracted text
            String url = urls.get(docIdStr);  // the original URL

            // Derive an integer fid (matches your SourceRecord.fid)
            // Assumes "doc0", "doc1", …; adjust if your IDs differ.
            int fid = Integer.parseInt(docIdStr.replace("doc", ""));

            // Extract a title from the first line of content (optional)
            String title;
            int newline = content.indexOf('\n');
            if (newline > 0) {
                title = content.substring(0, newline).replace("TITLE: ", "").trim();
            } else {
                title = url;  // fallback
            }

            // Create & store the SourceRecord
            SourceRecord rec = new SourceRecord(fid, url, title, content);
            sources.put(fid, rec);

            // Index that record’s text
            indexOneLine(content, fid);

            // Also set the document length
            rec.length = content.split("\\W+").length;
        }

        // Finally, sort your index so printDictionary() and queries work
        sortIndex();
        applyTfWeighting();
    }
    public void applyTfWeighting() {
        for (DictEntry entry : index.values()) {
            Posting p = entry.pList;
            while (p != null) {
                if (p.dtf > 0) {
                    p.dtf = 1 + Math.log10(p.dtf);
                }
                p = p.next;
            }
        }
        for (Map.Entry<String, DictEntry> entry : index.entrySet()) {
            Posting p = entry.getValue().pList;
            while (p != null) {
                System.out.println("Term: " + entry.getKey() + " | DocID: " + p.docId + " | Weighted TF: " + p.dtf);
                p = p.next;
            }
        }
    }
}