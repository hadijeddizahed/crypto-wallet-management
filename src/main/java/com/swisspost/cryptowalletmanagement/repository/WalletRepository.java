package com.swisspost.cryptowalletmanagement.repository;

import com.swisspost.cryptowalletmanagement.repository.entity.WalletEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, Long> {

    Page<WalletEntity> findAll(final Pageable pageable);

}
