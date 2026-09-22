package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * =====================================================================
 * CATEGORYREPOSITORY - Part 2 (workshop Part2.md:287-296).
 *
 * !!! BLOCKER: Category.java is a scaffold, not a real @Entity yet.
 *     The app will fail at startup ("Not a managed type") until you build the
 *     entity first. Do the entity, THEN come back and add these methods.
 *
 * CRUD inherited for free.
 * =====================================================================
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /*
     * TODO (REQUIRED - Part2.md:289): find a category by name, case-insensitive.
     *   List<Category> findByNameIgnoreCase(String name);
     */

    /*
     * TODO (REQUIRED - Part2.md:290): does a category with this name exist?
     *   boolean existsByName(String name);
     *   TIP: note the "exists" spelling - the old DAO's existByName does NOT
     *   get picked up as a derived query.
     */

    /*
     * TODO (OPTIONAL - Part2.md:293): name contains a keyword.
     *   List<Category> findByNameContaining(String keyword);
     */

    /*
     * TODO (OPTIONAL - Part2.md:294): how many categories exist.
     *   long count();   <- actually already inherited from JpaRepository,
     *   so you may not even need to write it!
     */
}