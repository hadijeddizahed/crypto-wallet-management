package com.swisspost.cryptowalletmanagement.service.user;

import com.swisspost.cryptowalletmanagement.service.pricing.PricingApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PricingApiService pricingApiService;

}
