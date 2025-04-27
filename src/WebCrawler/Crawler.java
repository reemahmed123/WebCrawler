import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class Crawler {
    // Maximum number of pages to crawl
    private static final int MAX_PAGES = 10;

    // Store visited URLs to avoid duplicates
    private final Set<String> visitedUrls;

    // Queue for URLs to be crawled
    private final LinkedList<String> urlQueue;

    // Store document content - map of document ID to document text
    private final Map<String, String> documentContents;

    // Store document URLs - map of document ID to URL
    private final Map<String, String> documentUrls;

    // Constructor
    public Crawler() {
        visitedUrls = new HashSet<>();
        urlQueue = new LinkedList<>();
        documentContents = new HashMap<>();
        documentUrls = new HashMap<>();
    }

    public void crawl(String[] seedUrls) {
        // Add seed URLs to the queue
        Collections.addAll(urlQueue, seedUrls);

        int docId = 0;

        // Process URLs until queue is empty, or we've reached MAX_PAGES
        while (!urlQueue.isEmpty() && docId < MAX_PAGES) {
            String currentUrl = urlQueue.poll();
            currentUrl = normalizeUrl(currentUrl);

            // Skip if already visited
            if (visitedUrls.contains(currentUrl)) {
                continue;
            }

            // Mark as visited
            visitedUrls.add(currentUrl);

            try {
                // Add a small delay to be respectful to Wikipedia servers
                Thread.sleep(100);

                // Connect to the URL and get the document
                Document doc = Jsoup.connect(currentUrl)
                        .userAgent("Mozilla/5.0 (compatible; WebCrawler/1.0; +https://example.com)")
                        .timeout(5000)
                        .get();

                System.out.println("Crawling: " + currentUrl);

                // Extract text from the document
                String text = extractText(doc);

                // Store the document content with a unique ID
                String documentId = "doc" + docId;
                documentContents.put(documentId, text);
                documentUrls.put(documentId, currentUrl);
                docId++;

                // Find all links on the page and add to queue if within Wikipedia
                addLinksToQueue(doc);

                System.out.println("Crawled unique pages so far: " + documentContents.size());

            } catch (IOException e) {
                System.err.println("Error crawling " + currentUrl + ": " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Crawling completed. " + docId + " pages crawled.");
    }


    private String extractText(Document doc) {
        // Get clean title (remove " - Wikipedia" suffix)
        String title = doc.title().replaceAll(" - Wikipedia$", "").trim();

        // Remove Wikipedia-specific elements that don't contain main article content
        doc.select("script, style, .navbox, .infobox, .toc, .mw-editsection, " +
                ".ambox, .hatnote, .reference, .mw-cite-backlink, .thumb, " +
                ".gallery, .metadata, .portal, .nomobile, .noprint, " +
                ".sidebar, .coordinates, .mw-indicators, .mw-redirect").remove();

        // Get the main content area (Wikipedia-specific selector)
        Element content = doc.selectFirst("#mw-content-text .mw-parser-output");

        StringBuilder sb = new StringBuilder();
        sb.append("TITLE: ").append(title).append("\n\n");

        if (content != null) {
            // Select all relevant content-bearing elements
            Elements textElements = content.select("p, h1, h2, h3, h4, h5, h6, ul, ol, li, dl, dt, dd");

            for (Element el : textElements) {
                String text = el.text()
                        .replaceAll("\\[\\d+\\]", "")  // Remove citation numbers [1]
                        .replaceAll("\\s+", " ")       // Normalize whitespace
                        .trim();

                if (!text.isEmpty()) {
                    switch (el.tagName()) {
                        case "h1":
                        case "h2":
                            sb.append("\n\n").append(text).append("\n");
                            break;
                        case "h3":
                        case "h4":
                        case "h5":
                        case "h6":
                            sb.append("\n").append(text).append("\n");
                            break;
                        case "li":
                            sb.append("\n• ").append(text);
                            break;
                        case "dt":
                            sb.append("\n").append(text).append(":");
                            break;
                        case "dd":
                            sb.append(" ").append(text);
                            break;
                        default:
                            sb.append("\n").append(text);
                    }
                }
            }
        } else {
            sb.append(doc.body().text());
        }

        // Post-processing
        return sb.toString()
                .replaceAll("(?m)^\\s+", "")   // Remove leading whitespace per line
                .replaceAll("\\n{3,}", "\n\n") // Normalize multiple newlines
                .trim();
    }

    private void addLinksToQueue(Document doc) {
        // Get all links
        Elements links = doc.select("a[href]");
        int validLinksFound = 0;

        System.out.println("Found " + links.size() + " total links on page");

        for (Element link : links) {
            String url = link.absUrl("href");
            url = normalizeUrl(url);

            // Only add Wikipedia article links (avoid special pages, etc.)
            if (isValidWikipediaArticle(url) && !visitedUrls.contains(url)) {
                urlQueue.add(url);
                validLinksFound++;

                // Print the first 5 valid links found (for debugging)
                if (validLinksFound <= 5) {
                    System.out.println("  Added to queue: " + url);
                }
            }
        }

        System.out.println("Added " + validLinksFound + " new links to the queue");
    }

    private boolean isValidWikipediaArticle(String url) {
        // Check if it's a Wikipedia article URL
        if (!url.startsWith("https://en.wikipedia.org/wiki/")) {
            return false;
        }

        // Exclude URLs with fragments/anchors (#)
        if (url.contains("#")) {
            return false;
        }

        // Exclude non-article namespaces
        String[] nonArticleNamespaces = {
                "Wikipedia:", "Special:", "File:", "Help:",
                "Talk:", "Category:", "Template:", "Portal:",
                "User:", "MediaWiki:"
        };

        for (String namespace : nonArticleNamespaces) {
            if (url.contains("/wiki/" + namespace)) {
                return false;
            }
        }

        // Exclude action pages and main page
        return !url.contains("action=") && !url.contains("&oldid=") &&
                !url.contains("index.php") && !url.contains("Main_Page");
    }

    private String normalizeUrl(String url) {
        // Remove fragment
        url = url.replaceAll("#.*$", "");

        // Remove trailing slash if present
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        return url;
    }

    // Getter for retrieved documents
    public Map<String, String> getDocumentContents() {
        return documentContents;
    }

    // Getter for document URLs
    public Map<String, String> getDocumentUrls() {
        return documentUrls;
    }

}
