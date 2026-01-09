package org.example.bdd;

import org.example.config.AbstractIntegrationTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AnagramBDDTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    static Stream<org.junit.jupiter.params.provider.Arguments> data() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of("listen", "silent", true),
                org.junit.jupiter.params.provider.Arguments.of("hello", "world", false),
                org.junit.jupiter.params.provider.Arguments.of("conversation", "voices rant on", true),
                org.junit.jupiter.params.provider.Arguments.of("school master", "the classroom", true),
                org.junit.jupiter.params.provider.Arguments.of("a gentleman", "elegant man", true),
                org.junit.jupiter.params.provider.Arguments.of("eleven plus two", "twelve plus one", true),
                org.junit.jupiter.params.provider.Arguments.of("apple", "paple", true),
                org.junit.jupiter.params.provider.Arguments.of("rat", "car", false)
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void scenario(String input1, String input2, boolean expected) {
        String url = "http://localhost:" + port + "/api/areAnagrams";
        Map resp = restTemplate.postForObject(url, Map.of("input1", input1, "input2", input2), Map.class);
        Object val = resp.get("areAnagrams");
        boolean actual = Boolean.valueOf(String.valueOf(val));
        assertEquals(expected, actual, "Are anagrams check failed for: " + input1 + " / " + input2);
    }
}
