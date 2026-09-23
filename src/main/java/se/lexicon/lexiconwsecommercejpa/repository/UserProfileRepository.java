package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;


public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

  Optional<UserProfile> findByNickName(String nickName);
  List<UserProfile> findByPhoneNumberContaining(String phoneNumberPart);
  List<UserProfile> findByBioIsNotNull();
  List<UserProfile> findByNickNameStartingWith(String prefix);
  Long countByPhoneNumberStartingWith(String prefix);
}
