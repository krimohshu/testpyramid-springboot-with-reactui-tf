package org.example.config;

import org.example.model.Word;
import org.example.repository.WordRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class DataLoader implements CommandLineRunner {

    private final WordRepository wordRepository;

    public DataLoader(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // IMPORT DISABLED: automatic import disabled to avoid duplicate insert errors during development.
        // Use the scripts/import tools to load wordlists manually when needed.
        return;

        // Previous implementation (kept for reference):
        // long count = wordRepository.count();
        // if (count > 0) { return; }
        // ClassPathResource res = new ClassPathResource("data/wordlist-small.txt");
        // if (!res.exists()) return;
        // try (BufferedReader r = new BufferedReader(new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8))) {
        //     String line;
        //     while ((line = r.readLine()) != null) {
        //         String w = line.trim();
        //         if (w.isEmpty()) continue;
        //         String key = normalize(w);
        //         if (key.isEmpty()) continue;
        //         try {
        //             boolean exists = wordRepository.existsByNormalizedKeyAndWordIgnoreCase(key, w);
        //             if (!exists) {
        //                 Word word = new Word(w, key, w.length());
        //                 wordRepository.save(word);
        //             }
        //         } catch (Exception ex) {
        //             // ignore and continue
        //         }
        //     }
        // }
    }

    private static String normalize(String input) {
        if (input == null) return "";
        String s = input.toLowerCase().replaceAll("[^a-z]", "");
        char[] chars = s.toCharArray();
        java.util.Arrays.sort(chars);
        return new String(chars);
    }
}
