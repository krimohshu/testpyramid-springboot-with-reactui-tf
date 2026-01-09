package org.example.controller;

import org.example.service.AnagramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AnalyzeController {

    private final AnagramService anagramService;
    private final JdbcTemplate jdbcTemplate;

    public AnalyzeController(AnagramService anagramService, JdbcTemplate jdbcTemplate) {
        this.anagramService = anagramService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestBody Map<String, Object> body) {
        String text = body.getOrDefault("text", "").toString();
        int limit = 10;
        if (body.containsKey("maxSuggestions")) {
            try { limit = Integer.parseInt(body.get("maxSuggestions").toString()); } catch (Exception ignored) {}
        }
        boolean isAnagram = anagramService.isExactAnagram(text);
        List<String> suggestions = anagramService.suggest(text, limit);

        Map<String, Object> resp = new HashMap<>();
        resp.put("text", text);
        resp.put("isAnagramOfDictionaryWord", isAnagram);
        resp.put("suggestions", suggestions);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<?> suggestions(@RequestParam(name = "text") String text,
                                         @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        List<String> suggestions = anagramService.suggest(text, limit);
        Map<String, Object> resp = new HashMap<>();
        resp.put("text", text);
        resp.put("suggestions", suggestions);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/combinations")
    public ResponseEntity<?> combinations(@RequestParam(name = "text") String text,
                                          @RequestParam(name = "maxWords", required = false, defaultValue = "2") int maxWords,
                                          @RequestParam(name = "maxResults", required = false, defaultValue = "20") int maxResults) {
        List<String> combos = anagramService.suggestCombinations(text, maxWords, maxResults);
        return ResponseEntity.ok(Map.of("text", text, "combinations", combos));
    }

    @PostMapping("/areAnagrams")
    public ResponseEntity<?> areAnagrams(@RequestBody Map<String, String> body) {
        String a = body.getOrDefault("input1", "");
        String b = body.getOrDefault("input2", "");
        boolean result = anagramService.areAnagrams(a, b);
        Map<String, Object> resp = new HashMap<>();
        resp.put("input1", a);
        resp.put("input2", b);
        resp.put("areAnagrams", result);
        return ResponseEntity.ok(resp);
    }

    // Admin endpoint: sample words for quick verification (no auth in MVP)
    @GetMapping("/admin/words")
    public ResponseEntity<?> adminWords(@RequestParam(name = "limit", required = false, defaultValue = "50") int limit) {
        String sql = "SELECT id, word, normalized_key FROM words ORDER BY id LIMIT ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, limit);
        return ResponseEntity.ok(Map.of("count", rows.size(), "rows", rows));
    }
}
