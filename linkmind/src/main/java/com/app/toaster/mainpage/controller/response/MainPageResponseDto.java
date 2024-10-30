package com.app.toaster.mainpage.controller.response;

import com.app.toaster.category.controller.response.CategoryResponse;

import lombok.Builder;

import java.util.List;

@Builder
public record MainPageResponseDto(String nickname, int readToastNum, int allToastNum, List<CategoryResponse> mainCategoryListDto) {
}
