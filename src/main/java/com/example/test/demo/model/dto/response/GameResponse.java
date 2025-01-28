package com.example.test.demo.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GameResponse {

    private UUID gameId;

    private int width;

    private int height;

    private  int minesCount;

    private boolean completed;

    private boolean notFirstTurn;

    private int[][] field;

}
