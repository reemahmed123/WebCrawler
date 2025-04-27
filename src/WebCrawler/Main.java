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

        Index5 idx = new Index5();
        idx.buildIndexFromCrawler(
                crawler.getDocumentContents(),
                crawler.getDocumentUrls()
        );
        idx.printDictionary();
        //Xidx.printDocumentVectors(); // Print all document vectors
    }
}
