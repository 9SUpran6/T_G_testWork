package com.example.test.demo.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GameModel {

    private UUID gameId;

    private int width;

    private int height;

    private int minesCount;

    private boolean completed;

    private boolean notFirstTurn;

    private String[][] openedField;

    private String[][] field;

}
