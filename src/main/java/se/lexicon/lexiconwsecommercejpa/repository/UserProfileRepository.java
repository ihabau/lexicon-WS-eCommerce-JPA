package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * =====================================================================
 * USERPROFILEREPOSITORY - Part 1 (workshop Part1.md:185-198).
 *
 * Interface only; Spring Data generates the implementation. CRUD is
 * inherited for free (findById, findAll, save, deleteById, ...).
 * =====================================================================
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    /*
     * TODO (REQUIRED - Part1.md:189): find a profile by nickname.
     *   Optional<UserProfile> findByNickName(String nickName);
     *
     *   TIP: Optional<> is the JpaRepository idiom for "at most one match"
     *   (instead of the old DAO's getSingleResult() + try/catch NoResultException).
     *   TIP: the method must spell "NickName" with a capital N to match the
     *   field name (UserProfile.nickName).
     */

    /*
     * TODO (REQUIRED - Part1.md:190): search by PARTIAL phone number.
     *   List<UserProfile> findByPhoneNumberContaining(String keyword);
     *   "Containing" === LIKE '%keyword%'.
     */

    /*
     * TODO (OPTIONAL - Part1.md:194): profiles that have a bio.
     *   List<UserProfile> findByBioIsNotNull();
     */

    /*
     * TODO (OPTIONAL - Part1.md:195): nicknames starting with a prefix.
     *   List<UserProfile> findByNickNameStartingWith(String prefix);
     */

    /*
     * TODO (OPTIONAL - Part1.md:197): count profiles with a phone prefix.
     *   Long countByPhoneNumberStartingWith(String prefix);
     *   TIP: returns Long, NOT a list.
     */

    /*
     * =====================================================================
     * EXTRA - carried over from the old UserProfileDAO, BEYOND the assignment.
     * Add these AFTER finishing the workshop.
     * =====================================================================
     *   // existence helpers (derived, note "existsBy" spelling):
     *   boolean existsByNickName(String nickName);
     *   boolean existsByPhoneNumber(String phoneNumber);
     */
}