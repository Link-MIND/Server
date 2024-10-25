package com.app.toaster.utils.valid;

import jakarta.validation.Payload;

public class Severity {
	public static class Info implements Payload {};
	public static class Error implements Payload {};
}
