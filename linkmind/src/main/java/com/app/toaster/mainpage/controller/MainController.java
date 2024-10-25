package com.app.toaster.mainpage.controller;

import com.app.toaster.config.UserId;
import org.springframework.web.bind.annotation.*;

import com.app.toaster.common.dto.ApiResponse;
import com.app.toaster.controller.valid.TitleValid;
import com.app.toaster.search.service.SearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
public class MainController {
	private final SearchService searchService;

	@GetMapping("/search")
	public ApiResponse searchProducts(@UserId Long userId ,@TitleValid @RequestParam("query") String query){
		return searchService.searchMain(userId,query);
	}

}
