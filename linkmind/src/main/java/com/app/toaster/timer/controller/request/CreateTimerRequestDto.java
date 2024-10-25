package com.app.toaster.timer.controller.request;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


public record CreateTimerRequestDto(
     Long categoryId,
     String remindTime,
     ArrayList<Integer> remindDates){
}
