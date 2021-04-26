package com.romy.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romy.auth.entity.AuthKey;

@Repository
public interface AuthKeyRepository extends JpaRepository<AuthKey, String> {

}
