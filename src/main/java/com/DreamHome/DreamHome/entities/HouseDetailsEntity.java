package com.DreamHome.DreamHome.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="housedetails")
public class HouseDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long houseId;

    @Column(name = "houseModel", nullable = false)
    private String houseModel;

    @Column(name = "sqft", nullable = false)
    private Long sqft;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "contactNumber", nullable = false, unique = true)
    private String contactNumber;

    @Column(name = "sellerId", nullable = false)
    private String sellerId;

    @Column(name = "imagePath", nullable = false)
    private String imagePath;

    @Column(name ="status",nullable = false)
    private  String status;

    @Column(name = "adminStatus",nullable = false)
    private String adminStatus;

}
