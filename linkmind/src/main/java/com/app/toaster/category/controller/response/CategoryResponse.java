package com.app.toaster.category.controller.response;

import lombok.Builder;

@Builder
public record CategoryResponse(
        Long categoryId,
        String categoryTitle,
        int toastNum) {

}
