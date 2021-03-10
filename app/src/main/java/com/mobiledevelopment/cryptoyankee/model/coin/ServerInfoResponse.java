package com.mobiledevelopment.cryptoyankee.model.coin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerInfoResponse {
    @JsonProperty("data")
    private List<ServerCoinDTO> serverCoinDTOS;
}
