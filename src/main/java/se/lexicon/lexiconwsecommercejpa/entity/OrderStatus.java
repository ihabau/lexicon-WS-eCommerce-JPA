package se.lexicon.lexiconwsecommercejpa.entity;

/*
 * A plain enum carries no JPA annotations. The database mapping lives on the
 * FIELD THAT USES it, which is:
 *
 *   @Enumerated(EnumType.STRING)
 *   private OrderStatus status;        // on Order
 *
 * That is the general rule: annotations describe a USAGE, not a type. The same
 * enum could be stored as text in one place and as a number in another without
 * this class knowing.
 *
 * With STRING, orders.status is VARCHAR and holds 'CREATED', 'PAID', ... Without
 * it, JPA stores the ORDINAL - the position in this list - as 0, 1, 2, 3, which
 * the assignment explicitly rules out by asking for "a readable string". The
 * reason that default is a trap: ordinals depend on the DECLARATION ORDER of this
 * file. Insert a constant in the middle and every existing row's meaning changes
 * underneath you. Adding "REFUNDED" to the END costs nothing with text storage.
 *
 * IF YOU ADD A STATE, check the two places that switch over these values:
 * OrderResponse.status carries this enum straight out to JSON, so a new constant
 * is a value clients will see; and a `switch` without a default will fail to
 * compile, which is the GOOD outcome because it forces the question.
 *
 * This enum says what states EXIST. It does not say which state a new order
 * starts in - Order.status has no Java default on purpose, because a default that
 * silently applies only on INSERT is exactly the sort of thing that gets
 * forgotten. The SERVICE sets it to CREATED explicitly, because that decision
 * belongs to the business layer, not the data model. (Status is the one Order
 * field @PrePersist deliberately does not fill in.)
 */
public enum OrderStatus {
    CREATED,
    PAID,
    SHIPPED,
    CANCELLED
}
