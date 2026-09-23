package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Category, Long> {

  Optional <Category> findByNameIgnoreCase(String name);

  boolean existsByName(String name);

  List<Category> findByNameContaining(String keyword);


  // REQUIRED (Part2.md:289-290): find a category by name (case-insensitive);
  // check whether a category with a given name exists.

  // OPTIONAL (Part2.md:293-294): categories whose name contains a keyword;
  // count all categories (already inherited from JpaRepository, so you may
  // not need to declare it).
}
