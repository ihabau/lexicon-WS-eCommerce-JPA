package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/*
 * "Count all categories" has no method here, and that is correct, not an
 * omission. JpaRepository already provides count(), findAll(), findById(),
 * existsById(), save(), saveAll() and deleteById() for every entity in the
 * project. A derived query is only worth declaring when the ANSWER is not
 * already in the box.
 *
 * The subject of the name decides the return type:
 *   findByNameIgnoreCase -> Optional<Category>  (name is unique)
 *   existsByName         -> boolean             (same SELECT, asked differently)
 *   findByNameContaining -> List<Category>      (many can match)
 * The modifiers change only the WHERE clause: IgnoreCase adds LOWER(...),
 * Containing turns = into LIKE '%value%'. Containing is case-SENSITIVE; the
 * insensitive version would be findByNameContainingIgnoreCase.
 *
 * NOTE the asymmetry below: existsByName is case-SENSITIVE but
 * findByNameIgnoreCase is not. DataSeeder checks with one and reads back with
 * the other, and it only works because the two literals happen to be
 * character-for-character equal. Being consistent is the point.
 *
 * The check-then-act gap (two statements, a moment between) is why two
 * simultaneous inserts can both pass existsByName and one then fails on the
 * unique constraint. The CONSTRAINT is the real guarantee; existsByName only
 * turns that failure into a readable message. Same is true of existsByEmail.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

  // REQUIRED: find a category by name (case-insensitive).
  Optional <Category> findByNameIgnoreCase(String name);

  // REQUIRED: check whether a category with a given name exists.
  boolean existsByName(String name);

  // OPTIONAL: name contains a keyword.
  List<Category> findByNameContaining(String keyword);
}
