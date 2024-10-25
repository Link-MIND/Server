package com.app.toaster.controller.response.main;

import com.app.toaster.category.controller.response.CategoryResponse;

import lombok.Builder;

import java.util.List;

@Builder
public record MainPageResponseDto(String nickname, int readToastNum, int allToastNum, List<CategoryResponse> mainCategoryListDto) {
}
