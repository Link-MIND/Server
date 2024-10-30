package com.app.toaster.category.controller.request;

import com.app.toaster.utils.valid.Severity;
import com.app.toaster.utils.valid.TitleValid;
import jakarta.validation.constraints.NotNull;

public record CreateCategoryDto(@TitleValid(payload = Severity.Error.class) @NotNull String categoryTitle) {
}
