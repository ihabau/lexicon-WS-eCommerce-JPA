package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.math.*;

/*
 * Three ways this file walks a nested property, and why each is different:
 *   findByCategoryId(Long id)              - you have the NUMBER
 *   findByCategoryNameIgnoreCase(String n) - you have the NAME
 *   countByCategoryNameIgnoreCase(String n)- you have the NAME, want a NUMBER
 *
 * Spring Data splits the name on capital letters and resolves each part against
 * the previous one, so `CategoryId` means category.id. That is legal only
 * because `category` is a real field on Product. REACHABILITY is the rule:
 * findByCustomerOrderItemProductName would not work, because Customer is not
 * reachable from Product.
 *
 * "Sort" is a CLAUSE, not a property, so it is written OrderBy and goes last:
 *   findAllBy | OrderBy | Price | Asc
 * Without the OrderBy keyword there is nothing to hang a sort on - it would try
 * to read "Price" as a filter and then fail on Asc, which is not a field.
 *
 * Between is INCLUSIVE on both ends (SQL BETWEEN is), so 10..20 includes 10 and
 * 20. Excluding an end needs a @Query, one of the few things a name cannot say.
 * "BiggerThan" and "MoreThan" are NOT keywords.
 *
 * countBy returns a single number and adds a COUNT to the SQL, so it returns
 * Long rather than List<Product>.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

  // @EntityGraph on exactly the two methods ProductServiceImpl maps.
  //
  // Product.category is LAZY and ProductMapper.toCategoryName calls
  // category.getName() - the second hop, which needs the row. The session closes
  // when the repository method returns, so mapping the products afterwards in the
  // service throws LazyInitializationException.
  //
  // Same fix as OrderRepository.findByStatus uses for "items", same reason: the
  // response needs a lazily-held association, so it must be fetched with the main
  // query. It also removes the N+1 - without it 20 products means 21 queries.
  // @Transactional on the service would also keep the session open, but that is
  // one transaction per read instead of one join.
  @EntityGraph(attributePaths = "category")
  List<Product> findAll();

  // REQUIRED: products by their category's name (case-insensitive, nested to
  // the mapped Category).
  List<Product> findByCategoryNameIgnoreCase(String name);

  // REQUIRED: products within a price range. BigDecimal, matching the field
  // exactly - money in a double loses cents.
  List<Product> findByPriceBetween(BigDecimal lowPrice, BigDecimal highPrice);

  // OPTIONAL: name contains a keyword.
  @EntityGraph(attributePaths = "category")
  List<Product> findByNameContaining(String keyword);

  // OPTIONAL: cheaper than a price.
  List<Product> findByPriceLessThan(BigDecimal price);

  // OPTIONAL: count products in a given category.
  Long countByCategoryNameIgnoreCase(String name);

  // OPTIONAL: products sorted by price, both directions.
  List<Product> findAllByOrderByPriceAsc();
  List<Product> findAllByOrderByPriceDesc();

  // OPTIONAL: products by the category's id (the foreign key).
  List<Product> findByCategoryId(Long id);
}
