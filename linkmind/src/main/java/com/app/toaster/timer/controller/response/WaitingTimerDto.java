package com.app.toaster.timer.controller.response;

import com.app.toaster.timer.domain.Reminder;

import java.time.LocalDateTime;

public record WaitingTimerDto(Long timerId, String remindTime, String remindDates, Boolean isAlarm, LocalDateTime updateAt, String comment, Long categoryId) {
    public static WaitingTimerDto of(Reminder timer, String remindTime, String remindDates) {
        if(timer.getCategory() == null)
            return new WaitingTimerDto(timer.getId(), remindTime, remindDates, timer.getIsAlarm(), timer.getUpdateAt(), "전체", 0L);
        return new WaitingTimerDto(timer.getId(), remindTime, remindDates, timer.getIsAlarm(), timer.getUpdateAt(), timer.getComment(), timer.getCategory().getCategoryId());
    }
}
