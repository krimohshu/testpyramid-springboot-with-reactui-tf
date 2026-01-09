# Anagram Service (MVP)

This repository contains a Spring Boot backend that exposes endpoints to check if a given input is an anagram of a dictionary word and to suggest up to 10 anagrams. A small sample dictionary is preloaded into an in-memory H2 database on startup for quick testing.

Run backend locally (requires Maven):

```bash
mvn -DskipTests spring-boot:run
```

Or build the jar:

```bash
mvn -DskipTests package
java -jar target/testpyramid-1.0-SNAPSHOT.jar
```

API examples:

POST /api/analyze
Request: { "text": "listen", "maxSuggestions": 10 }
Response: { "text": "listen", "isAnagramOfDictionaryWord": false, "suggestions": ["enlist","silent","inlets","tinsel"] }

GET /api/suggestions?text=listen&limit=5
Response: { "text": "listen", "suggestions": [...] }

POST /api/areAnagrams
Request: { "input1": "listen", "input2": "silent" }
Response: { "input1": "listen", "input2": "silent", "areAnagrams": true }

Swagger UI: http://localhost:8080/swagger-ui.html
H2 console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:testdb)


Web frontend (React)

A simple web frontend is provided under `web/`. It calls the backend endpoints for live suggestions and anagram checks.

To run the web app (requires Node.js >= 18):

```bash
cd web
npm install
npm start
```

Open http://localhost:3000 in your browser.

BDD e2e tests (Playwright + Cucumber)

The BDD feature files and step definitions are under `tests/` and call the backend directly. Ensure the backend is running at http://localhost:8080 before running the tests.

To run the BDD tests:

```bash
cd tests
npm install
npm run e2e
```

This will execute the feature `tests/features/anagram.feature` which covers the scenario outline you provided.

## Local dev with Docker

A docker-compose file is provided to start Postgres (for importer), RabbitMQ (for future queue), and Adminer (DB UI).

Start services:

```bash
docker-compose up -d
```

Run the importer (bundled small wordlist) against the Postgres container:

```bash
# from repo root
# set datasource URL to the dockerized Postgres
mvn -Dspring-boot.run.profiles=import \
    -Dspring.datasource.url=jdbc:postgresql://localhost:5432/anagramdb \
    -Dspring.datasource.username=anagram \
    -Dspring.datasource.password=anagram \
    -DskipTests spring-boot:run
```

Or use the helper script (it will set profile `import` and you can pass a path):

```bash
./scripts/import_wordlist.sh /path/to/your/large-wordlist.txt
```

Adminer (DB UI) is available at http://localhost:8081 (login with user `anagram` / password `anagram`).

```bash
# Optional stop services
docker-compose down -v
```
