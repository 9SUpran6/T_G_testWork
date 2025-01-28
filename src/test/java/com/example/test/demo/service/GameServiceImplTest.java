package com.example.test.demo.service;

import com.example.test.demo.model.GameModel;
import com.example.test.demo.model.dto.request.NewGameRequest;
import com.example.test.demo.model.dto.request.TurnRequest;
import com.example.test.demo.service.interfaces.GameService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceImplTest {

    private GameService gameService = new GameServiceImpl();

    private NewGameRequest newGameRequest;
    private TurnRequest turnRequest;

    private GameModel gameModel;

    private static final UUID gameId = UUID.randomUUID();
    private static final int width = 10;
    private static final int height = 10;
    private static final int minesCount = 10;
    private static final boolean completed = false;
    private static final boolean notFirstTurn = true;
    private static final int col = 5;
    private static final int row = 5;
    private static final int[][] field = new int[width][height];

    private static final Map<UUID, GameModel> LOCAL_CACHE = new ConcurrentHashMap<>();

    @BeforeEach
    void setUpEach() {
        newGameRequest = NewGameRequest.builder()
                .width(width)
                .height(height)
                .minesCount(minesCount)
                .build();

        turnRequest = TurnRequest.builder()
                .gameId(gameId)
                .col(col)
                .row(row)
                .build();

        gameModel = GameModel.builder()
                .completed(false)
                .width(newGameRequest.getWidth())
                .height(newGameRequest.getHeight())
                .minesCount(newGameRequest.getMinesCount())
                .notFirstTurn(false)
                .build();

        LOCAL_CACHE.put(gameId, gameModel);
    }
}