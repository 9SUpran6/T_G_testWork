package com.example.test.demo.model.dto.request;

import lombok.Data;

@Data
public class NewGameRequest {

    private int width;

    private int height;

    private  int minesCount;

}
