#!/usr/bin/env bash
# Download a large English wordlist (dwyl/english-words words_alpha.txt)
# Usage: ./scripts/download_wordlist.sh [output-path]
set -euo pipefail
OUT=${1:-data/words_alpha.txt}
mkdir -p "$(dirname "$OUT")"
echo "Downloading wordlist to $OUT..."
curl -L -o "$OUT" https://raw.githubusercontent.com/dwyl/english-words/master/words_alpha.txt
echo "Done. Lines: $(wc -l < "$OUT")"

