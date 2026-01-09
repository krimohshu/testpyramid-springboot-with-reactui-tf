package org.example.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/Anagram_Checker.feature",
    plugin = { "pretty", "html:target/cucumber-report-1.html", "json:target/cucumber-1.json" }
)
public class CucumberRunner1 {}

