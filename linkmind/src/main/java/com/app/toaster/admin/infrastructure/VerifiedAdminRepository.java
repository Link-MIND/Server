package com.app.toaster.admin.infrastructure;

import com.app.toaster.admin.entity.VerifiedAdmin;
import com.app.toaster.admin.entity.ToasterAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerifiedAdminRepository extends JpaRepository<VerifiedAdmin, Long> {
    Optional<VerifiedAdmin> findByAdmin(final ToasterAdmin admin);
}
