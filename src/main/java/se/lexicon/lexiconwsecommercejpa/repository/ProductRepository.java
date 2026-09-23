package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.math.*;



public interface ProductRepository extends JpaRepository<Product, Long> {


  List<Product> findByCategoryNameIgnoreCase(String name);

  List<Product> findByPriceBetween(BigDecimal lowPrice, BigDecimal highPrice);

  List<Product> findByNameContaining(String keyword);

  List<Product> findByPriceLessThan(BigDecimal price);

  Long countByCategoryNameIgnoreCase(String name);

  List<Product> findAllByPriceAsc();
  List<Product> findAllByPriceDesc();

  List<Product> findByCategoryId(Long id);


  // REQUIRED (Part2.md:301-302): find products by their category's name
  // (case-insensitive, nested to the mapped Category); find products within
  // a price range (both bounds inclusive).

  // OPTIONAL (Part2.md:306-310): products whose name contains a keyword;
  // products cheaper than a price; 
  // products sorted by price, ascending and
  // descending; count products in a given category; find products by the
  // category's id (foreign key).
}
