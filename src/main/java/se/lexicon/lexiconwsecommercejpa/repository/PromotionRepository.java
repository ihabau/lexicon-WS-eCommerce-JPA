package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * =====================================================================
 * PROMOTIONREPOSITORY - Part 2 (workshop Part2.md:345-355).
 *
 * !!! BLOCKER: Promotion.java is a scaffold, not a real @Entity yet.
 *     Build the entity first, then add these methods.
 *
 * CRUD inherited for free.
 * =====================================================================
 */
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    /*
     * TODO (REQUIRED - Part2.md:347): promotions ACTIVE on a given date.
     *   Active = startDate <= date AND (endDate >= date OR endDate is null).
     *   TIP: a method NAME cannot express the OR + IS NULL branch, so this one
     *   needs @Query:
     *
     *   @Query("SELECT p FROM Promotion p WHERE p.startDate <= :date " +
     *          "AND (p.endDate IS NULL OR p.endDate >= :date)")
     *   List<Promotion> findActiveOn(LocalDate date);
     *
     *   Needs Promotion.startDate + Promotion.endDate to exist.
     */

    /*
     * TODO (OPTIONAL - Part2.md:351): by unique code.
     *   Promotion findByCode(String code);
     *   TIP: code is unique in the DB -> single result is fine here.
     */

    /*
     * TODO (OPTIONAL - Part2.md:352-354):
     *   List<Promotion> findByStartDateAfter(LocalDate date);
     *   List<Promotion> findByEndDateBefore(LocalDate date);
     *   List<Promotion> findByEndDateIsNull();   // open-ended promotions
     */

    /*
     * TODO (OPTIONAL - Part2.md:355): active today.
     *   Reuse findActiveOn(LocalDate.now()) from a service, or add a default:
     *   default List<Promotion> findActiveToday() {
     *       return findActiveOn(LocalDate.now());
     *   }
     */
}