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
@Table(name="buyers")

public class BuyersEntity {
    @Id
    private String buyerId;

    @Column(name="buyerName",nullable=false)
    private String buyerName;

    @Column(name="email",nullable=false,unique = true)
    private String email;

    @Column(name="password",nullable = false)
    private String password ;

}
