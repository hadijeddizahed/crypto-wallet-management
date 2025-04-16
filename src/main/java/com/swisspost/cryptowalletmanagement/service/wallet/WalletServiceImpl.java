package com.swisspost.cryptowalletmanagement.service.wallet;

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
import com.swisspost.cryptowalletmanagement.service.mapper.WalletMapper;
import com.swisspost.cryptowalletmanagement.service.pricing.PricingApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final AssetRepository assetRepository;

    @Qualifier("CoinCapPricingService")
    private final PricingApiService coinCapService;

    public WalletResponseDTO create(final CreateWalletRequest request) {
        Optional<UserEntity> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            UserEntity user = existingUser.get();
            if (user.getWalletEntity() != null) {
                throw new DuplicateUserException("A wallet already exists for this user.");
            }

            WalletEntity wallet = new WalletEntity();
            wallet.setUser(user);
            user.setWalletEntity(wallet);
            return WalletMapper.INSTANCE.toDto(walletRepository.save(wallet));
        }

        // Create new user and wallet
        UserEntity newUser = new UserEntity();
        newUser.setEmail(request.getEmail());

        WalletEntity newWallet = new WalletEntity();
        newWallet.setUser(newUser);
        newUser.setWalletEntity(newWallet);

        return WalletMapper.INSTANCE.toDto(walletRepository.save(newWallet));
    }


    @Override
    public WalletResponseDTO addAsset(final Long walletId, final AssetRequest request) {
        final Optional<WalletEntity> optionalWallet = walletRepository.findById(walletId);
        if (optionalWallet.isEmpty()) {
            throw new BusinessException("Wallet not found");
        }

        final AssetInfo assetInfo = coinCapService.getSingleAssetInfo(AssetEnum.findBySymbol(request.getSymbol()).getName());
        if (assetInfo == null) {
            throw new BusinessException("No available info for this asset");
        }
        final var wallet = optionalWallet.get();
        final var assetEntity = new AssetEntity(
                request.getSymbol(),
                request.getQuantity(),
                assetInfo.priceUsd(),
                wallet
        );
        if (optionalWallet.get().getAssetEntities().isEmpty()) {
            wallet.getAssetEntities().add(assetEntity);
        } else {
            boolean found = false;
            for (AssetEntity assetEntity1 : wallet.getAssetEntities()) {
                if (assetEntity1.getSymbol().equals(request.getSymbol())) {
                    assetEntity1.setQuantity(request.getQuantity());
                    found = true;
                }
            }
            if (!found) {
                wallet.getAssetEntities().add(assetEntity);
            }
        }
        return WalletMapper.INSTANCE.toDto(walletRepository.save(wallet));
    }

    @Override
    public EvaluateDto evaluate(final EvaluateRequest request) {
        final int threadCount = Math.max(1, request.getAssets().size());
        final ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            List<CompletableFuture<AssetInfo>> futures = request.getAssets().stream()
                    .map(asset -> CompletableFuture.supplyAsync(
                            () -> coinCapService.getHistoricalInfo(AssetEnum.findBySymbol(asset.symbol()).getName(), request.getDate()), executor))
                    .toList();

            // Wait for all API calls to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // quantity per token
            final var quantities = getQuantities(request);

            // current values per token
            final var currentValues = getCurrentValues(request);

            // Build map of values at requested date to evaluate
            final Map<String, BigDecimal> valuesAtDate = new HashMap<>();
            for (CompletableFuture<AssetInfo> future : futures) {
                try {
                    AssetInfo info = future.get();
                    BigDecimal priceAtDate = info.priceUsd();
                    BigDecimal quantity = quantities.get(info.symbol());
                    valuesAtDate.put(info.symbol(), priceAtDate.multiply(quantity));
                } catch (InterruptedException | ExecutionException e) {
                    throw new BusinessException("Can not evaluate this wallet!");
                }
            }

            // Calculate total value
            final var totalValue = valuesAtDate.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calculate performance per token
            final var performanceMap = getPerformanceMap(currentValues, valuesAtDate);

            // Get best and worst
            final var bestAsset = getBest(performanceMap);

            final var worst = getWorst(performanceMap);

            // Build response
            return EvaluateDto.builder()
                    .total(totalValue)
                    .bestAsset(bestAsset != null ? bestAsset.getKey() : null)
                    .bestPerformance(bestAsset != null ? bestAsset.getValue() : null)
                    .worstAsset(worst != null ? worst.getKey() : null)
                    .worstPerformance(worst != null ? worst.getValue() : null)
                    .build();
        } finally {
            executor.shutdown();
        }
    }

    private static Map.Entry<String, BigDecimal> getWorst(Map<String, BigDecimal> performanceMap) {
        Map.Entry<String, BigDecimal> worst = performanceMap.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .orElse(null);
        return worst;
    }

    private static Map.Entry<String, BigDecimal> getBest(Map<String, BigDecimal> performanceMap) {
        Map.Entry<String, BigDecimal> best = performanceMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
        return best;
    }

    private static Map<String, BigDecimal> getPerformanceMap(Map<String, BigDecimal> currentValues, Map<String, BigDecimal> valuesAtDate) {
        Map<String, BigDecimal> performanceMap = new HashMap<>();
        for (String symbol : currentValues.keySet()) {
            BigDecimal oldValue = valuesAtDate.getOrDefault(symbol, BigDecimal.ZERO);
            BigDecimal currentValue = currentValues.getOrDefault(symbol, BigDecimal.ZERO);

            /*
             * in oder to calculate performance value should calculate based on this formula:
             * ((currentValue - oldValue)/oldValue) * 100
             **/
            BigDecimal performance = currentValue.subtract(oldValue)
                    .divide(oldValue, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            performanceMap.put(symbol, performance);
        }
        return performanceMap;
    }

    private static Map<String, BigDecimal> getCurrentValues(EvaluateRequest request) {
        return request.getAssets().stream()
                .collect(Collectors.toMap(r->AssetEnum.findBySymbol(r.symbol()).getName(), AssetInfoRequest::value, (a, b) -> b));
    }

    private static Map<String, BigDecimal> getQuantities(EvaluateRequest request) {
        return request.getAssets().stream()
                .collect(Collectors.toMap(r->AssetEnum.findBySymbol(r.symbol()).getName(), AssetInfoRequest::quantity, (a, b) -> b));
    }


    @Override
    public WalletInfoDTO showWalletInfo(final Long walletId, int size, int page) {
        final var wallet = walletRepository.findById(walletId);
        if (wallet.isEmpty()) {
            throw new BusinessException("Wallet not found!");
        }

        Pageable pageable = PageRequest.of(page, size);

        final var assetSummaries = assetRepository.findAssetsWithTotalValueByWalletId(wallet.get().getId(), pageable);
        var list = new ArrayList<AssetDto>();
        for (AssetSummary assetSummary : assetSummaries.toList()) {
            list.add(AssetDto.builder()
                    .price(assetSummary.getPrice())
                    .symbol(assetSummary.getSymbol())
                    .quantity(assetSummary.getQuantity())
                    .value(assetSummary.getPrice().multiply(assetSummary.getQuantity()))
                    .build());
        }

        return WalletInfoDTO.builder()
                .totalValue(assetSummaries.get().map(AssetSummary::getTotalValue).findFirst().orElse(BigDecimal.ZERO))
                .assets(list)
                .page(page)
                .size(size)
                .totalElements(assetSummaries.getTotalPages())
                .build();
    }

}
