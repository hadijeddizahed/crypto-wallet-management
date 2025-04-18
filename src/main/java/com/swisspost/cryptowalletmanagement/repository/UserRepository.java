package com.swisspost.cryptowalletmanagement.repository;

import com.swisspost.cryptowalletmanagement.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(final String email);

    boolean existsByEmail(final String email);
}
