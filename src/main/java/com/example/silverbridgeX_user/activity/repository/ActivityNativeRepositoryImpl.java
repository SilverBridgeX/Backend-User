package com.example.silverbridgeX_user.activity.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ActivityNativeRepositoryImpl implements ActivityNativeRepository{
    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void updateVectorByName(String name, String vectorLiteral) {
        em.createNativeQuery("""
        UPDATE activity
        SET description_embedding = ?::vector
        WHERE name = ?
    """)
                .setParameter(1, vectorLiteral)
                .setParameter(2, name)
                .executeUpdate();
    }

}
