package com.nesine.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import com.nesine.model.MatchDetail;
import java.util.ArrayList;
import java.util.List;

@Service
public class NesineScraperImpl implements NesineScraper {
    // Market type IDs to human-readable names (expand as needed)
    private static final java.util.Map<Integer, String> MTID_NAMES = java.util.Map.ofEntries(
        java.util.Map.entry(1, "1X2"),
        java.util.Map.entry(2, "Double Chance"),
        java.util.Map.entry(3, "Handicap 1"),
        java.util.Map.entry(4, "Handicap 2"),
        java.util.Map.entry(5, "Over/Under 2.5"),
        java.util.Map.entry(6, "Correct Score"),
        java.util.Map.entry(7, "Half Time/Full Time"),
        java.util.Map.entry(8, "Both Teams to Score"),
        java.util.Map.entry(9, "First Half 1X2"),
        java.util.Map.entry(10, "First Half Over/Under 1.5"),
        java.util.Map.entry(11, "Over/Under 1.5"),
        java.util.Map.entry(12, "Over/Under 3.5"),
        java.util.Map.entry(13, "Draw No Bet"),
        java.util.Map.entry(14, "First Team to Score"),
        java.util.Map.entry(15, "Last Team to Score")
        // Add more mappings as needed
    );
    @Override
    public List<MatchDetail> getMatchesBetween(LocalDate start, LocalDate end) {
        System.out.println("[DEBUG] Querying matches from Nesine API for date range: " + start + " to " + end);
        List<MatchDetail> results = new ArrayList<>();
        int totalGroups = 0, totalEvents = 0, addedEvents = 0;
        try {
            // Java 11+ HttpClient
            java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
                .version(java.net.http.HttpClient.Version.HTTP_1_1)
                .build();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(new java.net.URI("https://bulten.nesine.com/api/bulten/getprebultenfull"))
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
                    .header("Referer", "https://www.nesine.com/")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "tr-TR,tr;q=0.9,en-US;q=0.8,en;q=0.7")
                    .header("Upgrade-Insecure-Requests", "1")
                    // .header("Cookie", "YOUR_SESSION_COOKIE_HERE") // <-- Uncomment and set if you want to use your browser session
                    .build();
            java.net.http.HttpResponse<byte[]> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofByteArray());
            System.out.println("[DEBUG] HTTP response status: " + response.statusCode());
            if (response.statusCode() == 200) {
                String encoding = response.headers().firstValue("Content-Encoding").orElse("").toLowerCase();
                String json;
                if (encoding.contains("br")) {
                    try (org.brotli.dec.BrotliInputStream is = new org.brotli.dec.BrotliInputStream(new java.io.ByteArrayInputStream(response.body()))) {
                        json = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                    }
                    System.out.println("[DEBUG] Brotli response decoded");
                } else if (encoding.contains("gzip")) {
                    try (java.io.InputStream is = new java.util.zip.GZIPInputStream(new java.io.ByteArrayInputStream(response.body()))) {
                        json = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                    }
                    System.out.println("[DEBUG] GZIP response decoded");
                } else {
                    json = new String(response.body(), java.nio.charset.StandardCharsets.UTF_8);
                    System.out.println("[DEBUG] Plain response decoded");
                }
                try {
                    System.out.println("[DEBUG] Attempting to parse JSON...");
                    System.out.println("[DEBUG] Raw JSON (first 1000 chars):\n" + json.substring(0, Math.min(json.length(), 1000)));
                    // Parse JSON
                    com.fasterxml.jackson.databind.JsonNode root = new com.fasterxml.jackson.databind.ObjectMapper().readTree(json);
                    System.out.print("[DEBUG] Top-level JSON keys: ");
                    root.fieldNames().forEachRemaining(k -> System.out.print(k + " "));
                    System.out.println();
                    // New structure: root -> "sg" -> "EA" (array of matches)
                    if (root.has("sg") && root.get("sg").has("EA")) {
                        com.fasterxml.jackson.databind.JsonNode matches = root.get("sg").get("EA");
                        System.out.println("[DEBUG] Found " + matches.size() + " matches in sg.EA");
                        int printedDates = 0;
                        int eventIdx = 0;
                        for (com.fasterxml.jackson.databind.JsonNode match : matches) {
                            // Print the first 20 match dates found (for debugging)
                            if (printedDates < 20) {
                                String debugDateStr = match.path("D").asText("") + " " + match.path("T").asText("");
                                System.out.println("[DEBUG] API match dateStr: " + debugDateStr);
                                printedDates++;
                            }
                            String home = match.path("HN").asText(""); // Home team
                            String away = match.path("AN").asText(""); // Away team
                            String league = ""; // League info not directly present; can be extracted if needed
                            String dateStr = match.path("D").asText(""); // dd.MM.yyyy
                            String timeStr = match.path("T").asText(""); // HH:mm
                            LocalDate matchDate = null;
                            try {
                                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy");
                                matchDate = java.time.LocalDate.parse(dateStr, formatter);
                            } catch (Exception e) { /* skip if date invalid */ }
                            if (eventIdx < 5) {
                                System.out.println("[DEBUG] Event " + eventIdx + " dateStr: " + dateStr + ", parsed: " + matchDate);
                            }
                            eventIdx++;
                            if (matchDate == null) {
                                System.out.println("[DEBUG] Skipping match due to invalid date: " + dateStr);
                                continue;
                            }
                            if (matchDate.isBefore(start) || matchDate.isAfter(end)) {
                                continue;
                            }
                            // Odds: collect all markets
                            java.util.Map<String, Double> odds = new java.util.HashMap<>(); // legacy 1X2 odds
                            java.util.Map<String, java.util.Map<String, Double>> allOdds = new java.util.HashMap<>();
                            if (match.has("MA")) {
                                for (com.fasterxml.jackson.databind.JsonNode ma : match.get("MA")) {
                                    // Market key: use MTID (market type id) or NO (market code/number)
                                    // Use market name if available, otherwise use MTID mapping or MTID_xxx
                                    int mtid = ma.path("MTID").asInt(-1);
                                    String marketKey = MTID_NAMES.getOrDefault(mtid, "MTID_" + mtid);
                                    if (marketKey.startsWith("MTID_")) {
                                        System.out.println("[DEBUG] Unknown MTID encountered: " + mtid + ". Please map this ID for better readability.");
                                    }
                                    java.util.Map<String, Double> marketOdds = new java.util.HashMap<>();
                                    if (ma.has("OCA")) {
                                        for (com.fasterxml.jackson.databind.JsonNode odd : ma.get("OCA")) {
                                            String outcomeKey = odd.has("N") ? odd.get("N").asText() : "?";
                                            double oddVal = odd.path("O").asDouble(-1);
                                            if (oddVal > 0) {
                                                marketOdds.put(outcomeKey, oddVal);
                                            }
                                        }
                                    }
                                    if (!marketOdds.isEmpty()) {
                                        allOdds.put(marketKey, marketOdds);
                                    }
                                    // Legacy: fill 1X2 odds map for MTID==1
                                    if (ma.has("MTID") && ma.get("MTID").asInt() == 1) {
                                        if (ma.has("OCA")) {
                                            for (com.fasterxml.jackson.databind.JsonNode odd : ma.get("OCA")) {
                                                int n = odd.path("N").asInt();
                                                double oddVal = odd.path("O").asDouble(-1);
                                                if (oddVal > 0) {
                                                    if (n == 1) odds.put("home", oddVal);
                                                    else if (n == 2) odds.put("draw", oddVal);
                                                    else if (n == 3) odds.put("away", oddVal);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            results.add(new com.nesine.model.MatchDetail(home, away, matchDate, league, odds, allOdds));
                            addedEvents++;
                        }
                    }
                } catch (Exception je) {
                    System.out.println("[DEBUG] Failed to parse JSON. Raw response (first 400 chars):\n" + json.substring(0, Math.min(json.length(), 400)));
                    je.printStackTrace();
                }
            } else {
                System.out.println("[DEBUG] Non-200 HTTP response, body length: " + response.body().length);
            }
        } catch (Exception e) {
            System.out.println("[DEBUG] Exception in NesineScraperImpl.getMatchesBetween: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("[DEBUG] Total events processed: " + totalEvents + ", events added to result: " + addedEvents);
        return results;
    }
}
