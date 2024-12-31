package com.app.toaster.admin.infrastructure;

import com.app.toaster.admin.entity.ToasterAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<ToasterAdmin, Long> {
    Optional<ToasterAdmin> findByUsername(String username);
}
