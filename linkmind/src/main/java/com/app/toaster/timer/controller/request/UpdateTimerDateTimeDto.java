package com.app.toaster.timer.controller.request;

import java.util.ArrayList;

public record UpdateTimerDateTimeDto(String remindTime, ArrayList<Integer> remindDates) {
}
