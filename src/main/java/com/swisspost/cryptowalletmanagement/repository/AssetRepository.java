package com.swisspost.cryptowalletmanagement.repository;

import com.swisspost.cryptowalletmanagement.repository.data.AssetSummary;
import com.swisspost.cryptowalletmanagement.repository.entity.AssetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<AssetEntity, Long> {

    @Query(
            value = """
                    SELECT 
                        a.symbol AS symbol,
                        a.price AS price,
                        a.quantity AS quantity,
                        (SELECT SUM(price * quantity) FROM assets WHERE wallet_id = a.wallet_id) AS total_value
                    FROM assets a
                    WHERE a.wallet_id = :walletId
                    """,
            countQuery = """
                    SELECT COUNT(*) FROM assets a WHERE a.wallet_id = :walletId
                    """,
            nativeQuery = true
    )
    Page<AssetSummary> findAssetsWithTotalValueByWalletId(@Param("walletId") Long walletId,
                                                          Pageable pageable);

}
