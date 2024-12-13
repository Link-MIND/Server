package com.app.toaster.admin.infrastructure;

import com.app.toaster.admin.domain.VerifiedAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerifiedAdminRepository extends JpaRepository<VerifiedAdmin, Long> {
}
