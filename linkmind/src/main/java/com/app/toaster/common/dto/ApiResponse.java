package com.app.toaster.common.dto;

import com.app.toaster.exception.Error;
import com.app.toaster.exception.Success;

import lombok.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class ApiResponse<T> {

	private final int code;
	private final String message;
	private T data;


	public static ApiResponse success(Success success){
		return new ApiResponse<>(success.getHttpStatusCode(), success.getMessage());
	}

	public static <T> ApiResponse<T> success(Success success, T data){
		return new ApiResponse<T>(success.getHttpStatusCode(), success.getMessage(), data);
	}

	public static ApiResponse error(Error error){
		return new ApiResponse<>(error.getErrorCode(), error.getMessage());
	}

	public static ApiResponse error(Error error, String message){
		return new ApiResponse<>(error.getErrorCode(), message);
	}
}
