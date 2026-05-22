package com.DreamHome.DreamHome.repositories;

import com.DreamHome.DreamHome.entities.HouseDetailsEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface HouseDetailsRepository extends JpaRepository<HouseDetailsEntity,Long> {
    List<HouseDetailsEntity> findByCity(String city);
    List<HouseDetailsEntity> findBySellerId(String sellerId);
    HouseDetailsEntity findByHouseId(Long houseId);
    List<HouseDetailsEntity> findByAdminStatus(String adminStatus);
    @Modifying
    @Transactional
    void deleteByHouseId(Long houseId );
}
