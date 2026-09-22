package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * =====================================================================
 * PRODUCTREPOSITORY - Part 2 (workshop Part2.md:297-311).
 *
 * !!! BLOCKER: Product.java is a scaffold, not a real @Entity yet (no fields
 *     either). The nested queries below only resolve after you build the
 *     entity with a `category` column and a mapped Category.
 *
 * CRUD inherited for free.
 * =====================================================================
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /*
     * TODO (REQUIRED - Part2.md:299): products by the CATEGORY's name.
     *   List<Product> findByCategory_NameIgnoreCase(String categoryName);
     *   TIP: nested property Product -> Category -> name. Written with "_"
     *   to make the traversal unambiguous. Needs Product.category to exist.
     */

    /*
     * TODO (REQUIRED - Part2.md:300): products inside a price range.
     *   List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
     *   TIP: "Between" === price BETWEEN :min AND :max (both bounds inclusive).
     */

    /*
     * TODO (OPTIONAL - Part2.md:306): name contains a keyword.
     *   List<Product> findByNameContaining(String keyword);
     */

    /*
     * TODO (OPTIONAL - Part2.md:307): cheaper than a price.
     *   List<Product> findByPriceLessThan(BigDecimal price);
     */

    /*
     * TODO (OPTIONAL - Part2.md:308): sorted by price.
     *   List<Product> findAllByOrderByPriceAsc();
     *   List<Product> findAllByOrderByPriceDesc();
     */

    /*
     * TODO (OPTIONAL - Part2.md:309): count products in a category.
     *   Long countByCategory_Name(String categoryName);
     *   - or by FK id: Long countByCategory_Id(Long categoryId);
     */

    /*
     * TODO (OPTIONAL - Part2.md:310): products by category ID (FK).
     *   List<Product> findByCategory_Id(Long categoryId);
     */
}