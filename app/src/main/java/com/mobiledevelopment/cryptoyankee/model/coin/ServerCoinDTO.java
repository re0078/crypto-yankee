package com.mobiledevelopment.cryptoyankee.model.coin;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerCoinDTO {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("quote")
    private Quote quote;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Quote {
        @JsonProperty("USD")
        private USD usd;

        @AllArgsConstructor
        @NoArgsConstructor
        @Data
        public static class USD {
            @JsonProperty("price")
            private Float price;
            @JsonProperty("percent_change_1h")
            private Float hPrice;
            @JsonProperty("percent_change_24h")
            private Float dPrice;
            @JsonProperty("percent_change_7d")
            private Float wPrice;
        }
    }
}
