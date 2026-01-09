package org.example.controller;

import org.example.service.AnagramService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private final AnagramService anagramService;

    public DebugController(AnagramService anagramService) {
        this.anagramService = anagramService;
    }

    @GetMapping("/normalize")
    public Map<String, String> normalize(@RequestParam(name = "text", required = false, defaultValue = "") String text) {
        String norm = anagramService.getNormalizedLetters(text);
        return Map.of("text", text, "normalized", norm);
    }

    @GetMapping("/key")
    public Map<String, String> key(@RequestParam(name = "text", required = false, defaultValue = "") String text) {
        String key = anagramService.getCanonicalKey(text);
        return Map.of("text", text, "key", key);
    }
}

