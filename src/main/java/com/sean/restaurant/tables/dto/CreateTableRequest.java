package com.sean.restaurant.tables.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CreateTableRequest {
    @NotBlank public String label;
    @Min(1)  public int capacity;
}
