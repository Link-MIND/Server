package com.app.toaster.category.controller.request;

import jakarta.validation.constraints.NotNull;

public record ChangeCateoryPriorityDto(
        @NotNull
        Long categoryId,
        @NotNull
        int newPriority) {
}
