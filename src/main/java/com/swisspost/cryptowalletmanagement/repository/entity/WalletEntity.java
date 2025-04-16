package com.swisspost.cryptowalletmanagement.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "wallets")
@EqualsAndHashCode(callSuper = true)
@Data
@Getter
@NoArgsConstructor
public class WalletEntity extends BaseEntity{

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "id", unique = true, nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssetEntity> assetEntities = new ArrayList<>();

    public UserEntity getUser() {
        return user;
    }

    public List<AssetEntity> getAssetEntities() {
        return assetEntities;
    }
}
