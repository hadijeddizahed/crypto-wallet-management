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
                        ad.symbol AS symbol,
                        ad.price AS price,
                        a.quantity AS quantity,
                        (
                            SELECT SUM(ad2.price * a2.quantity)
                            FROM assets a2
                            JOIN asset_details ad2 ON a2.asset_detail_id = ad2.id
                            WHERE a2.wallet_id = a.wallet_id
                        ) AS total_value
                    FROM assets a
                    JOIN asset_details ad ON a.asset_detail_id = ad.id
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
