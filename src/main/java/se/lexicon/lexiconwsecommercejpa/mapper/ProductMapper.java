
package se.lexicon.lexiconwsecommercejpa.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import se.lexicon.lexiconwsecommercejpa.dto.ProductRequest;
import se.lexicon.lexiconwsecommercejpa.dto.ProductResponse;
import se.lexicon.lexiconwsecommercejpa.entity.Category;
import se.lexicon.lexiconwsecommercejpa.entity.Product;

/*
 * Stateless translator: entity <-> DTO. @Component so a service can take it as a
 * constructor argument. No repository - see toEntity.
 *
 * THE TWO DIRECTIONS ARE NOT SYMMETRIC. toResponse is total: everything the
 * response needs is already on the entity. toEntity is where data is MISSING,
 * and that is the whole difficulty of this class.
 */
@Component
public class ProductMapper {

  // ==========================================================================
  // ENTITY -> DTO
  // ==========================================================================

  public ProductResponse toResponse(Product product) {

    //this is better than if null throw error
    Objects.requireNonNull(product, "Product must not be null!");

    // id, name, price are copied. categoryName and imageUrls are NOT columns:
    // they are walked out of other objects, which is why ProductResponse carries
    // a String and a List rather than a Category and a collection table row.
    //
    // ARGUMENT ORDER IS NOT FREE. A record has one canonical constructor, in
    // component order, and no setters to catch a swap. Four of the five
    // arguments are different types so the compiler helps; swapping two Strings
    // would compile and silently lie in the JSON.
    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getPrice(),
        toImageUrls(product),
        toCategoryName(product)
    );
  }

  // ==========================================================================
  // DTO -> ENTITY
  // ==========================================================================

  // The second parameter is deliberate. ProductRequest carries categoryId (a
  // number) and Product needs a Category OBJECT; turning one into the other
  // means talking to the database, and a mapper that does that is a service in
  // disguise. So the service resolves it and passes the found object in.
  public Product toEntity(ProductRequest request, Category category) {
    Objects.requireNonNull(request, "ProductRequest must not be null!");

    Product product = new Product();
    applyToProduct(product, request);
    product.setCategory(category);

    // ProductRequest has no imageUrls field, but Product.imageUrls is declared
    // with NO initialiser, so a new Product holds null there - and Hibernate
    // refuses to persist an entity with a null @ElementCollection.
    //   null = BUG,  [] = no photos (a normal state).
    // Note Product.promotions right next to it DOES initialise itself; that
    // asymmetry inside one class is the trap.
    product.setImageUrls(new ArrayList<>());

    return product;
  }

  // ==========================================================================
  // PRIVATE HELPERS
  // ==========================================================================

  // Nullable on a hand-built Product even though the column is nullable = false.
  // Returning null beats a NullPointerException here, which would point at the
  // mapper rather than at whatever forgot to set the category.
  private String toCategoryName(Product product) {
    Category category = product.getCategory();

    if (category == null) {
      return null;
    }

    return category.getName();
  }

  // Empty list rather than null: [] serialises to "no photos", whereas null
  // makes a client looping the field throw on their side.
  private List<String> toImageUrls(Product product) {
    List<String> imageUrls = product.getImageUrls();

    if (imageUrls == null) {
      return new ArrayList<>();
    }

    return imageUrls;
  }

  // Extracted so a field added to one of create/update cannot be forgotten in
  // the other.
  private void applyToProduct(Product product, ProductRequest request) {
    product.setName(request.name());
    product.setPrice(request.price());
  }
}
