package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

/*
 * findActivePromo NEEDS a @Query because of the rule: active means it started
 * on or before the date AND either it has no end date OR it ends on or after
 * that date. The second half is TWO conditions joined by OR, and one of them is
 * IS NULL. A method name cannot express the grouping ("AND (no end OR end >= date)")
 * correctly - the parentheses matter. Without them the query changes meaning,
 * which is why this is one of the cases where the name cannot say it and JPQL
 * must.
 *
 * findByEndDateIsNull() works fine as a derived query - the null test for a
 * simple property is a keyword. The @Query is needed for the OR, not the null.
 *
 * "Find promotions active today" is missing but OPTIONAL. Two ways to add it
 * without writing a second @Query:
 *   - a default method: default List<Promotion> findActiveToday() {
 *       return findActivePromo(LocalDate.now());
 *     }
 *   - or just call the existing method from the service with LocalDate.now().
 *
 * Promotion uses LocalDate, not Instant: it has a START DAY and an END DAY. A
 * promo valid "2026-01-01 to 2026-01-31" is valid for the whole of the 31st.
 * The parameter type must match the field exactly - findByStartDateAfter(LocalDate)
 * will not resolve against Instant.
 */
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

  Optional<Promotion> findByCode(String code);

  List<Promotion> findByStartDateAfter(LocalDate startDate);
  List<Promotion> findByEndDateBefore(LocalDate endDate);

  List<Promotion> findByEndDateIsNull();

  @Query("SELECT p FROM Promotion p WHERE p.startDate <= :date AND ( p.endDate IS NULL or p.endDate >= :date )")
  List<Promotion> findActivePromo(@Param("date") LocalDate date);
}
