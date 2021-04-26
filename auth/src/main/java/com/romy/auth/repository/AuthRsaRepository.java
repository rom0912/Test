package com.romy.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romy.auth.entity.AuthRsa;

@Repository
public interface AuthRsaRepository extends JpaRepository<AuthRsa, String> {

}
