package com.example.test.demo.model.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class TurnRequest {

    private UUID gameId;

    private int col;

    private int row;

}
