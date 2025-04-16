package com.swisspost.cryptowalletmanagement.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "users")
@EqualsAndHashCode(callSuper = true)
@Data
public class UserEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private WalletEntity walletEntity;

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public WalletEntity getWalletEntity() {
        return walletEntity;
    }
}
