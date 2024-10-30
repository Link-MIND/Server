package com.app.toaster.recommendsite.controller;

import com.app.toaster.common.dto.ApiResponse;
import com.app.toaster.common.config.UserId;
import com.app.toaster.recommendsite.domain.RecommendSite;
import com.app.toaster.exception.Success;
import com.app.toaster.recommendsite.service.RecommendSiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendSiteController {

    private final RecommendSiteService recommendSiteService;
    @GetMapping("/sites")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<RecommendSite>> getRecommendSites(
            @UserId Long userId){
        return ApiResponse.success(Success.GET_SETTINGS_SUCCESS,recommendSiteService.getRecommendSites());
    }
}
