package se.lexicon.lexiconwsecommercejpa.dto;

/*
 * CategoryResponse - the smallest DTO in the package: id and name, no decisions
 * left to make.
 *
 * `id` is the database's own number for the category, not something a human
 * chose. That is fine to publish, and genuinely useful: ProductRequest.categoryId
 * refers to exactly this number, so a client has to be able to read it in order
 * to create a product.
 *
 * `Long id`, matching every other id in the project. A primitive `long` would also
 * work here - a response is built from data that already exists - but it can never
 * be null, so a mapper that forgot the id would emit a silent 0 instead of an
 * obvious null. The capital letter costs nothing and keeps one rule for every id.
 *
 * No validation annotations, and that is the rule for every response: they are
 * only checked on data coming IN, so here they look like a safety net while
 * checking nothing. What a response may carry is serialization setup -
 * @JsonProperty, @JsonInclude(NON_NULL), @JsonFormat.
 *
 * There is no CategoryRequest, and the spec does not ask for one: the optional
 * CategoryService has create(String name) take a plain String, so the service
 * builds the entity itself and uses CategoryRepository.existsByName as the
 * duplicate check. A record nobody sends is dead weight.
 *
 * For later: Category has a `products` back-reference, so putting the Category
 * ENTITY into a response is the recursion trap. This record is the way out.
 *
 * NOT DONE: the optional CategoryService whose findAll() would return
 * List<CategoryResponse> does not exist. It is optional, so it does not block
 * submission, and every repository method it needs is already there - it is a
 * @Service, constructor injection of CategoryRepository, and a findAll() mapping
 * each category to new CategoryResponse(c.getId(), c.getName()).
 */
public record CategoryResponse(

    Long id,
    String name

    ) {
}
