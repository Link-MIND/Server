package com.app.toaster.category.controller.request;

import com.app.toaster.controller.valid.Severity;
import com.app.toaster.controller.valid.TitleValid;
import jakarta.validation.constraints.NotNull;

public record CreateCategoryDto(@TitleValid(payload = Severity.Error.class) @NotNull String categoryTitle) {
}
