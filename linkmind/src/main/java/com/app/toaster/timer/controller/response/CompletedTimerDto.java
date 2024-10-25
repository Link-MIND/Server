package com.app.toaster.timer.controller.response;

import com.app.toaster.timer.domain.Reminder;

public record CompletedTimerDto(Long timerId, Long categoryId, String remindTime, String remindDate, String comment) {
    public static CompletedTimerDto of(Reminder timer,String remindTime, String remindDate){
        if(timer.getCategory() == null)
            return new CompletedTimerDto(timer.getId(), 0L, remindTime, remindDate, "전체");
        return new CompletedTimerDto(timer.getId(), timer.getCategory().getCategoryId(), remindTime, remindDate, timer.getComment() );
    }
}
