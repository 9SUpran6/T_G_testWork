package com.example.test.demo.model.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TurnRequest {

    private UUID gameId;

    private int col;

    private int row;

}
