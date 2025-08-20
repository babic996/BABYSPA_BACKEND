package com.backend.babyspa.v1.utils;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private String message;
    private T data;
    private List<Map<String, String>> errors;


    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(null, data, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message, null, null);
    }

    public static <T> ApiResponse<T> error(String message, List<Map<String, String>> errors) {
        return new ApiResponse<>(message, null, errors);
    }
}
