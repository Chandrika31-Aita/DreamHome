package com.DreamHome.DreamHome.repositories;

import com.DreamHome.DreamHome.entities.AdminEntity;
import com.DreamHome.DreamHome.entities.BuyersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<AdminEntity,Long> {
    AdminEntity findByEmail(String email);

    AdminEntity findByAdminId(String adminId);
}
