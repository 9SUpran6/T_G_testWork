package com.example.test.demo.service.interfaces;

import com.example.test.demo.model.dto.request.NewGameRequest;
import com.example.test.demo.model.dto.request.TurnRequest;
import com.example.test.demo.model.dto.response.GameResponse;

import java.util.UUID;

public interface GameService {

    GameResponse newGame(NewGameRequest newGameRequest);

    GameResponse turn(TurnRequest turnRequest);
}
