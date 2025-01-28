package com.example.test.demo.model.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewGameRequest {

    private int width;

    private int height;

    private  int minesCount;

}
