package com.app.toaster.timer.controller.response;

import lombok.Builder;

import java.util.List;

@Builder
public record GetTimerPageResponseDto(List<CompletedTimerDto> completedTimerList, List<WaitingTimerDto> waitingTimerList) {
}
