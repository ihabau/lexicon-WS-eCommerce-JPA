package se.lexicon.lexiconwsecommercejpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.*;

/*
 * PROMOTION - Part 2 entity (Part2.md:251-262, 345-355).
 * TODO: build it.
 *  - a JPA entity mapped to the "promotions" table
 *  - identity-generated primary key (Long id)
 *  - code: mandatory, UNIQUE, max 100
 *  - startDate: mandatory
 *  - endDate: optional (null = open-ended)
 *  - OPTIONAL bidirectional many-to-many BACK to Product (the INVERSE side;
 *    Product owns the join table via its field named "promotions")
 */
public class Promotion {
    // TODO: add fields + JPA annotations per the requirements above.
}