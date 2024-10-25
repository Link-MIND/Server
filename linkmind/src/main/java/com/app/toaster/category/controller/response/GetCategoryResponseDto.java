package com.app.toaster.category.controller.response;

import com.app.toaster.toast.controller.response.ToastDto;
import lombok.Builder;

import java.util.List;

@Builder
public record GetCategoryResponseDto(
        int allToastNum,
        List<ToastDto> toastListDto
){

}
