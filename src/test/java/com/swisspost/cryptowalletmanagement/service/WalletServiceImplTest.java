package com.swisspost.cryptowalletmanagement.service;

import com.swisspost.cryptowalletmanagement.api.dto.AssetInfoRequest;
import com.swisspost.cryptowalletmanagement.api.dto.AssetRequest;
import com.swisspost.cryptowalletmanagement.api.dto.CreateWalletRequest;
import com.swisspost.cryptowalletmanagement.api.dto.EvaluateRequest;
import com.swisspost.cryptowalletmanagement.repository.AssetRepository;
import com.swisspost.cryptowalletmanagement.repository.UserRepository;
import com.swisspost.cryptowalletmanagement.repository.WalletRepository;
import com.swisspost.cryptowalletmanagement.repository.data.AssetSummary;
import com.swisspost.cryptowalletmanagement.repository.entity.AssetEntity;
import com.swisspost.cryptowalletmanagement.repository.entity.UserEntity;
import com.swisspost.cryptowalletmanagement.repository.entity.WalletEntity;
import com.swisspost.cryptowalletmanagement.service.dto.*;
import com.swisspost.cryptowalletmanagement.service.exceptions.BusinessException;
import com.swisspost.cryptowalletmanagement.service.exceptions.DuplicateUserException;
import com.swisspost.cryptowalletmanagement.service.pricing.PricingProviderService;
import com.swisspost.cryptowalletmanagement.service.wallet.WalletServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private PricingProviderService pricingProviderService;

    @InjectMocks
    private WalletServiceImpl walletService;

    private CreateWalletRequest request;
    private UserEntity userEntity;
    private WalletEntity walletEntity;
    private AssetRequest assetRequest;
    private SingleAssetResponse singleAssetResponse;
    private AssetSummary assetSummary;
    private EvaluateRequest evaluateRequest;
    private HistoricalAssetResponse historicalAssetResponse1;
    private HistoricalAssetResponse historicalAssetResponse2;

    @BeforeEach
    void setUp() {
        request = new CreateWalletRequest();
        request.setEmail("test@gmail.com");

        userEntity = new UserEntity();
        userEntity.setEmail("test@gmail.com");

        walletEntity = new WalletEntity();
        walletEntity.setId(1l);
        walletEntity.setUser(userEntity);

        assetRequest = new AssetRequest();
        assetRequest.setSymbol("BTC");
        assetRequest.setQuantity(BigDecimal.valueOf(1.5));

        singleAssetResponse = SingleAssetResponse.builder()
                .priceUsd(BigDecimal.valueOf(50000))
                .build();

        historicalAssetResponse1 = HistoricalAssetResponse.builder()
                .priceUsd(new BigDecimal("30000"))
                .symbol("bitcoin")
                .build();
        historicalAssetResponse2 = HistoricalAssetResponse.builder()
                .symbol("ethereum")
                .priceUsd(new BigDecimal("1500"))
                .build();

        assetSummary = new AssetSummary() {
            @Override
            public String getSymbol() {
                return "BTC";
            }

            @Override
            public BigDecimal getPrice() {
                return new BigDecimal("50000");
            }

            @Override
            public BigDecimal getQuantity() {
                return new BigDecimal("2");
            }

            @Override
            public BigDecimal getTotalValue() {
                return new BigDecimal("100000");
            }
        };

        evaluateRequest = new EvaluateRequest();
        evaluateRequest.setDate(LocalDate.of(2023, 1, 1));
        evaluateRequest.setAssets(List.of(
                new AssetInfoRequest("BTC", new BigDecimal("1.0"), BigDecimal.valueOf(1000)),
                new AssetInfoRequest("ETH", new BigDecimal("2.0"), BigDecimal.valueOf(1000))
        ));
    }

    @Test
    void shouldCreateWallet_whenUserIsNew_success() {
        // Mock objects
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(walletRepository.save(any(WalletEntity.class))).thenReturn(walletEntity);

        // call the api
        WalletResponseDTO response = walletService.create(request);

        // Assert and check
        assertNotNull(response);
        verify(userRepository).findByEmail(request.getEmail());
        verify(walletRepository).save(any(WalletEntity.class));
        verifyNoInteractions(assetRepository, pricingProviderService);
    }

    @Test
    void shouldCreateWallet_whenExistingUserWithoutWallet_success() {
        // Mock objects
        userEntity.setWalletEntity(null);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(userEntity));
        when(walletRepository.save(any(WalletEntity.class))).thenReturn(walletEntity);

        // call the api
        WalletResponseDTO response = walletService.create(request);

        // Assert and check
        assertNotNull(response);
        verify(userRepository).findByEmail(request.getEmail());
        verify(walletRepository).save(any(WalletEntity.class));
        verifyNoInteractions(assetRepository, pricingProviderService);
    }

    @Test
    void shouldThrowsDuplicateUserException_whenExistingUserWithWallet() {
        // Mock objects
        userEntity.setWalletEntity(walletEntity);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(userEntity));

        // Act and Assert
        assertThrows(DuplicateUserException.class, () -> walletService.create(request));
        verify(userRepository).findByEmail(request.getEmail());
        verifyNoInteractions(walletRepository, assetRepository, pricingProviderService);
    }

    @Test
    void shouldThrowsBusinessException_whenWalletNotFound() {
        // Mock objects
        when(walletRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> walletService.addAsset(1L, assetRequest));
        verify(walletRepository).findById(1L);
        verifyNoInteractions(pricingProviderService, userRepository, assetRepository);
    }

    @Test
    void shouldThrowsBusinessException_WhenAssetInfoNotFound() {
        // Mock objects
        when(walletRepository.findById(1L)).thenReturn(Optional.of(walletEntity));
        when(pricingProviderService.getSingleAssetInfo(any(SingleAssetRequest.class))).thenReturn(null);

        // Act & Assert
        assertThrows(BusinessException.class, () -> walletService.addAsset(1L, assetRequest));
        verify(walletRepository).findById(1L);
        verify(pricingProviderService).getSingleAssetInfo(any(SingleAssetRequest.class));
        verifyNoInteractions(userRepository, assetRepository);
    }

    @Test
    void shouldAddAsset_WhenNewAsset() {
        // Mock objects
        when(walletRepository.findById(1L)).thenReturn(Optional.of(walletEntity));
        when(pricingProviderService.getSingleAssetInfo(any(SingleAssetRequest.class))).thenReturn(singleAssetResponse);
        when(walletRepository.save(any(WalletEntity.class))).thenReturn(walletEntity);

        // call API
        WalletResponseDTO response = walletService.addAsset(1L, assetRequest);

        // Assert
        assertNotNull(response);
        verify(walletRepository).findById(1L);
        verify(pricingProviderService).getSingleAssetInfo(any(SingleAssetRequest.class));
        verify(walletRepository).save(any(WalletEntity.class));
        verifyNoInteractions(userRepository, assetRepository);
    }

    @Test
    void shouldUpdatesQuantity_When_existingAsset() {
        // Mock objects
        AssetEntity existingAsset = new AssetEntity("BTC", BigDecimal.valueOf(1.0), BigDecimal.valueOf(40000.0), walletEntity);
        walletEntity.getAssetEntities().add(existingAsset);
        when(walletRepository.findById(1L)).thenReturn(Optional.of(walletEntity));
        when(pricingProviderService.getSingleAssetInfo(any(SingleAssetRequest.class))).thenReturn(singleAssetResponse);
        when(walletRepository.save(any(WalletEntity.class))).thenReturn(walletEntity);

        // call API
        WalletResponseDTO response = walletService.addAsset(1L, assetRequest);

        // Assert
        assertNotNull(response);
        assertThat(walletEntity.getAssetEntities().get(0).getQuantity().compareTo(BigDecimal.valueOf(1.5))).isZero();
        verify(walletRepository).findById(1L);
        verify(pricingProviderService).getSingleAssetInfo(any(SingleAssetRequest.class));
        verify(walletRepository).save(any(WalletEntity.class));
        verifyNoInteractions(userRepository, assetRepository);
    }

    @Test
    void shouldAddsToExistingList_whenAssetIsNew() {
        // Mock objects
        AssetEntity existingAsset = new AssetEntity("ETH", BigDecimal.valueOf(1.0), BigDecimal.valueOf(2000.0), walletEntity);
        walletEntity.getAssetEntities().add(existingAsset);
        when(walletRepository.findById(1L)).thenReturn(Optional.of(walletEntity));
        when(pricingProviderService.getSingleAssetInfo(any(SingleAssetRequest.class))).thenReturn(singleAssetResponse);
        when(walletRepository.save(any(WalletEntity.class))).thenReturn(walletEntity);

        // call API
        WalletResponseDTO response = walletService.addAsset(1L, assetRequest);

        // Assert
        assertNotNull(response);
        assertEquals(2, walletEntity.getAssetEntities().size());
        verify(walletRepository).findById(1L);
        verify(pricingProviderService).getSingleAssetInfo(any(SingleAssetRequest.class));
        verify(walletRepository).save(any(WalletEntity.class));
        verifyNoInteractions(userRepository, assetRepository);
    }

    @Test
    void showWalletInfo_walletNotFound_throwsBusinessException() {
        // Arrange
        when(walletRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> walletService.showWalletInfo(1L, 10, 0));
        verify(walletRepository).findById(1L);
        verifyNoInteractions(assetRepository);
    }

    @Test
    void showWalletInfo_validWallet_returnsWalletInfoDTO() {
        // Mock objects
        List<AssetSummary> assetSummaries = List.of(assetSummary);
        PageImpl<AssetSummary> pageResult = new PageImpl<>(assetSummaries, PageRequest.of(0, 10), 1);
        when(walletRepository.findById(1L)).thenReturn(Optional.of(walletEntity));
        when(assetRepository.findAssetsWithTotalValueByWalletId(eq(1L), eq(PageRequest.of(0, 10)))).thenReturn(pageResult);

        // call the api
        WalletInfoDTO result = walletService.showWalletInfo(1L, 10, 0);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("100000"), result.totalValue());
        assertEquals(1, result.assets().size());
        assertEquals("BTC", result.assets().get(0).symbol());
        assertEquals(new BigDecimal("50000"), result.assets().get(0).price());
        assertEquals(new BigDecimal("2"), result.assets().get(0).quantity());
        assertEquals(new BigDecimal("100000"), result.assets().get(0).value());
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(1, result.totalElements());
        verify(walletRepository).findById(1L);
        verify(assetRepository).findAssetsWithTotalValueByWalletId(eq(1L), any(Pageable.class));
    }

    @Test
    void showWalletInfo_emptyAssets_returnsEmptyAssetList() {
        // Mock objects
        List<AssetSummary> assetSummaries = new ArrayList<>();
        PageImpl<AssetSummary> pageResult = new PageImpl<>(assetSummaries, PageRequest.of(0, 10), 0);
        when(walletRepository.findById(1L)).thenReturn(Optional.of(walletEntity));
        when(assetRepository.findAssetsWithTotalValueByWalletId(eq(1L), any(Pageable.class))).thenReturn(pageResult);

        // call the api
        WalletInfoDTO result = walletService.showWalletInfo(1L, 10, 0);

        // Assert and check
        assertNotNull(result);
        assertEquals(0L, result.totalElements());
        assertTrue(result.assets().isEmpty());
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(0, result.totalElements());
        verify(walletRepository).findById(1L);
        verify(assetRepository).findAssetsWithTotalValueByWalletId(eq(1L), any(Pageable.class));
    }

    @Test
    void evaluate_validRequest_returnsEvaluateDto() {
        // Mock objects
        when(pricingProviderService.getHistoricalInfo(HistoricalRequest.builder()
                .date(LocalDate.of(2023, 1, 1))
                .token("bitcoin")
                .build())).thenReturn(historicalAssetResponse1);
        when(pricingProviderService.getHistoricalInfo(HistoricalRequest.builder()
                .date(LocalDate.of(2023, 1, 1))
                .token("ethereum")
                .build())).thenReturn(historicalAssetResponse2);
        // call the Api
        EvaluateDto result = walletService.evaluate(evaluateRequest);

        // Assert
        assertNotNull(result);
        assertThat(new BigDecimal("33000").compareTo(result.total())).isZero(); // 1*30000 + 2*1500
        assertNotNull(result.bestAsset());
        assertNotNull(result.worstAsset());
    }

    @Test
    void evaluate_emptyAssets_returnsZeroTotal() {
        // Mock objects
        evaluateRequest.setAssets(List.of());
        // call
        EvaluateDto result = walletService.evaluate(evaluateRequest);

        // Assert and check
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.total());
        assertNull(result.bestAsset());
        assertNull(result.worstAsset());
        verifyNoInteractions(pricingProviderService);
    }

    @Test
    void evaluate_apiCallFails_throwsBusinessException() {
        // Mock objects
        when(pricingProviderService.getHistoricalInfo(any(HistoricalRequest.class)))
                .thenThrow(new RuntimeException("API error"));

        // Assert
        assertThrows(CompletionException.class, () -> walletService.evaluate(evaluateRequest));
        verify(pricingProviderService, times(2)).getHistoricalInfo(any(HistoricalRequest.class));
    }
}