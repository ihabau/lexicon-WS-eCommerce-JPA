package se.lexicon.lexiconwsecommercejpa.data;

import org.springframework.stereotype.Component;

// Extra task (Part 2): automatically insert test data on application start.
// TODO: implement CommandLineRunner and/or use Spring Boot's data.sql loading.

@Component
public class DataSeeder {

    // TODO: implement this class:
    //      public class DataSeeder implements CommandLineRunner { ... }
    //      override run() and insert the test data there.

    //      RULES from the workshop:
    //      1. Categories must be created BEFORE products (products reference them).
    //      2. Insert only ONCE - if the data already exists, the app must still start.
    //         (check-by-exists before inserting, e.g. use CategoryDAO.existByName before saving.)
    //      3. Seed: a list of categories, then products linked to those categories.
    //         (promotions / sample orders are a nice bonus for testing the N+1-safe query.)

    //      TIP: you can inject the DAOs:
    //      private final CategoryDAO categoryDAO;
    //      private final ProductDAO productDAO;
    //      (constructor injection - Spring auto-wires a single constructor).

}