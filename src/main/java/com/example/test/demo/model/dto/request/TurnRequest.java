package com.example.test.demo.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TurnRequest {

    @JsonProperty("game_id")
    private UUID gameId;

    private int col;

    private int row;

}
