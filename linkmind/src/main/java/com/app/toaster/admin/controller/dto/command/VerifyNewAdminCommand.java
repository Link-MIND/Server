package com.app.toaster.admin.controller.dto.command;

import com.app.toaster.admin.domain.AdminStatus;

public record VerifyNewAdminCommand(
        Long id,
        String key,
        AdminStatus adminStatus
) {
}
