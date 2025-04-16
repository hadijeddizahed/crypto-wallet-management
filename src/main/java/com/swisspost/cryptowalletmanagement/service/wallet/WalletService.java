package com.swisspost.cryptowalletmanagement.service.wallet;

import com.swisspost.cryptowalletmanagement.api.dto.AssetRequest;
import com.swisspost.cryptowalletmanagement.api.dto.CreateWalletRequest;
import com.swisspost.cryptowalletmanagement.api.dto.EvaluateRequest;
import com.swisspost.cryptowalletmanagement.service.dto.EvaluateDto;
import com.swisspost.cryptowalletmanagement.service.dto.WalletInfoDTO;
import com.swisspost.cryptowalletmanagement.service.dto.WalletResponseDTO;

public interface WalletService {

    WalletResponseDTO create(final CreateWalletRequest request);

    WalletResponseDTO addAsset(final Long walletId, final AssetRequest request);

    EvaluateDto evaluate(final EvaluateRequest request);

    WalletInfoDTO showWalletInfo(final Long walletId, final int size, final int page);

}
