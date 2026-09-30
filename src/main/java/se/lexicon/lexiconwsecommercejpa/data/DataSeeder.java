package se.lexicon.lexiconwsecommercejpa.data;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import se.lexicon.lexiconwsecommercejpa.entity.Category;
import se.lexicon.lexiconwsecommercejpa.entity.Product;
import se.lexicon.lexiconwsecommercejpa.repository.CategoryRepository;
import se.lexicon.lexiconwsecommercejpa.repository.ProductRepository;

/*
 * Extra task (Part 2): automatically insert test data on application start.
 * RULES: categories BEFORE products (products reference them), and insert only
 * ONCE - if the data already exists the app must still start.
 *
 * WHY CommandLineRunner, not @PostConstruct: it runs after the context is fully
 * built and the DataSource is ready. @PostConstruct runs during bean creation,
 * which is too early to be sure the EntityManagerFactory and the schema exist -
 * and it runs even when you start the app for a different purpose. This hook only
 * fires on a real application start, so a @DataJpaTest slice never triggers it.
 *
 * THE TWO "ONLY ONCE" CHECKS ARE DELIBERATELY DIFFERENT, and that is a trade-off
 * worth being able to explain:
 *   seedCategories -> PER-NAME (existsByName). Add a fourth category next week
 *     and only that one is inserted; the others are recognised and left alone.
 *     Delete one seeded row and the next start puts it back.
 *   seedProducts   -> WHOLE-TABLE (count() > 0). All or nothing: if the table
 *     already holds ONE row - one you added by hand - the seeder assumes it has
 *     run and inserts NOTHING.
 * count() is the shorter code; existsByName is the more careful one. "The data
 * appears once" and "each row appears once" are different requirements, and the
 * second is what "avoid duplicates" really asks for.
 *
 * INCONSISTENCY worth knowing: the category check is case-SENSITIVE
 * (existsByName) but the read-back is case-INSENSITIVE (findByNameIgnoreCase).
 * It works only because the two literals are character-for-character equal. A
 * one-word fix: add existsByNameIgnoreCase to CategoryRepository and use that.
 *
 * The category is re-READ in seedProducts rather than held in a field. That looks
 * redundant, but it is what makes the second method safe on its own: if someone
 * reorders run(), orElse(null) hands the mapper a null Category and the save
 * fails on the not-null category_id.
 *
 * NO @Transactional HERE, on purpose. Each save commits on its own, so if
 * category 3 of 3 fails, categories 1 and 2 are already in the database and the
 * next start's existsByName checks carry on - the seeder heals itself. One
 * transaction over the whole class would roll the batch back and abort the
 * application start, which is the opposite of "the app should still start".
 * (placeOrder IS @Transactional: that one genuinely must be all-or-nothing.)
 *
 * createProduct never touches imageUrls, and that is fine - a null
 * @ElementCollection on a NEW entity is stored as "no rows", so every seeded
 * product just has no photos. Order.items and Category.products are the ones that
 * genuinely need a list, because they are cascade + orphanRemoval and an Order
 * must have at least one item or @PrePersist throws.
 *
 * This seeder covers categories and products ONLY. It does not create customers,
 * promotions or orders - orders in particular would have to be built with their
 * items in one go, which is exactly what OrderMapper is for.
 */
@Component
public class DataSeeder implements CommandLineRunner {

  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;

  public DataSeeder(CategoryRepository categoryRepository, ProductRepository productRepository) {
    this.categoryRepository = categoryRepository;
    this.productRepository = productRepository;
  }

  @Override
  public void run(String... args) {
    seedCategories();
    seedProducts();
  }

  private void seedCategories() {
    List<Category> categories = List.of(
      createCategory("Men's Wear"),
      createCategory("Women's Wear"),
      createCategory("Accessories")
    );

    for (Category category : categories) {
      if (!categoryRepository.existsByName(category.getName())) {
        categoryRepository.save(category);
      }
    }
  }

  private void seedProducts() {
    if (productRepository.count() > 0) {
      return;
    }

    Category mensWear = categoryRepository.findByNameIgnoreCase("Men's Wear").orElse(null);
    Category womensWear = categoryRepository.findByNameIgnoreCase("Women's Wear").orElse(null);
    Category accessories = categoryRepository.findByNameIgnoreCase("Accessories").orElse(null);

    productRepository.saveAll(List.of(
      createProduct("Classic T-Shirt", new BigDecimal("19.99"), mensWear),
      createProduct("Slim Fit Jeans", new BigDecimal("59.99"), mensWear),
      createProduct("Summer Dress", new BigDecimal("49.99"), womensWear),
      createProduct("Leather Handbag", new BigDecimal("89.99"), womensWear),
      createProduct("Sunglasses", new BigDecimal("29.99"), accessories),
      createProduct("Canvas Watch", new BigDecimal("99.99"), accessories)
    ));
  }

  private Category createCategory(String name) {
    Category category = new Category();
    category.setName(name);
    return category;
  }

  private Product createProduct(String name, BigDecimal price, Category category) {
    Product product = new Product();
    product.setName(name);
    product.setPrice(price);
    product.setCategory(category);
    return product;
  }
}
