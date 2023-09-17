package com.ys.practice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ys.practice.entity.TotpDetails;

@Repository
public interface TotpRepository extends CrudRepository<TotpDetails, String> {

    TotpDetails findByUsername(String username);
    boolean existsByUsername(String username);
    Long deleteByUsername(String username);
}
