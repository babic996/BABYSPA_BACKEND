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
	private String status;
	private String message;
	private T data;
	private List<Map<String, String>> errors;
}
