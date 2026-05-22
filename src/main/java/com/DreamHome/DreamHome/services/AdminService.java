package com.DreamHome.DreamHome.services;


import com.DreamHome.DreamHome.entities.AdminEntity;
import com.DreamHome.DreamHome.entities.BuyersEntity;
import com.DreamHome.DreamHome.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AuthenticationService authenticationService;

    public AdminEntity registerAdmin(AdminEntity newAdminDetails) {
        AdminEntity newAdminResponse = adminRepository.save(newAdminDetails);
        return newAdminResponse;
    }


    public String loginAdmin(String email, String password) {
        AdminEntity adminDetails = adminRepository.findByEmail(email);
        String dbPassword = adminDetails.getPassword();


        Boolean passwordVerificationStatus = authenticationService.verifyPassword(password, dbPassword);

        if (passwordVerificationStatus) {
            String jwt = authenticationService.createJWT(email, adminDetails.getAdminId(), 0); // No expiration
            return jwt;
        } else {
            return null;
        }

    }

    public AdminEntity updateAdmin(String jwtEmail, Map<String,Object> requestJson) {
        String encryptPassword = authenticationService.encryptPassword(requestJson.get("password").toString());

        AdminEntity adminDetails = adminRepository.findByEmail(jwtEmail);
        adminDetails.setAdminName(requestJson.get("adminName").toString());
        adminDetails.setEmail(requestJson.get("email").toString());
        adminDetails.setPassword(encryptPassword);

        AdminEntity updatedAdminResponseEntity = adminRepository.save(adminDetails);
        return updatedAdminResponseEntity;
    }

}
