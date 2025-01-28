package com.example.test.demo.service;

import com.example.test.demo.model.dto.request.NewGameRequest;
import com.example.test.demo.model.dto.request.TurnRequest;
import com.example.test.demo.model.dto.response.GameResponse;
import lombok.RequiredArgsConstructor;
import com.example.test.demo.model.GameModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.test.demo.service.interfaces.GameService;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {


    private static final Map<UUID, GameModel> LOCAL_CACHE = new ConcurrentHashMap<>();

    @Override
    public GameResponse newGame(NewGameRequest gameRequest) {
        int[][] field = new int[gameRequest.getWidth()][gameRequest.getHeight()];
        boolean[][] openMap = new boolean[gameRequest.getWidth()][gameRequest.getHeight()];
        UUID gameCode = UUID.randomUUID();

        GameModel currentGame = GameModel.builder()
                .gameId(gameCode)
                .completed(false)
                .width(gameRequest.getWidth())
                .height(gameRequest.getHeight())
                .minesCount(gameRequest.getMinesCount())
                .field(field)
                .openMap(openMap)
                .notFirstTurn(false)
                .build();

        LOCAL_CACHE.put(gameCode, currentGame);

        return GameResponse.builder()
                .gameId(gameCode)
                .completed(false)
                .width(gameRequest.getWidth())
                .height(gameRequest.getHeight())
                .minesCount(gameRequest.getMinesCount())
                .notFirstTurn(false)
                .field(field)
                .build();
    }


    @Override
    public GameResponse turn(TurnRequest turnRequest) {
        log.info("Start turn with game number: {}", turnRequest.getGameId());
        GameModel currentGame = LOCAL_CACHE.get(turnRequest.getGameId());
        if (!currentGame.isNotFirstTurn()) {
            log.info("Turn is already first turn with game number: {}", turnRequest.getGameId());
            currentGame.setField(setLevel(currentGame, turnRequest));
            currentGame.setNotFirstTurn(true);
        }
        if(!openCell(currentGame.getField(), currentGame.getOpenMap(),turnRequest.getCol(),turnRequest.getRow())){
            return GameResponse.builder()
                    .gameId(currentGame.getGameId())
                    .completed(true)
                    .field(currentGame.getField())
                    .build();
        };
        currentGame.setCompleted(finishGame(currentGame));
        log.info("Finished turn with game number: {}", turnRequest.getGameId());
        return GameResponse.builder()
                .gameId(currentGame.getGameId())
                .completed(currentGame.isCompleted())
                .field(currentGame.getField())
                .build();
    }

    private int[][] setLevel(GameModel currentGame, TurnRequest turnRequest) {
        log.info("Start setting level with game number: {}", turnRequest.getGameId());
        Random random = new Random();
        int width = currentGame.getWidth();
        int height = currentGame.getHeight();
        int minesCount = currentGame.getMinesCount();
        int col = turnRequest.getCol();
        int row = turnRequest.getRow();
        int[][] field = currentGame.getField();
        field[col][row] = -2;
        while (minesCount != 0) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            if (field[x][y] != -1 && field[x][y] != -2) {
                field[x][y] = -1;
                minesCount--;
            }
        }
        field[col][row] = 0;
        log.info("All mines are set on level with game number: {}", turnRequest.getGameId());
        return calculateCells(field, width, height);
    }

    private int[][] calculateCells(int[][] field, int width, int height) {
        log.info("Start calculating cells with game number");
        int[] xAxis = {1, 1, 1, 0, -1, -1, -1, 0};//Здесь указаны локальные смещения по осям относительно текущей клетки.
        int[] yAxis = {1, 0, -1, -1, -1, 0, 1, 1};//Координаты текущей клетки (0,0), справа по диагонали от нее (1,1) и т.д.
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (field[x][y] != -1) continue;
                for (int i = 0; i < 8; i++) {//Обходим все 8 соседей от клетки
                    int currentX = x + xAxis[i];
                    int currentY = y + yAxis[i];
                    if (currentX >= 0 && currentX < width && currentY >= 0 && currentY < height) {
                        if (field[currentX][currentY] != -1) {
                            field[currentX][currentY]++;
                        }
                    }
                }
            }
        }
        log.info("Finish calculating cells");
        return field;
    }

    private boolean openCell(int[][] field, boolean[][] openMap, int col, int row) {
        log.info("Start opening cells");
        if (openMap[col][row]) return true;
        if (field[col][row] == -1) {
            log.info("Mine is touched");
            return false;
        }
        openMap[col][row] = true;
        if (field[col][row] == 0) {
            int[] xAxis = {1, 1, 1, 0, -1, -1, -1, 0};
            int[] yAxis = {1, 0, -1, -1, -1, 0, 1, 1};
            for(int i = 0; i < 8; i++){
                openCell(field, openMap, col+xAxis[i], row+yAxis[i]);
            }
        }
        return true;
    }

    private boolean finishGame(GameModel currentGame){
        log.info("Start finishing game");
        boolean[][] openMap = currentGame.getOpenMap();
        int[][] field = currentGame.getField();
        int minesCount = currentGame.getMinesCount();
        int openedCells = Arrays.stream(openMap)
                .flatMapToInt(row -> IntStream.range(0, row.length).map(i -> row[i] ? 1 : 0))
                .sum();
        return openedCells == (field.length *field[0].length -minesCount);
    }

}