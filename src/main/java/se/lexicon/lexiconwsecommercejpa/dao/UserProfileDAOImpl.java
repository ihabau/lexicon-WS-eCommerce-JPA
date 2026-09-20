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
        // same pattern as CustomerDAOImpl.findById
        return em.find(UserProfile.class, id);
    }

    @Override
    @Transactional
    public UserProfile save(UserProfile userProfile) {
        // same pattern as CustomerDAOImpl.save - merge upserts new and existing rows
        return em.merge(userProfile);
    }

    @Override
    public List<UserProfile> findAll() {
        // same pattern as CustomerDAOImpl.findAll
        return em.createQuery("SELECT p from UserProfile p", UserProfile.class)
            .getResultList();
    }

    @Override
    @Transactional
    public UserProfile update(UserProfile userProfile) {
        // same pattern as CustomerDAOImpl.update
        return em.merge(userProfile);
    }

    @Override
    @Transactional
    public Boolean delete(UserProfile userProfile) {
        // TIP: FK caveat - UserProfile is the INVERSE side (mappedBy = "profile").
        //      Deleting a profile a Customer still references can break the profile_id FK.
        // same pattern as CustomerDAOImpl.delete otherwise - remove the managed instance.
        UserProfile managed = em.find(UserProfile.class, userProfile.getId());

        if (managed != null) {
            em.remove(managed);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean deleteById(long id) {
        // TIP: same FK caveat as delete(UserProfile) above.
        // same pattern as CustomerDAOImpl.deleteById
        UserProfile userProfile = em.find(UserProfile.class, id);

        if (userProfile != null) {
            em.remove(userProfile);
            return true;
        }
        return false;
    }

    @Override
    public UserProfile findByNickName(String nickName) {
        // now correct - :nickName placeholder and .setParameter("nickName") match (case-sensitive)
        // TIP: the try/catch around getSingleResult() is good - it is the only way to avoid a
        //      NoResultException escaping when no profile matches.
        try {
        return em.createQuery("SELECT p FROM UserProfile p WHERE LOWER(p.nickName) = LOWER(:nickName)", UserProfile.class)
            .setParameter("nickName", nickName.toLowerCase())
            .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }

    @Override
    public List<UserProfile> findByPhoneNumberContaining(String keyword) {
        return em.createQuery("SELECT p FROM UserProfile p WHERE p.phoneNumber LIKE CONCAT('%', :keyword, '%')", UserProfile.class)
            .setParameter("keyword", keyword)
            .getResultList();
    }

    @Override
    public List<UserProfile> findByBioIsNotNull() {
        return em.createQuery("SELECT p FROM UserProfile p WHERE p.bio IS NOT NULL", UserProfile.class)
            .getResultList();
    }

    @Override
    public List<UserProfile> findByNickNameStartingWith(String prefix) {
        // now correct - surplus ")" removed, LIKE + CONCAT reads cleanly
        return em.createQuery("SELECT p FROM UserProfile p WHERE LOWER(p.nickName) LIKE CONCAT(:prefix, '%')", UserProfile.class)
            .setParameter("prefix", prefix)
            .getResultList();
    }

    @Override
    public Long countByPhoneNumberStartingWith(String prefix) {
        return em.createQuery("SELECT COUNT(p) FROM UserProfile p WHERE p.phoneNumber like CONCAT(:prefix, '%')", Long.class)
            .setParameter("prefix", prefix)
            .getSingleResult();
    }

    @Override
    public Boolean existByNickName(String nickName) {
        // now correct - field, placeholder and count-typing all match
        Long count = em.createQuery("SELECT COUNT(p) FROM UserProfile p WHERE LOWER(p.nickName) = LOWER(:nickName)", Long.class)
            .setParameter("nickName", nickName)
            .getSingleResult();
        
        return count > 0;
    }

    @Override
    public Boolean existByPhoneNumber(String phoneNumber) {
        // now correct - missing "=", Boolean.class and parameter binding all fixed
        Long count = em.createQuery("SELECT COUNT(p) FROM UserProfile p WHERE p.phoneNumber = :phoneNumber", Long.class)
            .setParameter("phoneNumber", phoneNumber)
            .getSingleResult();
        
        return count > 0;
    }

}
