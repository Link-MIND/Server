package com.app.toaster.category.controller.request;

import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;

public record DeleteCategoryDto(@NotNull ArrayList<Long> deleteCategoryList) {
}
