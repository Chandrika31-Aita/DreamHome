package com.DreamHome.DreamHome.repositories;

import com.DreamHome.DreamHome.entities.SellersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellersRepository extends JpaRepository<SellersEntity,Long> {

    SellersEntity findByEmail(String email);
}
