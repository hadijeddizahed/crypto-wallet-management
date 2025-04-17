package com.swisspost.cryptowalletmanagement.service.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.swisspost.cryptowalletmanagement.service.dto.HistoricalAssetResponse;
import com.swisspost.cryptowalletmanagement.service.dto.SingleAssetResponse;
import com.swisspost.cryptowalletmanagement.service.exceptions.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoinCapMapperTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CoinCapMapper coinCapMapper;

    private ObjectMapper realObjectMapper = new ObjectMapper();


    @Test
    void mapHistoricalInfo_singleObjectInArray_returnsHistoricalAssetResponse() {
        // Mock objects
        JsonNode jsonNode = realObjectMapper.createObjectNode();
        ArrayNode dataArray = realObjectMapper.createArrayNode();
        ObjectNode dataObject = realObjectMapper.createObjectNode();
        dataObject.put("priceUsd", "100.00");
        dataObject.put("date", "2023-10-01");
        dataArray.add(dataObject);
        ((ObjectNode) jsonNode).set("data", dataArray);

        HistoricalAssetResponse expectedResponse = new HistoricalAssetResponse(
                BigDecimal.valueOf(100.00),
                "bitcoin"

        );
        when(objectMapper.convertValue(dataArray.get(0), HistoricalAssetResponse.class))
                .thenReturn(expectedResponse);

        // Call the api
        HistoricalAssetResponse result = coinCapMapper.mapHistoricalResponse(jsonNode);

        // Assert
        assertEquals(expectedResponse, result);
        verify(objectMapper).convertValue(dataArray.get(0), HistoricalAssetResponse.class);
    }

    @Test
    void mapHistoricalResponse_multipleObjectsInArray_throwsIllegalArgumentException() {
        // Mock objects
        JsonNode jsonNode = realObjectMapper.createObjectNode();
        ArrayNode dataArray = realObjectMapper.createArrayNode();
        dataArray.add(realObjectMapper.createObjectNode());
        dataArray.add(realObjectMapper.createObjectNode());
        ((ObjectNode) jsonNode).set("data", dataArray);

        // Call Api and check
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> coinCapMapper.mapHistoricalResponse(jsonNode)
        );
        assertEquals("Expected a single object in array, but found 2", exception.getMessage());
        verifyNoInteractions(objectMapper);
    }

    @Test
    void mapHistoricalResponse_dataNotArray_returnsNull() {
        // Mock objects
        JsonNode jsonNode = realObjectMapper.createObjectNode();
        ((ObjectNode) jsonNode).set("data", realObjectMapper.createObjectNode());

        // Call the api
        HistoricalAssetResponse result = coinCapMapper.mapHistoricalResponse(jsonNode);

        // Assert
        assertNull(result);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void mapHistoricalResponse_noDataField_returnsNull() {
        // Mock objects
        JsonNode jsonNode = realObjectMapper.createObjectNode();

        // Call the api
        HistoricalAssetResponse result = coinCapMapper.mapHistoricalResponse(jsonNode);

        // Assert and check
        assertNull(result);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void mapHistoricalResponse_emptyArray_throwsIllegalArgumentException() {
        // Mock objects
        JsonNode jsonNode = realObjectMapper.createObjectNode();
        ArrayNode dataArray = realObjectMapper.createArrayNode();
        ((ObjectNode) jsonNode).set("data", dataArray);

        // Call Api and check
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> coinCapMapper.mapHistoricalResponse(jsonNode)
        );
        assertEquals("Expected a single object in array, but found 0", exception.getMessage());
        verifyNoInteractions(objectMapper);
    }

    @Test
    void mapSingleAssetInfo_validData_returnsSingleAssetResponse() {
        // Mock objects
        JsonNode jsonNode = realObjectMapper.createObjectNode();
        ObjectNode dataObject = realObjectMapper.createObjectNode();
        dataObject.put("id", "bitcoin");
        dataObject.put("priceUsd", "50000.00");
        ((ObjectNode) jsonNode).set("data", dataObject);

        SingleAssetResponse expectedResponse = new SingleAssetResponse(
                "",
                "bitcoin",
                "BTC",
                BigDecimal.valueOf(50000.00)

        );
        when(objectMapper.convertValue(dataObject, SingleAssetResponse.class))
                .thenReturn(expectedResponse);

        // Call the api
        SingleAssetResponse result = coinCapMapper.mapSingleAssetResponse(jsonNode);

        // Assert and check
        assertEquals(expectedResponse, result);
        verify(objectMapper).convertValue(dataObject, SingleAssetResponse.class);
    }

    @Test
    void mapSingleAssetResponse_noDataField_throwsBusinessException() {
        // Mock objects
        JsonNode jsonNode = realObjectMapper.createObjectNode();

        // Call Api and check
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> coinCapMapper.mapSingleAssetResponse(jsonNode)
        );
        assertEquals("Pricing service not responded", exception.getMessage());
        verifyNoInteractions(objectMapper);
    }

    @Test
    void mapSingleAssetResponse_nullDataField_throwsBusinessException() {
        JsonNode jsonNode = realObjectMapper.createObjectNode();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> coinCapMapper.mapSingleAssetResponse(jsonNode)
        );
        assertEquals("Pricing service not responded", exception.getMessage());
        verifyNoInteractions(objectMapper);
    }
}