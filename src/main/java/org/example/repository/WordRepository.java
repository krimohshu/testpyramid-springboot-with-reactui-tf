package org.example.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends CrudRepository<org.example.model.Word, Long> {

    boolean existsByNormalizedKeyAndWordIgnoreCase(String normalizedKey, String word);

    @Query(value = "SELECT w.word FROM Word w WHERE w.normalizedKey = :key ORDER BY w.word ASC")
    List<String> findByNormalizedKey(@Param("key") String key);

    // We'll implement a method to limit results using JPA query in service (fetch all then limit)
    default List<String> findTopNByNormalizedKey(String key, int n) {
        List<String> list = findByNormalizedKey(key);
        if (list.size() <= n) return list;
        return list.subList(0, n);
    }

    // Fetch Word entities up to a maximum length to aid combination searching
    @Query(value = "SELECT w FROM Word w WHERE w.length <= :maxLen")
    List<org.example.model.Word> findByLengthLessThanEqual(@Param("maxLen") int maxLen);
}
