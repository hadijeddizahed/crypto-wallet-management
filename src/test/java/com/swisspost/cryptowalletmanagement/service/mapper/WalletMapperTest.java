package com.swisspost.cryptowalletmanagement.service.mapper;

import com.swisspost.cryptowalletmanagement.repository.entity.AssetEntity;
import com.swisspost.cryptowalletmanagement.repository.entity.UserEntity;
import com.swisspost.cryptowalletmanagement.repository.entity.WalletEntity;
import com.swisspost.cryptowalletmanagement.service.dto.AssetDto;
import com.swisspost.cryptowalletmanagement.service.dto.WalletResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WalletMapperTest {

    private WalletMapper walletMapper;

    @BeforeEach
    void setUp() {
        walletMapper = Mappers.getMapper(WalletMapper.class);
    }

    @Test
    void shouldMapsToWalletResponseDTO() {
        UserEntity user = new UserEntity();
        user.setEmail("test@gmail.com");

        AssetEntity asset = new AssetEntity();
        asset.setSymbol("BTC");
        asset.setQuantity(new BigDecimal("2.5"));
        asset.setPrice(new BigDecimal("40000"));

        WalletEntity wallet = new WalletEntity();
        wallet.setId(1L);
        wallet.setUser(user);
        wallet.setAssetEntities(List.of(asset));

        WalletResponseDTO result = walletMapper.toDto(wallet);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("test@gmail.com", result.email());
        assertEquals(1, result.assetsInfo().size());
        assertEquals("BTC", result.assetsInfo().get(0).symbol());
        assertEquals(new BigDecimal("2.5"), result.assetsInfo().get(0).quantity());
        assertEquals(new BigDecimal("40000"), result.assetsInfo().get(0).price());
        assertThat(new BigDecimal("100000").compareTo(result.assetsInfo().get(0).value())).isZero();
    }

    @Test
    void shouldMapsToAssetDto() {
        AssetEntity asset = new AssetEntity();
        asset.setSymbol("ETH");
        asset.setQuantity(new BigDecimal("10"));
        asset.setPrice(new BigDecimal("3000"));

        AssetDto result = walletMapper.toDto(asset);

        assertNotNull(result);
        assertEquals("ETH", result.symbol());
        assertEquals(new BigDecimal("10"), result.quantity());
        assertEquals(new BigDecimal("3000"), result.price());
        assertEquals(new BigDecimal("30000"), result.value());
    }

    @Test
    void shouldMapsToAssetDtoList() {
        AssetEntity asset1 = new AssetEntity();
        asset1.setSymbol("BTC");
        asset1.setQuantity(new BigDecimal("2"));
        asset1.setPrice(new BigDecimal("40000"));

        AssetEntity asset2 = new AssetEntity();
        asset2.setSymbol("ETH");
        asset2.setQuantity(new BigDecimal("5"));
        asset2.setPrice(new BigDecimal("3000"));

        List<AssetEntity> assets = List.of(asset1, asset2);

        List<AssetDto> result = walletMapper.toDtoList(assets);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("BTC", result.get(0).symbol());
        assertEquals(new BigDecimal("80000"), result.get(0).value());
        assertEquals("ETH", result.get(1).symbol());
        assertEquals(new BigDecimal("15000"), result.get(1).value());
    }

    @Test
    void shouldReturnsZero_whenNullQuantityOrPrice() {
        AssetEntity asset1 = new AssetEntity();
        asset1.setQuantity(null);
        asset1.setPrice(new BigDecimal("1000"));

        AssetEntity asset2 = new AssetEntity();
        asset2.setQuantity(new BigDecimal("2"));
        asset2.setPrice(null);

        BigDecimal result1 = walletMapper.calculateValue(asset1);
        BigDecimal result2 = walletMapper.calculateValue(asset2);

        assertEquals(BigDecimal.ZERO, result1);
        assertEquals(BigDecimal.ZERO, result2);
    }
}