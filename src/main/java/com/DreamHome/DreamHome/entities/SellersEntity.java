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
@Table(name="sellers")

public class SellersEntity {
    @Id
    private String sellerId;

    @Column(name = "sellerName", nullable = false)
    private String sellerName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "aadharNumber", nullable = false, unique = true)
    private String aadharNumber;
}
