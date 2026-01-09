Testing and CI

Run all Java tests (unit, integration, BDD, UI if available):

```bash
# run everything (requires Docker for Testcontainers)
mvn -DskipTests=false test

# view cucumber HTML reports
open target/cucumber-report-1.html
open target/cucumber-report-2.html

# merged JSON (created by exec plugin in verify phase)
cat target/cucumber-merged.json | jq '.'
```

CI: a GitHub Actions workflow is included at `.github/workflows/ci.yml` which runs `mvn test` and uploads the resulting HTML/JSON and surefire reports as artifacts.

Notes:
- UI Playwright tests are executed from Java but may attempt to download browsers; if that fails they skip gracefully.
- Integration tests use Testcontainers and require Docker available on the machine.

