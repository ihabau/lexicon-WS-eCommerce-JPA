package se.lexicon.lexiconwsecommercejpa.dao;

import se.lexicon.lexiconwsecommercejpa.entity.UserProfile;
import java.util.*;

public interface UserProfileDAO {

    // Required & optional queries per SpringBoot-DataJPA-Workshop-Part1.md (UserProfileRepository)

    UserProfile findById(long id);

    UserProfile findByNickName(String nickName);                       // required: find profile by nickname

    List<UserProfile> findByPhoneNumberContaining(String keyword);     // required: search by partial phone number

    List<UserProfile> findByBioIsNotNull();                            // optional: profiles with a bio

    List<UserProfile> findByNickNameStartingWith(String prefix);       // optional: nickname starts with a prefix

    Long countByPhoneNumberStartingWith(String prefix);                // optional: count profiles with a phone prefix

    // optional "created after a date" is N/A:
    // UserProfile has no createdAt column (see SpringBoot-DataJPA-Workshop-Part1.md)

    UserProfile save(UserProfile userProfile);
    List<UserProfile> findAll();
    UserProfile update(UserProfile userProfile);
    Boolean delete(UserProfile userProfile);
    Boolean deleteById(long id);
    Boolean existByNickName(String nickName);
    Boolean existByPhoneNumber(String phoneNumber);




}
