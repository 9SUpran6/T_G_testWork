package com.example.test.demo.service;

import com.example.test.demo.model.dto.request.NewGameRequest;
import com.example.test.demo.model.dto.request.TurnRequest;
import com.example.test.demo.model.dto.response.GameResponse;
import com.example.test.demo.model.GameModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.test.demo.service.interfaces.GameService;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static com.example.test.demo.service.CacheServiceImpl.LOCAL_CACHE;

@Slf4j
@Service
public class GameServiceImpl implements GameService {

    @Override
    public GameResponse newGame(NewGameRequest gameRequest) {
        log.info("Start new game");
        if (gameRequest.getMinesCount() >= (gameRequest.getWidth() * gameRequest.getHeight())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Количество мин должно быть меньше длина*высота -1");
        }
        String[][] field = new String[gameRequest.getHeight()][gameRequest.getWidth()];
        String[][] openMap = new String[gameRequest.getHeight()][gameRequest.getWidth()];
        UUID gameCode = UUID.randomUUID();

        GameModel currentGame = GameModel.builder()
                .gameId(gameCode)
                .completed(false)
                .width(gameRequest.getWidth())
                .height(gameRequest.getHeight())
                .minesCount(gameRequest.getMinesCount())
                .field(field)
                .openedField(openMap)
                .notFirstTurn(false)
                .build();

        LOCAL_CACHE.put(gameCode, currentGame);
        log.info("new game with gameId {} created", gameCode);
        return GameResponse.builder()
                .gameId(gameCode)
                .width(gameRequest.getWidth())
                .height(gameRequest.getHeight())
                .minesCount(gameRequest.getMinesCount())
                .completed(false)
                .field(field)
                .build();
    }


    @Override
    public GameResponse turn(TurnRequest turnRequest) {
        log.info("Start turn with gameId: {}", turnRequest.getGameId());
        GameModel currentGame = LOCAL_CACHE.get(turnRequest.getGameId());

        if (currentGame.isCompleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Игра уже завершена, ход невозможен");
        }

        if (!currentGame.isNotFirstTurn()) {
            log.info("Turn is already first turn with gameId: {}", turnRequest.getGameId());
            currentGame.setField(setLevel(currentGame, turnRequest));
            currentGame.setNotFirstTurn(true);
        }

        if (minesTouch(currentGame.getField(), turnRequest)) {
            log.info("Mine is touched");
            return GameResponse.builder()
                    .gameId(currentGame.getGameId())
                    .width(currentGame.getWidth())
                    .height(currentGame.getHeight())
                    .minesCount(currentGame.getMinesCount())
                    .completed(true)
                    .field(currentGame.getField())
                    .build();
        }

        currentGame.setOpenedField(openCell(currentGame.getField(),currentGame.getOpenedField(), turnRequest.getCol(), turnRequest.getRow()));

        if (finishGame(currentGame)) {
            currentGame.setCompleted(true);
            currentGame.setOpenedField(finishField(currentGame.getField()));
        }

        log.info("Finished turn with gameId: {}", turnRequest.getGameId());
        return GameResponse.builder()
                .gameId(currentGame.getGameId())
                .width(currentGame.getWidth())
                .height(currentGame.getHeight())
                .minesCount(currentGame.getMinesCount())
                .completed(currentGame.isCompleted())
                .field(currentGame.getOpenedField())
                .build();
    }

    private String[][] setLevel(GameModel currentGame, TurnRequest turnRequest) {
        log.info("Start setting level with gameId: {}", turnRequest.getGameId());
        Random random = new Random();
        int width = currentGame.getWidth();
        int height = currentGame.getHeight();
        int minesCount = currentGame.getMinesCount();
        int row = turnRequest.getRow();
        int col = turnRequest.getCol();
        String[][] field = currentGame.getField();
        field[row][col] = "S";
        while (minesCount != 0) {
            int x = random.nextInt(height);
            int y = random.nextInt(width);
            if (!"X".equals(field[x][y]) && !"S".equals(field[x][y])) {
                field[x][y] = "X";
                minesCount--;
            }
        }
        field[row][col] = null;
        log.info("All mines are set on level with gameId: {}", turnRequest.getGameId());
        return calculateCells(field, width, height);
    }

    private String[][] calculateCells(String[][] field, int width, int height) {
        log.info("Start calculating cells");
        int[] yAxis = {1, 0, -1, -1, -1, 0, 1, 1};//Здесь указаны локальные смещения по осям относительно текущей клетки.
        int[] xAxis = {1, 1, 1, 0, -1, -1, -1, 0};//Координаты текущей клетки (0,0), справа по диагонали от нее (1,1) и т.д.
        int currentRow, currentCol;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if ("X".equals(field[row][col])) {
                    for (int i = 0; i < 8; i++) {//Обходим все 8 соседей от клетки
                        currentRow = row + yAxis[i];
                        currentCol = col + xAxis[i];
                        if (currentRow >= 0 && currentRow < height &&
                            currentCol >= 0 && currentCol < width &&
                            !"X".equals(field[currentRow][currentCol])) {

                            if (field[currentRow][currentCol] == null) {
                                field[currentRow][currentCol] = "1";
                            } else {
                                field[currentRow][currentCol] = String.valueOf(Integer.parseInt(field[currentRow][currentCol]) + 1);
                            }
                        }
                    }
                }
            }
        }
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (field[row][col] == null) {
                    field[row][col] = "0";
                }
            }
        }
        log.info("Finish calculating cells");
        return field;
    }

    private String[][] openCell(String[][] field, String[][] openedField, int col, int row) {
        if (openedField[row][col] != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Эта ячейка уже открыта");

        openedField[row][col] = field[row][col];

        if ("0".equals(field[row][col])) {
            int[] yAxis = {1, 0, -1, -1, -1, 0, 1, 1};
            int[] xAxis = {1, 1, 1, 0, -1, -1, -1, 0};
            int newCol, newRow;
            for (int i = 0; i < 8; i++) {
                newCol = col + xAxis[i];
                newRow = row + yAxis[i];
                if (newCol >= 0 && newCol < field[0].length && newRow >= 0 && newRow < field.length) {
                    if (openedField[newRow][newCol] == null) {
                        openCell(field, openedField, newCol, newRow);
                    }
                }
            }
        }
        return openedField;
    }

    private boolean minesTouch(String[][] field, TurnRequest turnRequest){
        return "X".equals(field[turnRequest.getRow()][turnRequest.getCol()]);
    }

    private boolean finishGame(GameModel currentGame) {
        log.info("Start finishing game");
        String[][] openMap = currentGame.getOpenedField();
        long notOpenCells = Arrays.stream(openMap)
                .flatMap(Arrays::stream)
                .filter(Objects::isNull)
                .count();
        return notOpenCells == currentGame.getMinesCount();
    }

    private String[][] finishField(String[][] field) {
        return Arrays.stream(field)
                .map(row -> Arrays.stream(row)
                        .map(x -> "X".equals(x) ? "M" : x)
                        .toArray(String[]::new))
                .toArray(String[][]::new);
    }

}
