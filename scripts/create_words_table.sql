-- Create `words` table for bulk import
CREATE TABLE IF NOT EXISTS public.words (
  id SERIAL PRIMARY KEY,
  word TEXT NOT NULL,
  normalized_key TEXT NOT NULL,
  length INT
);

CREATE INDEX IF NOT EXISTS idx_words_normalized_key ON public.words(normalized_key);

