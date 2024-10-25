package com.app.toaster.parse.controller.response;

public record OgResponse(String titleAdvanced, String imageAdvanced) {
	public static OgResponse of(String titleAdvanced, String imageAdvanced){
		return new OgResponse(titleAdvanced, imageAdvanced);
	}
}
