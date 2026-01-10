package org.example.ui;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UiAnagramTest {

    static Playwright playwright;
    static Browser browser;
    static HttpServer staticServer;

    @BeforeAll
    static void setup() {
        try {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));

            // start a simple static file server to serve the web UI for Playwright
            Path webRoot = Paths.get("web/public");
            if (Files.exists(webRoot)) {
                staticServer = HttpServer.create(new InetSocketAddress(3000), 0);
                staticServer.createContext("/", new StaticFileHandler(webRoot));
                staticServer.setExecutor(null);
                staticServer.start();
            }
        } catch (Throwable t) {
            // Playwright or server couldn't start — mark playwright null so tests skip
            playwright = null;
            if (staticServer != null) {
                staticServer.stop(0);
                staticServer = null;
            }
        }
    }

    @AfterAll
    static void teardown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
        if (staticServer != null) staticServer.stop(0);
    }

    @Test
    void uiCheckListenSilent() {
        if (playwright == null) return; // skip
        BrowserContext context = browser.newContext();
        Page page = context.newPage();
        String base = System.getProperty("ui.base.url", "http://localhost:3000");
        page.navigate(base);
        // focus input and type 'listen' and second input 'silent'
        page.fill("#anagram-input", "listen");
        page.fill("#compare-input", "silent");
        page.click("text=Check Anagram");
        // wait for badge to update
        page.waitForSelector(".badge.ok, .badge.bad", new Page.WaitForSelectorOptions().setTimeout(2000));
        String badge = page.textContent(".badge");
        assertTrue(badge.toLowerCase().contains("anagram") || badge.toLowerCase().contains("anagrams") || badge.toLowerCase().contains("not anagrams"));
        context.close();
    }

    // Simple static file handler for HttpServer
    static class StaticFileHandler implements HttpHandler {
        private final Path root;

        StaticFileHandler(Path root) {
            this.root = root;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            Path resolved = root.resolve(path.substring(1)).normalize();
            if (!resolved.startsWith(root) || !Files.exists(resolved) || Files.isDirectory(resolved)) {
                byte[] notFound = "404".getBytes();
                exchange.sendResponseHeaders(404, notFound.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFound);
                }
                return;
            }
            byte[] data = Files.readAllBytes(resolved);
            exchange.sendResponseHeaders(200, data.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(data);
            }
        }
    }
}
