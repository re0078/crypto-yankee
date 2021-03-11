package com.mobiledevelopment.cryptoyankee.communication;

import android.content.res.Resources;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobiledevelopment.cryptoyankee.model.coin.ServerCoinDTO;
import com.mobiledevelopment.cryptoyankee.model.coin.ServerInfoResponse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiServiceTests {
    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void init() {
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    }

    @Test
    public void jsonParseTest() throws IOException {
        String sampleResponseJson = "{\n" +
                "    \"status\": {\n" +
                "        \"timestamp\": \"2021-03-11T18:34:26.778Z\",\n" +
                "        \"error_code\": 0,\n" +
                "        \"error_message\": null,\n" +
                "        \"elapsed\": 14,\n" +
                "        \"credit_count\": 1,\n" +
                "        \"notice\": null,\n" +
                "        \"total_count\": 4327\n" +
                "    },\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"id\": 1,\n" +
                "            \"name\": \"Bitcoin\",\n" +
                "            \"symbol\": \"BTC\",\n" +
                "            \"slug\": \"bitcoin\",\n" +
                "            \"num_market_pairs\": 9840,\n" +
                "            \"date_added\": \"2013-04-28T00:00:00.000Z\",\n" +
                "            \"tags\": [\n" +
                "                \"mineable\",\n" +
                "                \"pow\",\n" +
                "                \"sha-256\",\n" +
                "                \"store-of-value\",\n" +
                "                \"state-channels\",\n" +
                "                \"coinbase-ventures-portfolio\",\n" +
                "                \"three-arrows-capital-portfolio\",\n" +
                "                \"polychain-capital-portfolio\"\n" +
                "            ],\n" +
                "            \"max_supply\": 21000000,\n" +
                "            \"circulating_supply\": 18651062,\n" +
                "            \"total_supply\": 18651062,\n" +
                "            \"platform\": null,\n" +
                "            \"cmc_rank\": 1,\n" +
                "            \"last_updated\": \"2021-03-11T18:33:02.000Z\",\n" +
                "            \"quote\": {\n" +
                "                \"USD\": {\n" +
                "                    \"price\": 57092.7627636887,\n" +
                "                    \"volume_24h\": 56514271987.06758,\n" +
                "                    \"percent_change_1h\": 0.37080798,\n" +
                "                    \"percent_change_24h\": 0.25095305,\n" +
                "                    \"percent_change_7d\": 16.10544322,\n" +
                "                    \"percent_change_30d\": 23.09715997,\n" +
                "                    \"percent_change_60d\": 48.99015962,\n" +
                "                    \"percent_change_90d\": 218.30212052,\n" +
                "                    \"market_cap\": 1064840658056.8492,\n" +
                "                    \"last_updated\": \"2021-03-11T18:33:02.000Z\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        System.out.println(sampleResponseJson);
        ServerInfoResponse serverInfoResponse = objectMapper.reader().readValue(sampleResponseJson,
                ServerInfoResponse.class);
        assertEquals(1, serverInfoResponse.getServerCoinDTOS().size());
    }
}
