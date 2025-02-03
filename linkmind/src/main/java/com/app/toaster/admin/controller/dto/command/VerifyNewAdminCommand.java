package com.app.toaster.admin.controller.dto.command;

public record VerifyNewAdminCommand(
        Long id,
        String key,
        boolean isNewAdmin
) {
}
