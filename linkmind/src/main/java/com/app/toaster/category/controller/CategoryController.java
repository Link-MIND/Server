package com.app.toaster.category.controller;

import com.app.toaster.category.controller.request.ChangeCateoryPriorityDto;
import com.app.toaster.category.controller.request.ChangeCateoryTitleDto;
import com.app.toaster.category.controller.request.CreateCategoryDto;
import com.app.toaster.common.dto.ApiResponse;
import com.app.toaster.common.config.UserId;
import com.app.toaster.category.controller.response.CategoriesResponse;
import com.app.toaster.toast.controller.response.ToastFilter;
import com.app.toaster.category.controller.response.GetCategoryResponseDto;
import com.app.toaster.utils.valid.Severity;
import com.app.toaster.utils.valid.TitleValid;
import com.app.toaster.exception.Success;
import com.app.toaster.category.service.CategoryService;
import com.app.toaster.search.service.SearchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;
    private final SearchService searchService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse createCateory(
            @UserId Long userId,
            @Valid @RequestBody CreateCategoryDto createCategoryDto
    ){
        categoryService.createCategory(userId, createCategoryDto);
        return ApiResponse.success(Success.CREATE_CATEGORY_SUCCESS);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse deleteCategory(
            @UserId Long userId,
            @NotNull @RequestParam(value = "deleteCategoryDto") ArrayList<Long> deleteCategoryDto
    ){
        categoryService.deleteCategory(deleteCategoryDto);
        return ApiResponse.success(Success.DELETE_CATEGORY_SUCCESS);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<CategoriesResponse> getCategories(@UserId Long userId){
        return ApiResponse.success(Success.GET_CATEORIES_SUCCESS, categoryService.getCategories(userId));
    }

    @PatchMapping("/priority")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse editCategoryPriority(
            @UserId Long userId,
            @RequestBody ChangeCateoryPriorityDto changeCateoryPriorityDto
    ){
        categoryService.editCategoryPriority(changeCateoryPriorityDto);
        return ApiResponse.success(Success.UPDATE_CATEGORY_TITLE_SUCCESS);
    }

    @PatchMapping("/title")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse editCategoryTitle(
            @UserId Long userId,
            @Valid @RequestBody ChangeCateoryTitleDto changeCateoryTitleDto
    ){
        categoryService.editCategoryTitle(changeCateoryTitleDto);
        return ApiResponse.success(Success.UPDATE_CATEGORY_TITLE_SUCCESS);
    }

    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<GetCategoryResponseDto> getCategory(
            @UserId Long userId,
            @PathVariable Long categoryId,
            @RequestParam("filter") ToastFilter filter
    ){
        return ApiResponse.success(Success.GET_CATEORY_SUCCESS,categoryService.getCategory(userId, categoryId, filter));
    }


    @GetMapping("/search")
    public ApiResponse searchProducts(@UserId Long userId , @TitleValid(payload = Severity.Error.class) @RequestParam("query") String query){
      return searchService.searchMain(userId,query);
    }

    @GetMapping("/check")
    public ApiResponse checkDuplicatedCategoryTitle(@UserId Long userId ,@RequestParam("title") String title){
        return ApiResponse.success(Success.GET_DUPLICATED_SUCCESS, categoryService.checkDuplicatedTitle(userId,title));
    }

}
