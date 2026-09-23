package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;


public interface PromotionRepository extends JpaRepository<Promotion, Long> {

  Optional<Promotion> findByCode(String code);

  List<Promotion> findByStartDateAfter(LocalDate startDate);
  List<Promotion> findByEndDateBefore(LocalDate endDate);

  List<Promotion> findByEndDateIsNull();

  @Query("SELECT p FROM Promotion p WHERE p.startDate <= :date AND ( p.endDate IS NULL or p.endDate >= :date )")
  List<Promotion> findActivePromo(@Param("date") LocalDate date);


  // REQUIRED (Part2.md:348): find promotions active on a given date - active
  // means it started on/before that date AND either has no end date or ends
  // on/after it. A method name can't express the OR + IS NULL branch, so a
  // custom query is required.

  // OPTIONAL (Part2.md:351-355): find a promotion by its unique code; find
  // promotions starting after a date; ending before a date; with no end date;
  // find promotions active today (can reuse the "active on" query).
}
