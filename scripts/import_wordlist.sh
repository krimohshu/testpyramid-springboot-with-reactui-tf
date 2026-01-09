#!/usr/bin/env bash
# Usage: ./import_wordlist.sh [path/to/wordlist.txt]
set -euo pipefail
WORDLIST_PATH=${1-}

# Preflight: require Maven
if ! command -v mvn >/dev/null 2>&1; then
  echo "ERROR: mvn (Maven) not found on PATH. Install Maven or run the jar directly." >&2
  exit 1
fi

if [ -z "$WORDLIST_PATH" ]; then
  echo "No path provided — using bundled wordlist"
  mvn -Dspring-boot.run.profiles=import -DskipTests spring-boot:run
else
  echo "Importing wordlist from: $WORDLIST_PATH"
  mvn -Dspring-boot.run.profiles=import -Dwordlist.path="$WORDLIST_PATH" -DskipTests spring-boot:run
fi

