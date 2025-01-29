package com.example.test.demo.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewGameRequest {

    @Min(2)@Max(30)
    private int width;

    @Min(2)@Max(30)
    private int height;

    @Min(1)
    @JsonProperty("mines_count")
    private  int minesCount;

}
