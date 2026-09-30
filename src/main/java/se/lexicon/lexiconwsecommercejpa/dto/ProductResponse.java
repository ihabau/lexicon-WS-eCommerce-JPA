package se.lexicon.lexiconwsecommercejpa.dto;

import java.math.BigDecimal;
import java.util.List;

/*
 * ProductResponse - the data WE SEND BACK about a product. Flattened TWICE over:
 * categoryName is a String rather than a Category object, and imageUrls is a
 * List<String> - no nested entity of any kind.
 *
 * WHY imageUrls is a LIST: a product can have several photos and one column can
 * only hold one value, so the links live in a second table (product_images, one
 * row per photo, tied back by product_id) - that is what @ElementCollection in
 * Product describes. The mapper copies the list across. A product read back from
 * the database always gets a collection from Hibernate, never nothing, so one with
 * no photos serialises as [] rather than null - which is what you want in JSON.
 * (A product built by hand in a test CAN hold null there, so a test-built object
 * may behave differently.)
 *
 * categoryName is a String on purpose - that is what "flattened" means in the
 * assignment. The name is not stored on the product; the mapper walks over to the
 * category and copies it. ONE THING TO KNOW ABOUT THAT WALK: Product.category is
 * LAZY, so it only resolves while the session is still open - close it first and
 * the mapper gets LazyInitializationException instead of a name. That is why
 * ProductRepository carries @EntityGraph("category") on findAll and
 * findByNameContaining. Same trap as Order.items.
 *
 * TWO THINGS TO KEEP DOING:
 *   - No validation annotations. They are only checked on data coming IN, so on a
 *     response they look like a safety net while checking nothing.
 *   - Spell the field names correctly. Jackson builds the JSON from these names,
 *     so a typo is not a private slip - it ships to clients as part of the API,
 *     and fixing it later breaks them.
 *
 * Also remember the constructor ORDER is a contract: the mapper must pass
 * id, name, price, imageUrls, categoryName in exactly that sequence.
 */
public record ProductResponse(

  Long id,
  String name,
  BigDecimal price,
  List<String> imageUrls,
  String categoryName
    ) {
}
