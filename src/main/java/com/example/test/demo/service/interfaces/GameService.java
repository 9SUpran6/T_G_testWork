package com.example.test.demo.service.interfaces;

import com.example.test.demo.model.dto.request.NewGameRequest;
import com.example.test.demo.model.dto.request.TurnRequest;
import com.example.test.demo.model.dto.response.GameResponse;

public interface GameService {

    /**
     * Определенно в угоду читаемости код в реализации сервиса можно было написать короче
     * и попробовать его отрефачить. Но в первую очередь попытался сделать оперативно
     * рабочее решение
     */

    GameResponse newGame(NewGameRequest newGameRequest);

    GameResponse turn(TurnRequest turnRequest);

}
