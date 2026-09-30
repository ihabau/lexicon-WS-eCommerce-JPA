package se.lexicon.lexiconwsecommercejpa.dto;


import java.math.BigDecimal;
import jakarta.validation.constraints.*;

/*
 * ProductRequest - what a CUSTOMER SENDS US to create or change a product.
 *
 * Three different questions, one annotation each:
 *   "Is it there?"        -> @NotNull / @NotBlank
 *   "Is it a real price?" -> @Positive
 *   "Does it fit?"        -> @Digits and @Size
 * @NotNull on categoryId and price only means "the client sent something"; a
 * price of -5 is still something, so @Positive and @Digits are what stop it.
 *
 * @Digits(integer = 8, fraction = 2) is not arbitrary: the column is declared
 * precision = 10, scale = 2, and 10 - 2 leaves room for 8 digits before the dot.
 * Same rule written twice - once in the DTO so the client gets a clean 400, once
 * on the column so the database is the last line of defence. @Size(max = 100) on
 * name mirrors length = 100 the same way.
 *
 * @NotBlank only understands things that can be "empty" - text, lists, maps,
 * arrays. Put it on a number and the validator finds no rule to apply, throws
 * UnexpectedTypeException, and the caller gets a 500 instead of a 400. So: text
 * gets @NotBlank, numbers get @NotNull.
 *
 * `Long` (capital L), not `long`: a primitive can never be null, so a client
 * leaving categoryId out gets 0 and @NotNull passes anyway. Boxed types for data
 * coming IN, because only they can tell "missing" from "zero".
 *
 * THE ORDER OF THE FIELDS IS PART OF THE CONTRACT. Java builds the constructor
 * from the order written here, so ProductMapper must pass the values in exactly
 * this order, and the fields appear in the JSON in this order too.
 *
 * WHY THERE ARE NO imageUrls HERE. The assignment asks for name, price and
 * categoryId only, and leaving the field out is the recommended option. The one
 * thing you must not forget: Product.imageUrls has no initialiser (unlike
 * Product.promotions), so a new Product holds null and Hibernate refuses to save
 * a null @ElementCollection - the mapper has to give it `new ArrayList<>()`.
 * DataSeeder leaves it alone too, so every seeded product simply has no photos;
 * that is the normal case, not an edge case. Adding the field instead would force
 * three unanswered questions: what a missing list means, whether to validate each
 * URL, and whether an update replaces or appends.
 *
 * The message= TEXT IS PART OF THE CONTRACT - it is the only explanation the
 * caller ever sees, so say what is wrong in their language ("Price must be
 * provided!"), not which annotation failed ("@NotNull violated"), which means
 * nothing to someone who has never seen your code.
 */
public record ProductRequest(

      @NotNull(message = "CategoryId must be provided!")
    Long categoryId,


      @NotBlank(message = "Name must be provided!")
      @Size(max = 100, message = "Max length is 100 character!")
    String name,


      @NotNull(message = "Price must be provided!")
      @Positive(message = "Only positive number!")
      @Digits(integer = 8, fraction = 2, message = "Max number length is 8 digits with 2 fractions!")
    BigDecimal price
){}
