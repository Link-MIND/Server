package com.app.toaster.controller;

import com.app.toaster.common.dto.ApiResponse;
import com.app.toaster.config.UserId;
import com.app.toaster.controller.request.timer.CreateTimerRequestDto;
import com.app.toaster.controller.request.timer.UpdateTimerCommentDto;
import com.app.toaster.controller.request.timer.UpdateTimerDateTimeDto;
import com.app.toaster.controller.response.timer.GetTimerResponseDto;
import com.app.toaster.exception.Error;
import com.app.toaster.exception.Success;
import com.app.toaster.exception.model.CustomException;
import com.app.toaster.service.timer.TimerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/timer")
public class TimerController {

    private final TimerService timerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse createTimer(
            @UserId Long userId,
            @RequestBody CreateTimerRequestDto createTimerRequestDto
            ){

        timerService.createTimer(userId, createTimerRequestDto);
        return ApiResponse.success(Success.CREATE_TIMER_SUCCESS);
    }

    @GetMapping("/{timerId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<GetTimerResponseDto> getTimer(
            @UserId Long userId,
            @PathVariable Long timerId) {

        return ApiResponse.success(Success.GET_TIMER_SUCCESS,timerService.getTimer(userId, timerId) );
    }

    @PatchMapping("/datetime/{timerId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse updateTimerDatetime(
            @UserId Long userId,
            @PathVariable Long timerId,
            @RequestBody UpdateTimerDateTimeDto updateTimerDateTimeDto){

        timerService.updateTimerDatetime(userId,timerId, updateTimerDateTimeDto);
        return ApiResponse.success(Success.UPDATE_TIMER_DATETIME_SUCCESS);
    }

    @PatchMapping("/comment/{timerId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse updateTimerComment(
            @UserId Long userId,
            @PathVariable Long timerId,
            @RequestBody UpdateTimerCommentDto updateTimerCommentDto){

        timerService.updateTimerComment(userId,timerId, updateTimerCommentDto);
        return ApiResponse.success(Success.UPDATE_TIMER_COMMENT_SUCCESS);
    }

    @DeleteMapping("/{timerId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse deleteTimer(
            @UserId Long userId,
            @PathVariable Long timerId){
        timerService.deleteTimer(userId,timerId);
        return ApiResponse.success(Success.DELETE_TIMER_SUCCESS);
    }
    @GetMapping("/main")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getTimerPage(
            @UserId Long userId) throws IOException {
        return ApiResponse.success(Success.GET_TIMER_PAGE_SUCCESS, timerService.getTimerPage(userId));
    }
}
