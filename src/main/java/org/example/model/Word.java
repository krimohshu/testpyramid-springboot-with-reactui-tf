package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "words", indexes = {
        @Index(name = "idx_normalized_key", columnList = "normalized_key")
})
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "normalized_key", nullable = false)
    private String normalizedKey;

    @Column(name = "length")
    private Integer length;

    public Word() {}

    public Word(String word, String normalizedKey, Integer length) {
        this.word = word;
        this.normalizedKey = normalizedKey;
        this.length = length;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    public String getNormalizedKey() { return normalizedKey; }
    public void setNormalizedKey(String normalizedKey) { this.normalizedKey = normalizedKey; }
    public Integer getLength() { return length; }
    public void setLength(Integer length) { this.length = length; }
}

