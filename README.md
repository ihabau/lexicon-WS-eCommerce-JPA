![Lexicon Logo](https://lexicongruppen.se/media/wi5hphtd/lexicon-logo.svg)

# Lexicon WS eCommerce JPA

Spring Boot e-commerce platform built with JPA, focusing on One-to-One relationships.

## Tech Stack

- Java 17
- Spring Boot 4.1.1
- Spring Data JPA
- H2 (runtime/test) + MySQL (production/development)
- Lombok
- Maven

## Project Checklist

### Setup

- [x] Maven `pom.xml` configured with required dependencies
- [x] Git repository initialized
- [x] Workshop files committed
- [x] Application starts without errors
- [x] Pushed to GitHub
- [x] Share with the teacher.
- [x] added resources/applicaition.properties
- [x] added prod properties

### Entities (Part 1)

- [ ] Address (table `addresses`, unidirectional, standalone)
- [ ] UserProfile (table `user_profiles`, inverse side with `mappedBy`)
- [ ] Customer (table `customers`, owner side)
  - [ ] Unidirectional One-to-One with Address (`address_id`)
  - [ ] Bidirectional One-to-One with UserProfile (`profile_id`, optional)
  - [ ] Cascading and orphan removal configured

### Repositories

- [ ] CustomerRepository
  - [ ] Find by email
  - [ ] Find by last name (case-insensitive)
  - [ ] Find by city
  - [ ] Optional: email contains keyword, created after/between dates, count by city, exists by email
- [ ] UserProfileRepository
  - [ ] Find by nickname
  - [ ] Find by partial phone number
  - [ ] Optional: bio not null, nickname prefix, count by phone prefix
- [ ] AddressRepository
  - [ ] Find by zip code
  - [ ] Optional: find by city, street name, zip code prefix, count by zip code

### Verification & Delivery

- [ ] Feature branch created (`feature/jpa-part1`)
- [ ] Application runs and schema generated correctly
- [ ] Descriptive commits for each major step
- [ ] Branch pushed to GitHub with link provided

## Workshop

See `SpringBoot-DataJPA-Workshop-Part1.md` for the full assignment details.