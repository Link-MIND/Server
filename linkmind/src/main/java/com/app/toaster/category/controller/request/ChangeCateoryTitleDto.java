package com.app.toaster.category.controller.request;

import com.app.toaster.utils.valid.Severity;
import com.app.toaster.utils.valid.TitleValid;
import jakarta.validation.constraints.NotNull;

public record ChangeCateoryTitleDto(
        @NotNull
        Long categoryId,
        @TitleValid(payload = Severity.Error.class) @NotNull String newTitle) {
}
