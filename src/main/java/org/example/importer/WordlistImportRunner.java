package org.example.importer;

import org.example.model.Word;
import org.example.repository.WordRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Profile("import")
public class WordlistImportRunner implements CommandLineRunner {

    private final WordRepository wordRepository;

    public WordlistImportRunner(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    private String normalizeLettersOnly(String input) {
        if (input == null) return "";
        String s = input.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return s;
    }

    private String canonical(String input) {
        String n = normalizeLettersOnly(input);
        char[] ca = n.toCharArray();
        java.util.Arrays.sort(ca);
        return new String(ca);
    }

    @Override
    public void run(String... args) throws Exception {
        String path = System.getProperty("wordlist.path");
        InputStream in;
        if (path == null || path.isBlank()) {
            System.out.println("No -Dwordlist.path provided, using bundled data/wordlist-small.txt");
            in = getClass().getClassLoader().getResourceAsStream("data/wordlist-small.txt");
            if (in == null) {
                System.err.println("Bundled wordlist resource data/wordlist-small.txt not found");
                return;
            }
        } else {
            System.out.println("Loading wordlist from file: " + path);
            in = new java.io.FileInputStream(path);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        AtomicInteger added = new AtomicInteger();
        AtomicInteger skipped = new AtomicInteger();

        reader.lines()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(word -> {
                    String key = canonical(word);
                    if (key.isEmpty()) {
                        skipped.incrementAndGet();
                        return;
                    }
                    // check if word already exists (by exact word)
                    boolean exists = false;
                    try {
                        exists = wordRepository.existsByNormalizedKeyAndWordIgnoreCase(key, word);
                    } catch (Exception e) {
                        // repository might not support the method exactly in all dialects; ignore and try save
                        exists = false;
                    }
                    if (!exists) {
                        Word w = new Word(word, key, word.length());
                        try {
                            wordRepository.save(w);
                            added.incrementAndGet();
                        } catch (Exception e) {
                            System.err.println("Failed to save word: " + word + " => " + e.getMessage());
                            skipped.incrementAndGet();
                        }
                    } else {
                        skipped.incrementAndGet();
                    }
                });

        System.out.println("Import finished. Added: " + added.get() + ", Skipped: " + skipped.get());
        // exit when import profile used so the app doesn't continue running
        System.exit(0);
    }
}

