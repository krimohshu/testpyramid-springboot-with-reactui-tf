-- Remove duplicate words keeping the smallest id (earliest)
DELETE FROM public.words a
USING public.words b
WHERE a.word = b.word AND a.id > b.id;

-- Create unique index on word to prevent future duplicates
CREATE UNIQUE INDEX IF NOT EXISTS ux_words_word ON public.words(LOWER(word));

-- Optional: vacuum analyze
VACUUM VERBOSE public.words;

