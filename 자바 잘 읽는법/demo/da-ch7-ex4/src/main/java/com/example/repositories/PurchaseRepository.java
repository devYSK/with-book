package com.example.repositories;

import com.example.entities.Product;
import com.example.entities.Purchase;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Repository
public class PurchaseRepository {

  private final EntityManager entityManager;

  public PurchaseRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public List<Purchase> findAll() {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Purchase> cq = cb.createQuery(Purchase.class);

    cq.from(Purchase.class);

    TypedQuery<Purchase> query = entityManager.createQuery(cq);
    return query.getResultList();
  }
}