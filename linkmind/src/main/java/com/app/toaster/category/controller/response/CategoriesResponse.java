package com.app.toaster.category.controller.response;

import java.util.List;

public record CategoriesResponse(Long toastNumberInEntire ,List<CategoryResponse> categories) {
	public static CategoriesResponse of(Long toastNumberInEntire ,List<CategoryResponse> categories){
		return new CategoriesResponse(toastNumberInEntire, categories);
	}
}
