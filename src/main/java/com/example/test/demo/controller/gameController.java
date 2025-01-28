package com.example.test.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import com.example.test.demo.service.interfaces.GameService;

@RestController
@RequiredArgsConstructor
public class gameController {

    private final GameService gameService;
}
