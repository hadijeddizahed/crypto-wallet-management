package com.swisspost.cryptowalletmanagement.api;

import com.swisspost.cryptowalletmanagement.api.dto.AssetRequest;
import com.swisspost.cryptowalletmanagement.api.dto.CreateWalletRequest;
import com.swisspost.cryptowalletmanagement.api.dto.EvaluateRequest;
import com.swisspost.cryptowalletmanagement.service.dto.EvaluateDto;
import com.swisspost.cryptowalletmanagement.service.dto.WalletResponseDTO;
import com.swisspost.cryptowalletmanagement.service.wallet.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping()
    public ResponseEntity<WalletResponseDTO> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        return ResponseEntity.ok(walletService.create(request));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<?> showWalletInfo(@PathVariable final Long walletId,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(walletService.showWalletInfo(walletId, size,page));
    }

    @PostMapping("/{walletId}/assets")
    public ResponseEntity<WalletResponseDTO> addAsset(@PathVariable(name = "walletId") final Long walletId,
                                                      @Valid @RequestBody AssetRequest assetRequest){

        return ResponseEntity.ok(walletService.addAsset(walletId, assetRequest));
    }

    @PostMapping("/evaluate")
    public ResponseEntity<EvaluateDto> evaluate(@Valid @RequestBody final EvaluateRequest evaluateRequest){
        return ResponseEntity.ok(walletService.evaluate(evaluateRequest));
    }
}
