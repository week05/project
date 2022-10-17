package com.example.intermediate.controller.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RestApiException {
    private String field;
    private String errorMessage;

}
