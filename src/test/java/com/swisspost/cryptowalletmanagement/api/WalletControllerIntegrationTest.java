package com.swisspost.cryptowalletmanagement.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swisspost.cryptowalletmanagement.api.dto.AssetRequest;
import com.swisspost.cryptowalletmanagement.api.dto.CreateWalletRequest;
import com.swisspost.cryptowalletmanagement.api.dto.EvaluateRequest;
import com.swisspost.cryptowalletmanagement.service.dto.AssetDto;
import com.swisspost.cryptowalletmanagement.service.dto.EvaluateDto;
import com.swisspost.cryptowalletmanagement.service.dto.WalletInfoDTO;
import com.swisspost.cryptowalletmanagement.service.dto.WalletResponseDTO;
import com.swisspost.cryptowalletmanagement.service.exceptions.BusinessException;
import com.swisspost.cryptowalletmanagement.service.wallet.WalletService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletControllerIntegrationTest {
    private static final String BASE_URL = "/api/v1/wallets";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WalletService walletService;

    @Test
    void shouldCreateWallet_WhenWalletRequestIsValid() throws Exception {
        CreateWalletRequest request = new CreateWalletRequest();
        request.setEmail("test@gmail.com");
        // set fields as needed

        WalletResponseDTO responseDTO = WalletResponseDTO.builder()
                .email("test@gmail.com")
                .build();
        // set response fields

        Mockito.when(walletService.create(any(CreateWalletRequest.class))).thenReturn(responseDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@gmail.com"));
    }

    @Test
    void shouldReturnBadRequest_WhenWalletRequestIsNotValid() throws Exception {
        CreateWalletRequest request = new CreateWalletRequest();
        request.setEmail(null);

        Mockito.when(walletService.create(any(CreateWalletRequest.class))).thenReturn(null);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnWalletInformation_whenCallWithCorrectData() throws Exception {
        Long walletId = 1L;
        WalletInfoDTO res = WalletInfoDTO.builder()
                .totalValue(BigDecimal.valueOf(1200))
                .assets(new ArrayList<>())
                .build();

        Mockito.when(walletService.showWalletInfo(walletId, 20, 0)).thenReturn(res);

        var response = mockMvc.perform(get(BASE_URL + "/{walletId}", walletId)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        final var walletInfoDTO = this.objectMapper.readValue(response, WalletInfoDTO.class);
        assertThat(walletInfoDTO.assets()).isNotNull();

    }

    @Test
    void shouldAddAsset_whenCallApiWithCorrectData() throws Exception {
        Long walletId = 1L;
        AssetRequest assetRequest = new AssetRequest();
        assetRequest.setQuantity(BigDecimal.valueOf(12));
        assetRequest.setSymbol("BTC");

        WalletResponseDTO responseDTO = WalletResponseDTO.builder()
                .assetsInfo(List.of(AssetDto.builder()
                        .quantity(BigDecimal.valueOf(12))
                        .symbol("BTC")
                        .build()))
                .build();

        Mockito.when(walletService.addAsset(eq(walletId), any(AssetRequest.class))).thenReturn(responseDTO);

        final var response = mockMvc.perform(post(BASE_URL + "/{walletId}/assets", walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assetRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        final var walletResponseDTO = this.objectMapper.readValue(response, WalletResponseDTO.class);
        assertThat(walletResponseDTO.assetsInfo()).isNotNull();
        assertThat(walletResponseDTO.assetsInfo().get(0).quantity()).isEqualTo(BigDecimal.valueOf(12));
    }

    @Test
    void shouldReturnBadRequest_whenCallApiWithWrongData() throws Exception {
        Long walletId = 1L;
        AssetRequest assetRequest = new AssetRequest();
        assetRequest.setQuantity(BigDecimal.valueOf(12));
        assetRequest.setSymbol("WRONS-SYMBOL");

        Mockito.when(walletService.addAsset(eq(walletId), any(AssetRequest.class))).thenThrow(BusinessException.class);

        mockMvc.perform(post(BASE_URL + "/{walletId}/assets", walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assetRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldEvaluate_whenCallApiWithCorrectData() throws Exception {
        EvaluateRequest evaluateRequest = new EvaluateRequest();
        evaluateRequest.setDate(LocalDate.now());
        evaluateRequest.setAssets(List.of());

        EvaluateDto evaluateDto = EvaluateDto.builder().build();

        Mockito.when(walletService.evaluate(any(EvaluateRequest.class))).thenReturn(evaluateDto);

        mockMvc.perform(post(BASE_URL + "/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evaluateRequest)))
                .andExpect(status().isOk());
    }
}
