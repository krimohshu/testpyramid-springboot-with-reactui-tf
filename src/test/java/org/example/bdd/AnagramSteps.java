package org.example.bdd;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AnagramSteps {

    private String input1;
    private String input2;
    private Boolean result;

    @Given("the input strings {string} and {string}")
    public void givenInputs(String a, String b) {
        this.input1 = a;
        this.input2 = b;
    }

    @When("I check if they are anagrams")
    public void checkAnagrams() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String body = String.format("{\"input1\":%s,\"input2\":%s}", quote(input1), quote(input2));
        HttpRequest req = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:9090/api/areAnagrams"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) throw new RuntimeException("API returned " + resp.statusCode() + ": " + resp.body());
        String b = resp.body();
        this.result = b.contains("\"areAnagrams\":true") || b.contains("true");
    }

    @Then("the result should be {string}")
    public void thenResult(String expected) {
        boolean exp = Boolean.parseBoolean(expected);
        assertNotNull(result, "No result from API");
        assertEquals(exp, result.booleanValue());
    }

    private String quote(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
