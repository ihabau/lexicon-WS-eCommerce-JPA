package se.lexicon.lexiconwsecommercejpa.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import se.lexicon.lexiconwsecommercejpa.entity.UserProfile;
import java.util.*;

@Repository
public class UserProfileDAOImpl implements UserProfileDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public UserProfile findById(long id) {
        return em.find(UserProfile.class, id);
    }

    @Override
    @Transactional
    public UserProfile save(UserProfile userProfile) {
        // TIP: merge() upserts - works for both new (id == 0) and existing profiles.
        return em.merge(userProfile);
    }

    @Override
    public List<UserProfile> findAll() {
        return em.createQuery("SELECT p FROM UserProfile p", UserProfile.class)
            .getResultList();
    }

    @Override
    @Transactional
    public UserProfile update(UserProfile userProfile) {
        return em.merge(userProfile);
    }

    @Override
    public UserProfile findByNickName(String nickName) {
        // TODO: exact, case-insensitive lookup:
        //      em.createQuery("SELECT p FROM UserProfile p WHERE LOWER(p.nickName) = LOWER(:nickName)", UserProfile.class)
        //        .setParameter("nickName", nickName.toLowerCase())
        //        .getSingleResult();
        // TIP: getSingleResult() throws NoResultException / NonUniqueResultException -
        //      use getResultList() + isEmpty() if duplicates are possible.
        return null;
    }

    @Override
    public List<UserProfile> findByPhoneNumberContaining(String keyword) {
        // TODO: partial phone-number search - use LIKE with the wildcards on ONE side:
        //      em.createQuery("SELECT p FROM UserProfile p WHERE LOWER(p.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))", UserProfile.class)
        //        .setParameter("keyword", keyword)
        //        .getResultList();
        return null;
    }

    @Override
    public List<UserProfile> findByBioIsNotNull() {
        // TODO: em.createQuery("SELECT p FROM UserProfile p WHERE p.bio IS NOT NULL", UserProfile.class)
        //        .getResultList();
        return null;
    }

    @Override
    public List<UserProfile> findByNickNameStartingWith(String prefix) {
        // TODO: prefix match - bind ":prefix" as prefix + "%":
        //      em.createQuery("SELECT p FROM UserProfile p WHERE LOWER(p.nickName) LIKE LOWER(:prefix)", UserProfile.class)
        //        .setParameter("prefix", prefix.toLowerCase() + "%")
        //        .getResultList();
        return null;
    }

    @Override
    public Long countByPhoneNumberStartingWith(String prefix) {
        // TODO: SELECT COUNT(p) ... WHERE p.phoneNumber LIKE :prefix, typed as Long.class;
        //      e.g. SELECT COUNT(p) FROM UserProfile p WHERE LOWER(p.phoneNumber) LIKE LOWER(:prefix),
        //      .setParameter("prefix", prefix.toLowerCase() + "%").getSingleResult();
        return 0L;
    }

    @Override
    @Transactional
    public Boolean delete(UserProfile userProfile) {
        // TODO: FK caveat - UserProfile is the INVERSE side of the OneToOne
        //      (mappedBy = "profile" on Customer). Deleting a profile the Customer still
        //      references breaks the profile_id FK. Null out customer.profile first
        //      (or rely on Customer's orphanRemoval), then em.remove the managed instance.
        return false;
    }

    @Override
    @Transactional
    public Boolean deleteById(long id) {
        // TODO: managed = em.find(UserProfile.class, id);
        //      if (managed != null) { em.remove(managed); return true; } return false;
        //      Same FK caveat as delete(UserProfile).
        return false;
    }

    @Override
    public Boolean existByNickName(String nickName) {
        // TODO: SELECT COUNT(p) FROM UserProfile p WHERE LOWER(p.nickName) = LOWER(:nickName)
        //      with Long.class, then return count > 0.
        return false;
    }

    @Override
    public Boolean existByPhoneNumber(String phoneNumber) {
        // TODO: SELECT COUNT(p) FROM UserProfile p WHERE p.phoneNumber = :phoneNumber
        //      with Long.class, then return count > 0.
        return false;
    }

}