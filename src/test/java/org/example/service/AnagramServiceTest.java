package org.example.service;

import org.example.repository.WordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AnagramServiceTest {

    private WordRepository wordRepository;
    private AnagramService anagramService;

    @BeforeEach
    void setup() {
        wordRepository = Mockito.mock(WordRepository.class);
        when(wordRepository.existsByNormalizedKeyAndWordIgnoreCase(anyString(), anyString())).thenReturn(true);
        when(wordRepository.findTopNByNormalizedKey(anyString(), Mockito.anyInt())).thenReturn(List.of("silent", "enlist", "inlets"));
        anagramService = new AnagramService(wordRepository);
    }

    @Test
    void testAreAnagramsTrue() {
        assertTrue(anagramService.areAnagrams("listen", "silent"));
    }

    @Test
    void testAreAnagramsFalse() {
        assertFalse(anagramService.areAnagrams("hello", "world"));
    }

    @Test
    void testSuggest() {
        List<String> suggestions = anagramService.suggest("listen", 3);
        assertNotNull(suggestions);
        assertTrue(suggestions.size() <= 3);
    }

    @Test
    void testIsExactAnagram() {
        assertTrue(anagramService.isExactAnagram("listen"));
    }
}

