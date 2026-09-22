package se.lexicon.lexiconwsecommercejpa.entity;

public enum OrderStatus {
    CREATED,
    PAID,
    SHIPPED,
    CANCELLED
    // TODO: stored in the DB as a STRING - remember @Enumerated(EnumType.STRING) in Order.status.
}