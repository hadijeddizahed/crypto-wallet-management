package com.swisspost.cryptowalletmanagement.service.mapper;

import com.swisspost.cryptowalletmanagement.repository.entity.AssetEntity;
import com.swisspost.cryptowalletmanagement.repository.entity.WalletEntity;
import com.swisspost.cryptowalletmanagement.service.dto.AssetDto;
import com.swisspost.cryptowalletmanagement.service.dto.WalletResponseDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface WalletMapper {
    WalletMapper INSTANCE = Mappers.getMapper(WalletMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "assetEntities", target = "assetsInfo")
    WalletResponseDTO toDto(WalletEntity wallet);

    @Mapping(target = "value", source = "assetEntity", qualifiedByName = "calculateValue")
    AssetDto toDto(AssetEntity assetEntity);

    List<AssetDto> toDtoList(List<AssetEntity> assetEntities);

    @Named("calculateValue")
    default BigDecimal calculateValue(AssetEntity assetEntity) {
        if (assetEntity.getQuantity() == null || assetEntity.getPrice() == null) {
            return BigDecimal.ZERO;
        }
        return assetEntity.getQuantity().multiply(assetEntity.getPrice());
    }
}
