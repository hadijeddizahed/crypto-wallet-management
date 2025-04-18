package com.swisspost.cryptowalletmanagement.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity(name = "assets")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssetEntity extends BaseEntity{

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_detail_id", nullable = false)
    private AssetDetailEntity assetDetail;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private WalletEntity wallet;

    public BigDecimal getQuantity() {
        return quantity;
    }

    public AssetDetailEntity getAssetDetail() {
        return assetDetail;
    }

    public WalletEntity getWallet() {
        return wallet;
    }
}
