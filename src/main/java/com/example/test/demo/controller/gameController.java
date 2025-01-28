package com.example.test.demo.controller;

import com.example.test.demo.model.dto.request.NewGameRequest;
import com.example.test.demo.model.dto.request.TurnRequest;
import com.example.test.demo.model.dto.response.GameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.test.demo.service.interfaces.GameService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class gameController {

    private final GameService gameService;

    @PostMapping("/new")
    public ResponseEntity<GameResponse> newGame(@RequestBody NewGameRequest newGameRequest) {
        return new ResponseEntity<>(gameService.newGame(newGameRequest), HttpStatus.OK);
    }

    @PostMapping("/turn")
    public ResponseEntity<GameResponse> turn(@RequestBody TurnRequest turnRequest) {
        return new ResponseEntity<>(gameService.turn(turnRequest),HttpStatus.OK);
    }

}
