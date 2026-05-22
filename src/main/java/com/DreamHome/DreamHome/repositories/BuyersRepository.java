package com.DreamHome.DreamHome.repositories;

import com.DreamHome.DreamHome.entities.BuyersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyersRepository extends JpaRepository<BuyersEntity,Long> {
    BuyersEntity findByEmail(String email);
}
