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

Terraform & AWS notes

- Default AWS region in Terraform is `eu-west-2` (London).
- DB password:
  - If you do not set `TF_VAR_db_password` before running Terraform, a random secure password will be generated for you and exposed as a Terraform output `db_password` (sensitive).
  - To set your own password, export it before running Terraform:

```bash
export TF_VAR_db_password="your-strong-password"
```

CI: a GitHub Actions workflow is included at `.github/workflows/ci.yml` which runs `mvn test` and uploads the resulting HTML/JSON and surefire reports as artifacts.

Notes:
- UI Playwright tests are executed from Java but may attempt to download browsers; if that fails they skip gracefully.
- Integration tests use Testcontainers and require Docker available on the machine.
