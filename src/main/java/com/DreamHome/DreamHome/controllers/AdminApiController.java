package com.DreamHome.DreamHome.controllers;

import com.DreamHome.DreamHome.entities.AdminEntity;
import com.DreamHome.DreamHome.entities.BuyersEntity;
import com.DreamHome.DreamHome.services.AdminService;
import com.DreamHome.DreamHome.services.AuthenticationService;
import com.DreamHome.DreamHome.services.BuyersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")

public class AdminApiController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/register-admin")
    public Map<String, Object> registerAdmin(@RequestBody Map<String, Object> requestJson) {

        String encryptedPassword = authenticationService.encryptPassword(requestJson.get("password").toString());

        UUID adminId = UUID.randomUUID();
        String adminIdString = adminId.toString();

        AdminEntity newAdminServiceRequest = new AdminEntity();
        newAdminServiceRequest.setAdminId(adminIdString);
        newAdminServiceRequest.setAdminName(requestJson.get("adminName").toString());
        newAdminServiceRequest.setEmail(requestJson.get("email").toString());
        newAdminServiceRequest.setPassword(encryptedPassword);

        AdminEntity newAdminServiceResponse = adminService.registerAdmin(newAdminServiceRequest);


        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("message", "successfully registered admin");
        responseJson.put("adminId", newAdminServiceResponse.getAdminId());
        responseJson.put("adminName", newAdminServiceResponse.getAdminName());
        responseJson.put("email", newAdminServiceResponse.getEmail());
        return responseJson;
    }


    @PostMapping("/login-admin")
    public Map<String , Object> loginAdmin(@RequestBody Map<String, Object> requestJson) {
        String email = requestJson.get("email").toString();
        String password = requestJson.get("password").toString();

        String jwt = adminService.loginAdmin(email, password);
        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("jwt", jwt);
        return responseJson;
    }

    @PutMapping("/update-admin")
    public Map<String, Object> updateAdmin(@RequestBody Map<String, Object> requestJson, @RequestHeader ("Authorization") String jwt) {
        Map<String, Object> jwtDetails = authenticationService.decodeJWT(jwt);
        String jwtEmail = jwtDetails.get("email").toString();

        AdminEntity updateAdminResponseEntity = adminService.updateAdmin(jwtEmail, requestJson);

        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("message", "successfully updated buyer");
        responseJson.put("adminId", updateAdminResponseEntity.getAdminId());
        responseJson.put("adminName", updateAdminResponseEntity.getAdminName());
        responseJson.put("email", updateAdminResponseEntity.getEmail());
        return responseJson;
    }

}


