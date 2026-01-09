import React, { useState, useEffect, useRef } from 'react';
import './App.css';

function useDebounced(value, delay) {
  const [debounced, setDebounced] = useState(value);
  useEffect(() => {
    const t = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(t);
  }, [value, delay]);
  return debounced;
}

function Header() {
  return (
    <header className="site-header">
      <div className="site-brand">AnagramPro</div>
      <nav className="site-nav">
        <a href="#">Home</a>
        <a href="#features">Features</a>
        <a href="#about">About</a>
      </nav>
    </header>
  );
}

function Footer() {
  return (
    <footer className="site-footer">
      <div>© {new Date().getFullYear()} AnagramPro — Built with care</div>
      <div className="footer-links">
        <a href="#">Privacy</a>
        <a href="#">Terms</a>
        <a href="#">Contact</a>
      </div>
    </footer>
  );
}

function App() {
  const [input, setInput] = useState('');
  const debounced = useDebounced(input, 300);
  const [suggestions, setSuggestions] = useState([]);
  const [selectedIndex, setSelectedIndex] = useState(-1);
  const [isAnagram, setIsAnagram] = useState(null);
  const [other, setOther] = useState('');
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);
  const [comboResults, setComboResults] = useState([]);
  const mounted = useRef(true);
  const inputRef = useRef(null);
  const comboInputRef = useRef(null);

  useEffect(() => {
    mounted.current = true;
    return () => { mounted.current = false };
  }, []);

  // keyboard shortcuts: '/' or Ctrl/Cmd+K focus main input
  useEffect(() => {
    function onKey(e) {
      if (e.key === '/') {
        if (document.activeElement === inputRef.current) return;
        e.preventDefault();
        inputRef.current && inputRef.current.focus();
      }
      const isMac = navigator.platform.toUpperCase().indexOf('MAC') >= 0;
      if ((e.ctrlKey && !isMac) || (e.metaKey && isMac)) {
        if (e.key.toLowerCase() === 'k') {
          e.preventDefault();
          inputRef.current && inputRef.current.focus();
        }
      }
    }
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, []);

  useEffect(() => {
    // reset selection whenever suggestions change
    setSelectedIndex(-1);
  }, [suggestions]);

  // when selectedIndex changes, ensure item is visible
  useEffect(() => {
    if (selectedIndex >= 0) {
      const el = document.getElementById(`sugg-${selectedIndex}`);
      if (el && typeof el.scrollIntoView === 'function') {
        el.scrollIntoView({ block: 'nearest', inline: 'nearest' });
      }
    }
  }, [selectedIndex]);

  useEffect(() => {
    if (!debounced || debounced.trim().length === 0) {
      setSuggestions([]);
      return;
    }
    setLoading(true);
    fetch(`/api/suggestions?text=${encodeURIComponent(debounced)}&limit=10`)
      .then(r => r.json())
      .then(data => {
        if (!mounted.current) return;
        setSuggestions(data.suggestions || []);
      })
      .catch(() => setSuggestions([]))
      .finally(() => { if (mounted.current) setLoading(false); });
  }, [debounced]);

  function checkAnagram() {
    setErrorMessage(null);
    const payload = { input1: (input||'').trim(), input2: (other||'').trim() };
    fetch('/api/areAnagrams', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
      .then(r => {
        if (!r.ok) throw new Error('Server returned ' + r.status);
        return r.json();
      })
      .then(j => setIsAnagram(Boolean(j.areAnagrams)))
      .catch(err => { setIsAnagram(null); setErrorMessage('Anagram check failed: ' + err.message); });
  }

  function applySuggestion(s) {
    setInput(s ? s.trim() : s);
    setSuggestions([]);
    setSelectedIndex(-1);
    // return focus to input
    setTimeout(() => inputRef.current && inputRef.current.focus(), 0);
  }

  function onInputKeyDown(e) {
    if (suggestions.length === 0) return;
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setSelectedIndex(i => Math.min(i + 1, suggestions.length - 1));
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setSelectedIndex(i => Math.max(i - 1, 0));
    } else if (e.key === 'Enter') {
      // if a suggestion is highlighted, pick it
      if (selectedIndex >= 0 && selectedIndex < suggestions.length) {
        e.preventDefault();
        applySuggestion(suggestions[selectedIndex]);
      } else {
        // otherwise, maybe run the check
        // fallthrough: allow form submit behavior if needed
      }
    } else if (e.key === 'Escape') {
      setSuggestions([]);
      setSelectedIndex(-1);
    } else if (e.key === 'Tab') {
      if (selectedIndex >= 0 && selectedIndex < suggestions.length) {
        applySuggestion(suggestions[selectedIndex]);
        // allow tab to continue focusing
      }
    }
  }

  async function findCombinations() {
    setErrorMessage(null); setComboResults([]);
    const text = (comboInputRef.current?.value || '').trim();
    const maxWords = parseInt(document.getElementById('combo-maxwords').value || '2');
    const maxResults = parseInt(document.getElementById('combo-maxresults').value || '20');
    if (!text) { setErrorMessage('Enter text to search for combinations'); return; }
    try {
      const resp = await fetch(`/api/combinations?text=${encodeURIComponent(text)}&maxWords=${maxWords}&maxResults=${maxResults}`);
      if (!resp.ok) { const txt = await resp.text(); throw new Error('Server error: ' + resp.status + ' ' + txt); }
      const j = await resp.json(); const combos = j.combinations || [];
      if (combos.length) setComboResults(combos);
      else setErrorMessage('No combinations found');
    } catch (err) { setErrorMessage('Combination search failed: ' + err.message); }
  }

  return (
    <div className="app-root">
      <Header />
      <main className="content">
        <section className="hero">
          <div className="hero-card">
            <div className="hero-left">
              <h1>AnagramPro</h1>
              <p className="lead">Instant anagram checks and smart suggestions. Fast, accurate, and production-ready.</p>

              <label className="label">Input</label>
              <div className="input-row">
                <input id="anagram-input" ref={inputRef} className="input" value={input} onChange={e => setInput(e.target.value)} onKeyDown={onInputKeyDown} placeholder="Type a word or phrase..." />
                <button className="btn primary" onClick={() => { setOther(''); setIsAnagram(null); setComboResults([]); }}>Clear</button>
              </div>

              {loading && <div className="muted">Loading suggestions...</div>}
              {suggestions.length > 0 && (
                <ul className="suggestions-list" role="listbox" id="suggestion-list">
                  {suggestions.map((s, i) => (
                    <li id={`sugg-${i}`} key={i} role="option" aria-selected={i === selectedIndex} className={`suggestion-item ${i === selectedIndex ? 'highlight' : ''}`} onMouseEnter={() => setSelectedIndex(i)} onMouseLeave={() => setSelectedIndex(-1)} onClick={() => applySuggestion(s)}>
                      {s}
                    </li>
                  ))}
                </ul>
              )}

              <div className="compare-block">
                <label className="label">Compare with</label>
                <input id="compare-input" className="input" value={other} onChange={e => setOther(e.target.value)} placeholder="Second string to compare..." />
                <div className="actions">
                  <button className="btn" onClick={checkAnagram}>Check Anagram</button>
                  <div className={`badge ${isAnagram ? 'ok' : isAnagram === null ? 'muted' : 'bad'}`}>{isAnagram === null ? '—' : (isAnagram ? 'Anagrams' : 'Not anagrams')}</div>
                </div>
                {isAnagram === null && errorMessage && <div className="error">{errorMessage}</div>}
              </div>

            </div>

            <aside className="hero-right">
              <div className="card">
                <h3>Combination Finder</h3>
                <p className="muted">Provide letters and find 1–3 word combinations that use all letters.</p>
                <input ref={comboInputRef} id="combo-input" className="input" placeholder="Enter letters or phrase" />
                <div className="row-sm">
                  <label>Max words</label>
                  <select id="combo-maxwords" defaultValue={2} className="select"><option value={1}>1</option><option value={2}>2</option><option value={3}>3</option></select>
                  <label>Max results</label>
                  <input id="combo-maxresults" defaultValue={10} type="number" className="select" style={{width:80}} />
                </div>
                <div className="actions" style={{marginTop:8}}>
                  <button className="btn primary" onClick={findCombinations}>Find</button>
                  <button className="btn" onClick={() => { comboInputRef.current.value = ''; setComboResults([]); setErrorMessage(null); }}>Reset</button>
                </div>

                {comboResults && comboResults.length > 0 && (
                  <div className="combo-results">
                    {comboResults.map((c,i) => (
                      <button key={i} className="chip" onClick={() => { setInput(c); setComboResults([]); window.scrollTo({top:0,behavior:'smooth'}); }} title="Apply to main input">{c}</button>
                    ))}
                  </div>
                )}

                {errorMessage && <div className="error" style={{marginTop:8}}>{errorMessage}</div>}
              </div>

              <div className="card stats">
                <h4>Quick Actions</h4>
                <button className="btn" onClick={() => fetch('/api/admin/words?limit=20').then(r => r.json()).then(d => alert(JSON.stringify(d.rows,null,2))).catch(()=>alert('failed'))}>Show 20 words</button>
              </div>
            </aside>
          </div>
        </section>
      </main>
      <Footer />
    </div>
  );
}

export default App;

