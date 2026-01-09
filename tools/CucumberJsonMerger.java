package tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CucumberJsonMerger {
    public static void main(String[] args) throws Exception {
        String a = "target/cucumber-1.json";
        String b = "target/cucumber-2.json";
        String out = "target/cucumber-merged.json";
        ObjectMapper m = new ObjectMapper();
        ArrayNode merged = m.createArrayNode();
        if (Files.exists(Paths.get(a))) {
            JsonNode n = m.readTree(new File(a));
            if (n.isArray()) merged.addAll((ArrayNode) n);
        }
        if (Files.exists(Paths.get(b))) {
            JsonNode n = m.readTree(new File(b));
            if (n.isArray()) merged.addAll((ArrayNode) n);
        }
        m.writerWithDefaultPrettyPrinter().writeValue(new File(out), merged);
        System.out.println("Wrote merged to " + out);
    }
}

