package com.app.toaster.controller;

import com.app.toaster.config.UserId;
import org.springframework.web.bind.annotation.*;

import com.app.toaster.common.dto.ApiResponse;
import com.app.toaster.config.UserId;
import com.app.toaster.service.UserService;
import com.app.toaster.service.search.SearchService;
import com.app.toaster.service.toast.ToastService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
public class MainController {
	private final SearchService searchService;

	@GetMapping("/search")
	public ApiResponse searchProducts(@UserId Long userId ,@RequestParam("query") String query){
		return searchService.searchMain(userId,query);
	}

}
