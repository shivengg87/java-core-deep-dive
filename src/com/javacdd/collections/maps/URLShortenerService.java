package com.javacdd.collections.maps;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Real-world URL Shortener Service implementation using CustomHashMap.
 *
 * Similar to bit.ly, tinyurl.com - demonstrates:
 * - Bidirectional mapping (short URL <-> long URL)
 * - Hash collision handling in production scenarios
 * - URL encoding/decoding
 * - Performance at scale
 *
 * System Design Interview Favorite!
 *
 * Example Usage:
 *   URLShortenerService service = new URLShortenerService();
 *   String shortUrl = service.shortenURL("https://www.example.com/very/long/url");
 *   String originalUrl = service.expandURL(shortUrl);
 */
public class URLShortenerService {
    // Maps short code to original URL
    private final CustomHashMap<String, URLMapping> shortToLongMap;

    // Maps original URL to short code (prevents duplicates)
    private final CustomHashMap<String, String> longToShortMap;

    // Base URL for short links
    private static final String BASE_URL = "https://myshorturl.ly/";

    // Characters used for generating short codes
    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    // Length of generated short codes
    private static final int SHORT_CODE_LENGTH = 7;

    private final Random random;

    /**
     * Represents a URL mapping with metadata
     */
    private static class URLMapping {
        String originalUrl;
        String shortCode;
        long createdAt;
        int accessCount;

        URLMapping(String originalUrl, String shortCode) {
            this.originalUrl = originalUrl;
            this.shortCode = shortCode;
            this.createdAt = System.currentTimeMillis();
            this.accessCount = 0;
        }

        void incrementAccess() {
            accessCount++;
        }
    }

    public URLShortenerService() {
        this.shortToLongMap = new CustomHashMap<>();
        this.longToShortMap = new CustomHashMap<>();
        this.random = new Random();
    }
    /**
     * Shortens a long URL to a compact form.
     * If URL already shortened, returns existing short URL.
     *
     * Time Complexity: O(1) average case
     *
     * @param longUrl the original URL to shorten
     * @return shortened URL (e.g., "https://short.ly/aB3xY9z")
     */
    public String shortenURL(String longUrl) {
        if (longUrl == null || longUrl.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        // Check if URL already shortened
        String existingShortCode = longToShortMap.get(longUrl);
        if (existingShortCode != null) {
            return BASE_URL + existingShortCode;
        }

        // Generate unique short code
        String shortCode = generateShortCode(longUrl);

        // Handle collision (very rare but possible)
        int attempts = 0;
        while (shortToLongMap.containsKey(shortCode) && attempts < 10) {
            shortCode = generateRandomCode();
            attempts++;
        }

        if (attempts >= 10) {
            throw new RuntimeException("Failed to generate unique short code after 10 attempts");
        }

        // Store bidirectional mapping
        URLMapping mapping = new URLMapping(longUrl, shortCode);
        shortToLongMap.put(shortCode, mapping);
        longToShortMap.put(longUrl, shortCode);

        return BASE_URL + shortCode;
    }
    /**
     * Expands a short URL back to its original form.
     *
     * Time Complexity: O(1) average case
     *
     * @param shortUrl the shortened URL
     * @return original long URL, or null if not found
     */
    public String expandURL(String shortUrl) {
        if (shortUrl == null || !shortUrl.startsWith(BASE_URL)) {
            return null;
        }

        String shortCode = shortUrl.substring(BASE_URL.length());
        URLMapping mapping = shortToLongMap.get(shortCode);

        if (mapping != null) {
            mapping.incrementAccess();
            return mapping.originalUrl;
        }

        return null;
    }

    /**
     * Gets statistics for a shortened URL
     */
    public URLStats getStatistics(String shortUrl) {
        if (shortUrl == null || !shortUrl.startsWith(BASE_URL)) {
            return null;
        }

        String shortCode = shortUrl.substring(BASE_URL.length());
        URLMapping mapping = shortToLongMap.get(shortCode);

        if (mapping == null) {
            return null;
        }

        return new URLStats(
                mapping.originalUrl,
                shortUrl,
                mapping.createdAt,
                mapping.accessCount
        );
    }
    /**
     * Deletes a URL mapping
     */
    public boolean deleteURL(String shortUrl) {
        if (shortUrl == null || !shortUrl.startsWith(BASE_URL)) {
            return false;
        }

        String shortCode = shortUrl.substring(BASE_URL.length());
        URLMapping mapping = shortToLongMap.remove(shortCode);

        if (mapping != null) {
            longToShortMap.remove(mapping.originalUrl);
            return true;
        }

        return false;
    }

    /**
     * Returns total number of shortened URLs
     */
    public int getTotalURLs() {
        return shortToLongMap.size();
    }

    /**
     * Generates a short code using hash-based approach
     */
    private String generateShortCode(String longUrl) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(longUrl.getBytes());

            // Convert hash to base62
            StringBuilder shortCode = new StringBuilder();
            for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
                int index = Math.abs(hash[i % hash.length]) % CHARSET.length();
                shortCode.append(CHARSET.charAt(index));
            }

            return shortCode.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback to random generation
            return generateRandomCode();
        }
    }

    /**
     * Generates a random short code (used for collision resolution)
     */
    private String generateRandomCode() {
        StringBuilder shortCode = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int index = random.nextInt(CHARSET.length());
            shortCode.append(CHARSET.charAt(index));
        }
        return shortCode.toString();
    }

    /**
     * Statistics for a shortened URL
     */
    public static class URLStats {
        public final String originalUrl;
        public final String shortUrl;
        public final long createdAt;
        public final int accessCount;

        URLStats(String originalUrl, String shortUrl, long createdAt, int accessCount) {
            this.originalUrl = originalUrl;
            this.shortUrl = shortUrl;
            this.createdAt = createdAt;
            this.accessCount = accessCount;
        }

        @Override
        public String toString() {
            long ageMinutes = (System.currentTimeMillis() - createdAt) / 60000;
            return String.format(
                    "Original: %s%nShort: %s%nAge: %d minutes%nClicks: %d",
                    originalUrl, shortUrl, ageMinutes, accessCount
            );
        }
    }
    /**
     * Demonstration of URL Shortener Service
     */
    public static void main(String[] args) {
        System.out.println("=== URL Shortener Service Demo ===\n");

        URLShortenerService service = new URLShortenerService();

        // Example 1: Basic shortening
        System.out.println("1. Basic URL Shortening:");
        String longUrl1 = "https://www.example.com/products/category/electronics/laptops/gaming?sort=price&page=5";
        String shortUrl1 = service.shortenURL(longUrl1);
        System.out.println("Original: " + longUrl1);
        System.out.println("Shortened: " + shortUrl1);
        System.out.println();

        // Example 2: Expanding short URL
        System.out.println("2. Expanding Short URL:");
        String expanded = service.expandURL(shortUrl1);
        System.out.println("Short URL: " + shortUrl1);
        System.out.println("Expanded: " + expanded);
        System.out.println("Match: " + expanded.equals(longUrl1));
        System.out.println();

        // Example 3: Duplicate URL handling
        System.out.println("3. Duplicate URL Detection:");
        String shortUrl1Again = service.shortenURL(longUrl1);
        System.out.println("Shortening same URL again: " + shortUrl1Again);
        System.out.println("Returns same short URL: " + shortUrl1.equals(shortUrl1Again));
        System.out.println();

        // Example 4: Multiple URLs
        System.out.println("4. Shortening Multiple URLs:");
        String[] urls = {
                "https://github.com/spring-projects/spring-boot",
                "https://docs.oracle.com/javase/tutorial/java/nutsandbolts/index.html",
                "https://stackoverflow.com/questions/tagged/java",
                "https://www.linkedin.com/jobs/search/?keywords=java%20developer"
        };

        for (String url : urls) {
            String shortUrl = service.shortenURL(url);
            System.out.println(shortUrl + " -> " + url.substring(0, Math.min(50, url.length())) + "...");
        }
        System.out.println();

        // Example 5: Access statistics
        System.out.println("5. URL Access Statistics:");
        for (int i = 0; i < 5; i++) {
            service.expandURL(shortUrl1); // Simulate clicks
        }
        URLStats stats = service.getStatistics(shortUrl1);
        System.out.println(stats);
        System.out.println();

        // Example 6: Performance test
        System.out.println("6. Performance Test (1000 URLs):");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            String url = "https://example.com/page/" + i;
            service.shortenURL(url);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
        System.out.println("Total URLs stored: " + service.getTotalURLs());
        System.out.println("Average time per URL: " + (endTime - startTime) / 1000.0 + " ms");
        System.out.println();

        // Example 7: Hash collision demonstration
        System.out.println("7. System Design Insights:");
        System.out.println("✓ Bidirectional mapping prevents duplicate URLs");
        System.out.println("✓ Hash-based generation reduces collisions");
        System.out.println("✓ Random fallback handles rare collision cases");
        System.out.println("✓ O(1) lookup time for URL expansion");
        System.out.println("✓ Tracks usage statistics per URL");
        System.out.println();

        // Example 8: CustomHashMap statistics
        System.out.println("8. Underlying HashMap Statistics:");
        System.out.println("This demonstrates CustomHashMap's performance:");
        System.out.println("- Load factor optimization");
        System.out.println("- Collision handling via chaining");
        System.out.println("- Automatic resizing when needed");
        System.out.println();

        // Example 9: Real-world scaling considerations
        System.out.println("9. Production Considerations:");
        System.out.println("For real URL shortener at scale, consider:");
        System.out.println("• Database persistence (Redis, PostgreSQL)");
        System.out.println("• Distributed ID generation (Snowflake algorithm)");
        System.out.println("• Rate limiting per user/IP");
        System.out.println("• Custom domain support");
        System.out.println("• Link expiration & analytics");
        System.out.println("• CDN for global performance");
        System.out.println();

        System.out.println("=== Demo Complete ===");
    }
}
