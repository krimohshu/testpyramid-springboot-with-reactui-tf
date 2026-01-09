#!/usr/bin/env bash
# Prepare wordlist CSV with normalized_key and then bulk COPY into Postgres
# Usage: ./scripts/prepare_and_copy.sh /path/to/wordlist.txt
set -euo pipefail
WORDLIST=${1:-data/words_alpha.txt}
CSV_OUT=${2:-/tmp/words_import.csv}
DB_URL=${3:-jdbc:postgresql://localhost:5432/anagramdb}
DB_USER=${4:-anagram}
DB_PASS=${5:-anagram}

if [ ! -f "$WORDLIST" ]; then
  echo "Wordlist not found at $WORDLIST"
  exit 1
fi

echo "Preparing CSV at $CSV_OUT..."

python3 - "$WORDLIST" "$CSV_OUT" <<'PY'
import sys, unicodedata
inp = sys.argv[1]
out = sys.argv[2]

def normalize(s):
    s = s.lower()
    s = ''.join(ch for ch in s if ch.isalpha())
    s = unicodedata.normalize('NFD', s)
    s = ''.join(ch for ch in s if not unicodedata.combining(ch))
    return ''.join(sorted(s))

with open(inp, 'r', encoding='utf-8') as fin, open(out, 'w', encoding='utf-8') as fout:
    for line in fin:
        w = line.strip()
        if not w:
            continue
        key = normalize(w)
        if not key:
            continue
        # CSV: word,normalized_key,length
        fout.write('"{}","{}",{}\n'.format(w.replace('"','""'), key, len(w)))

print('Wrote CSV to', out)
PY

echo "CSV ready. Import via psql COPY (requires psql client and DB accessible)."
echo "Example: PGPASSWORD=$DB_PASS psql -h localhost -p 5432 -U $DB_USER -d anagramdb -c \"\copy words(word, normalized_key, length) FROM '$CSV_OUT' CSV\""
