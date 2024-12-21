package com.app.toaster.admin.common;

import com.app.toaster.common.dto.ApiResponse;
import com.app.toaster.exception.Success;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RedirectResponse<T> extends ApiResponse<T> {
    private final int code;
    private final String redirectUrl;
    private T data;


    public static <T> RedirectResponse<T> success(Success success, String redirectUrl, T data){
        return new RedirectResponse<>(success.getHttpStatusCode(), redirectUrl, data);
    }
}
