package org.example.service;

import org.example.repository.WordRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnagramService {

    private final WordRepository wordRepository;

    public AnagramService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    private String normalizeLettersOnly(String input) {
        if (input == null) return "";
        String s = input.toLowerCase().replaceAll("[^a-z]", "");
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return s;
    }

    // public wrappers for debugging and external use
    public String getNormalizedLetters(String input) {
        return normalizeLettersOnly(input);
    }

    public String getCanonicalKey(String input) {
        return canonicalKey(input);
    }

    private String canonicalKey(String input) {
        String n = normalizeLettersOnly(input);
        char[] chars = n.toCharArray();
        java.util.Arrays.sort(chars);
        return new String(chars);
    }

    public boolean isExactAnagram(String input) {
        String key = canonicalKey(input);
        if (key.isEmpty()) return false;
        return wordRepository.existsByNormalizedKeyAndWordIgnoreCase(key, input);
    }

    public boolean areAnagrams(String a, String b) {
        if (a == null || b == null) return false;
        String na = normalizeLettersOnly(a);
        String nb = normalizeLettersOnly(b);
        if (na.isEmpty() || nb.isEmpty()) return false;
        return canonicalKey(na).equals(canonicalKey(nb));
    }

    public List<String> suggest(String input, int limit) {
        String key = canonicalKey(input);
        if (key.isEmpty()) return Collections.emptyList();
        List<String> words = wordRepository.findTopNByNormalizedKey(key, limit + 1); // fetch +1 to exclude exact same
        String normalizedInput = normalizeLettersOnly(input);
        List<String> filtered = words.stream()
                .map(String::trim)
                .filter(w -> !normalizeLettersOnly(w).equals(normalizedInput))
                .limit(limit)
                .collect(Collectors.toList());
        return filtered;
    }

    // New: suggest combinations of dictionary words that together form an anagram of the input
    public List<String> suggestCombinations(String input, int maxWords, int maxResults) {
        String normalized = normalizeLettersOnly(input);
        if (normalized.isEmpty()) return Collections.emptyList();
        // frequency map of input letters
        int[] target = freq(normalized);
        int totalLen = normalized.length();

        // fetch candidate words whose length <= totalLen
        List<org.example.model.Word> candidates = wordRepository.findByLengthLessThanEqual(totalLen);
        // map each candidate to its normalized key and letter freq
        List<Candidate> candList = new ArrayList<>();
        for (org.example.model.Word w : candidates) {
            String key = canonicalKey(w.getWord());
            if (key.isEmpty()) continue;
            if (key.length() > totalLen) continue;
            candList.add(new Candidate(w.getWord(), key, freq(key)));
        }
        // sort candidates by descending length to find larger words first (heuristic)
        candList.sort((a,b) -> Integer.compare(b.word.length(), a.word.length()));

        List<String> results = new ArrayList<>();
        LinkedList<String> path = new LinkedList<>();

        backtrackCombine(candList, 0, target, path, results, maxWords, maxResults);
        return results;
    }

    private void backtrackCombine(List<Candidate> candList, int start, int[] remaining, LinkedList<String> path, List<String> results, int maxWords, int maxResults) {
        if (results.size() >= maxResults) return;
        if (isZero(remaining)) {
            results.add(String.join(" ", path));
            return;
        }
        if (path.size() >= maxWords) return;

        for (int i = start; i < candList.size(); i++) {
            Candidate c = candList.get(i);
            if (!canUse(remaining, c.freq)) continue;
            // choose
            subtract(remaining, c.freq);
            path.addLast(c.word);
            backtrackCombine(candList, i + 1, remaining, path, results, maxWords, maxResults);
            path.removeLast();
            add(remaining, c.freq);
            if (results.size() >= maxResults) return;
        }
    }

    private static class Candidate {
        String word;
        String key;
        int[] freq;
        Candidate(String word, String key, int[] freq) { this.word = word; this.key = key; this.freq = freq; }
    }

    private int[] freq(String s) {
        int[] f = new int[26];
        for (char ch : s.toCharArray()) {
            if (ch >= 'a' && ch <= 'z') f[ch - 'a']++;
        }
        return f;
    }

    private boolean canUse(int[] rem, int[] add) {
        for (int i = 0; i < 26; i++) if (add[i] > rem[i]) return false;
        return true;
    }

    private void subtract(int[] rem, int[] add) {
        for (int i = 0; i < 26; i++) rem[i] -= add[i];
    }

    private void add(int[] rem, int[] add) {
        for (int i = 0; i < 26; i++) rem[i] += add[i];
    }

    private boolean isZero(int[] arr) {
        for (int v : arr) if (v != 0) return false;
        return true;
    }
}
