package br.usp.lucas.applicationbackend.user;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class UserRepository {
    private final EntityManager entityManager;

    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<User> getAll() {
        return entityManager.createNamedQuery("User.getAll", User.class)
                .getResultList();
    }

    public User getById(Integer id) {
        return entityManager.createNamedQuery("User.getById", User.class)
                .setParameter("id", id)
                .getSingleResult();
    }
}
