package com.example.test.demo.service;

import com.example.test.demo.model.dto.request.NewGameRequest;
import com.example.test.demo.model.dto.request.TurnRequest;
import com.example.test.demo.model.dto.response.GameResponse;
import lombok.RequiredArgsConstructor;
import com.example.test.demo.model.GameModel;
import org.springframework.stereotype.Service;
import com.example.test.demo.service.interfaces.GameService;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private static final Map<UUID, GameModel> LOCAL_CACHE = new ConcurrentHashMap<>();

    @Override
    public GameResponse newGame(NewGameRequest gameRequest) {
        int[][] field = new int[gameRequest.getWidth()][gameRequest.getHeight()];
        UUID gameCode = UUID.randomUUID();

        GameModel currentGame = GameModel.builder()
                .completed(false)
                .width(gameRequest.getWidth())
                .height(gameRequest.getHeight())
                .minesCount(gameRequest.getMinesCount())
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
    public int[][] turn(UUID gameCode, TurnRequest turnRequest) {
        GameModel currentGame = LOCAL_CACHE.get(gameCode);
        if (!currentGame.isNotFirstTurn()) {
            currentGame.setField(setLevel(currentGame.getWidth(),
                                          currentGame.getHeight(),
                                          currentGame.getMinesCount(),
                                          turnRequest));
            currentGame.setNotFirstTurn(true);
        }

        return new int[0][];
    }

    /**
     * Каждая игра начинается с указания размера поля width и height, а также количества мин mines_count на нём.
     * Исходная задача не подразумевает ограничений, но для тестовой реализации остановимся на разумном ограничении входных параметров:
     * ширина и высота не более 30, количество мин не более width * height - 1 (всегда должна быть хотя бы одна свободная ячейка).
     * <p>
     * Далее игроку предлагается в созданной игре (идентификация игры по полученному в ответ game_id)
     * делать ходы, указывая координаты ячейки, которую игрок хочет открыть, а именно - row (номер ряда, начиная с нуля)
     * и col (номер колонки, начиная с нуля).
     * <p>
     * В ответ на оба метода приходят данные о самой игре: уникальный идентификатор игры game_id, размер поля и количество мин,
     * указанные при создании игры,
     * а также данные о поле field в виде двумерного массива символов (height строк, в каждой по width элементов),
     * где пустые строки " " (пробелы) означают неоткрытые ячейки поля, поля с цифрами от "0" до "8" означают открытые ячейки,
     * где цифры указывают, сколько мин расположено в непосредственной близости от текущей ячейки. Также возвращается параметр completed,
     * указывающий, завершена ли текущая игра.
     * <p>
     * Игра заканчивается в случае, если пользователь указал на ячейку, где установлена мина (ячейки с минами при этом отмечены символом
     * "X" - латинская заглавная "икс"), либо пользователь открыл все ячейки, не занятые минами (в этом случае мины отмечены
     * "M" - латинская заглавная "эм"). Также при завершении игры должна появиться информация
     * по всем остальным ячейкам - количество мин рядом с каждой из ячеек.
     * <p>
     * Если в процессе игры пользователь открывает ячейку, рядом с которой нет ни одной мины (то есть ячейка со значением "0"),
     * должны "открыться" все смежные ячейки, рядом с которыми также нет ни одной мины, а также все смежные с ними "числовые" ячейки,
     * рядом с которыми мины есть, с указанием их количества.
     * <p>
     * Не допускается после завершения игры делать новые ходы, а также повторно проверять уже проверенную ячейку.
     * Эти, а также иные ошибочные ситуации должны возвращать ошибку с кодом 400 Bad Request с текстовым описание ошибки в error.
     **/

    private int[][] setLevel(int width, int height, int minesCount, TurnRequest turnRequest) {
        Random random = new Random();
        int placementMines = 0;
        int[][] field = new int[width][height];
        field[turnRequest.getCol()][turnRequest.getRow()] = -2;
        while (placementMines < minesCount) {
            int x = random.nextInt(2, width);
            int y = random.nextInt(2, height);
            if (field[x][y] != -1 && field[x][y] != -2) {
                field[x][y] = -1;
                placementMines++;
            }
        }
        field[turnRequest.getCol()][turnRequest.getRow()] = 0;
        return calculateCells(field, width, height);
    }

    private int[][] calculateCells(int[][] field, int width, int height) {
        int[] xAxis = {1, 1, 1, 0, -1, -1, -1, 0};//Здесь указаны локальные смещения по осям относительно текущей клетки.
        int[] yAxis = {1, 0, -1, -1, -1, 0, 1, 1};//Координаты текущей клетки (0,0), справа по диагонали от нее (1,1) и т.д.
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (field[x][y] != -1) {
                    break;
                }
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
        return field;
    }
}