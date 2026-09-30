package se.lexicon.lexiconwsecommercejpa.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * CustomerRequest - INBOUND data: the registration / profile-update payload.
 *
 * THE RULE THAT MADE THE OTHER CONSTRAINTS NECESSARY: for every `nullable = false`
 * column on the way in, put a constraint on the way in. street, city and zipCode
 * are all mandatory in Address but carry only @NotBlank here, so the database is
 * the only guard and a violation surfaces at flush as a 500 with a stack trace
 * instead of a 400 that names the field. Adding @Size(max = 255) to the three
 * address components would close that; it is optional, but worth saying out loud.
 *
 * WHY @Size(min = 6) ON password, WHEN EVERY OTHER @Size IS A max: the maxes
 * mirror a COLUMN length (firstName/lastName match Customer exactly, email at
 * 100 is stricter than the 150 column). A minimum is a different kind of rule -
 * not a storage limit, no column to mirror, but a POLICY decision. @NotBlank says
 * "something was sent"; @Size(min) says "long enough to be worth having".
 *
 * @Email AND @NotBlank ARE NOT REDUNDANT. "not-an-email" is a perfectly non-blank
 * string, so without @Email a nonsense address reaches the database and the
 * unique constraint happily stores it. Note @Email is imported from
 * jakarta.validation above, not from org.hibernate.validator - both work, the
 * jakarta one is the Bean Validation standard.
 *
 * `password` IS ACCEPTED AND THEN SILENTLY DROPPED, and that is correct for now.
 * Customer has no password column and CustomerMapper.applyToCustomer copies only
 * firstName/lastName/email, so a component nothing reads is a promise the API
 * does not keep - the entity would have to gain the column, which is outside the
 * Part 1 spec. It must NEVER come back out either: CustomerResponse has no
 * password field, and that is the half that matters.
 *
 * WORTH KNOWING: a record's generated toString() prints every component, so
 * logging this object prints the password in plain text. A DTO holding a secret
 * needs an explicit toString() override, or must simply never be logged.
 *
 * DTO LIMITS AND COLUMN LENGTHS ARE TWO CONTRACTS (@Size(max = 100) on email
 * against a length = 150 column). Being stricter in the DTO is legitimate - just
 * make it a choice deliberately.
 *
 * NOT DONE, optional: normalise at the boundary in a compact constructor -
 * trimming and lowercasing email. It is the login identity, so "Alice@Example.com "
 * and "alice@example.com" are one person to a human and two rows to a unique
 * index. Be aware a compact constructor runs BEFORE Bean Validation, so @Email
 * would then be checking something the client did not literally send.
 */
public record CustomerRequest(
      @NotBlank(message = "FirstName must be provided!")
      @Size(max = 100, message = "Max length of firstName must be less than 100 characters!")
      String firstName,
      @NotBlank(message = "LastName must be provided!")
    @Size(max = 100, message = "Max length of lastName must be less than 100 characters!")
      String lastName,
      @NotBlank(message = "Email must be provided!")
      @Email(message = "Invalid email format!")
      @Size(max = 100, message = "Max length of email must be less than 100 characters!")
      String email,
      @NotBlank(message = "Password must be provided!")
      @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters!")
      String password,
      @NotBlank(message = "Street must be provided!")
      String street,
      @NotBlank(message = "City must be provided!")
      String city,
      @NotBlank(message = "ZipCode must be provided!")
      String zipCode
    ) {


}
