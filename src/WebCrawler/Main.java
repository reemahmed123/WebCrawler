import invertedIndex.Index5;
import invertedIndex.Stemmer;

import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        // 1) Crawl
        Crawler crawler = new Crawler();
        crawler.crawl(new String[]{
                "https://en.wikipedia.org/wiki/List_of_pharaohs",
                "https://en.wikipedia.org/wiki/Pharaoh"
                //"https://en.wikipedia.org/wiki/Wiki",       // very short “stub” page
                //"https://en.wikipedia.org/wiki/Stub"        // the generic stub explanation page
        });

        /*Index5 idx = new Index5();
        idx.buildIndexFromCrawler(
                crawler.getDocumentContents(),
                crawler.getDocumentUrls()
        );
        String doc1Text = crawler.getDocumentContents().get("doc1");
        System.out.println("=== doc1 raw ===\n" + doc1Text);

        String[] tokens = doc1Text.toLowerCase().split("\\W+");
        Set<String> unique = new HashSet<>();
        for (String t : tokens) {
            if (t.length() > 1) {
                unique.add(t);
            }
        }
        System.out.println("Unique index-able tokens in doc1: " + unique);*/
        // 2) Build the index from the crawler’s maps
        Index5 idx = new Index5();
        idx.buildIndexFromCrawler(
                crawler.getDocumentContents(),
                crawler.getDocumentUrls()
        );

        // 3) Inspect dictionary and postings
        idx.printDictionary();


        //idx.indexOneLine("The writer tried to write many stories.",0);
        /*String w="drinker";
        w=idx.stemWord(w);
        System.out.println(w);*/
    }
}
