package org.example.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/Anagram_Checker_2.feature",
    plugin = { "pretty", "html:target/cucumber-report-2.html", "json:target/cucumber-2.json" }
)
public class CucumberRunner2 {}

