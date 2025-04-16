package com.swisspost.cryptowalletmanagement.service.dto;


import com.swisspost.cryptowalletmanagement.service.exceptions.BusinessException;

public enum AssetEnum {
    BTC("bitcoin"),
    ETH("ethereum"),
    BNB("binance coin"),
    XRP("ripple"),
    ADA("cardano"),
    SOL("solana"),
    DOT("polkadot"),
    DOGE("dogecoin"),
    AVAX("avalanche"),
    SHIB("shiba inu"),
    MATIC("polygon"),
    LINK("chainlink"),
    TRX("tron"),
    ALGO("algorand"),
    XLM("stellar"),
    ATOM("cosmos"),
    VET("vechain"),
    MANA("decentraland"),
    SAND("the sandbox"),
    UNI("uniswap");

    private final String name;

    AssetEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static AssetEnum findBySymbol(String symbol) {
        for (AssetEnum assetEnum : values()) {
            if (assetEnum.name().equals(symbol)) {
                return assetEnum;
            }
        }
        throw new BusinessException("No Asset found with symbol: " + symbol);
    }
}