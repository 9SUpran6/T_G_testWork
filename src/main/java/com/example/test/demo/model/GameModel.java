package com.example.test.demo.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class GameModel {

    private UUID gameId;

    private int width;

    private int height;

    private int minesCount;

    private boolean completed;

    private boolean notFirstTurn;

    private int[][] field;

}
