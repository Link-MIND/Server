package com.app.toaster.admin.infrastructure;

import com.app.toaster.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
