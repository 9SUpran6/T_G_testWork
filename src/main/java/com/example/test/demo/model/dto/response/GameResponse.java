package com.example.test.demo.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GameResponse {

    @JsonProperty("game_id")
    private UUID gameId;

    private int width;

    private int height;

    @JsonProperty("mines_count")
    private  int minesCount;

    private boolean completed;

    private String[][] field;

}
