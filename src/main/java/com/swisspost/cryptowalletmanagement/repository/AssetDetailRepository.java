package com.swisspost.cryptowalletmanagement.repository;

import com.swisspost.cryptowalletmanagement.repository.entity.AssetDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetDetailRepository extends JpaRepository<AssetDetailEntity, Long> {
    Optional<AssetDetailEntity> findBySymbol(final String symbol);
}
