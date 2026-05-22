package com.DreamHome.DreamHome.controllers;

import com.DreamHome.DreamHome.entities.SellersEntity;
import com.DreamHome.DreamHome.services.AuthenticationService;
import com.DreamHome.DreamHome.services.SellersService;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/seller")

public class SellersApiController {
    @Autowired
    private SellersService sellersService;

    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/register-seller")
    public Map<String, Object> registerSeller(@RequestBody Map<String, Object> requestJson) {

        String encryptedPassword = authenticationService.encryptPassword(requestJson.get("password").toString());

        UUID sellerId = UUID.randomUUID();
        String sellerIdString = sellerId.toString();

        SellersEntity newSellerServiceRequest = new SellersEntity();
        newSellerServiceRequest.setSellerId(sellerIdString);
        newSellerServiceRequest.setSellerName(requestJson.get("sellerName").toString());
        newSellerServiceRequest.setEmail(requestJson.get("email").toString());
        newSellerServiceRequest.setPassword(encryptedPassword);
        newSellerServiceRequest.setAadharNumber(requestJson.get("aadharNumber").toString());

        SellersEntity newSellerServiceResponse = sellersService.registerSeller(newSellerServiceRequest);


        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("message", "successfully registered seller");
        responseJson.put("sellerId", newSellerServiceResponse.getSellerId());
        responseJson.put("sellerName", newSellerServiceResponse.getSellerName());
        responseJson.put("email", newSellerServiceResponse.getEmail());
        responseJson.put("aadharNumber", newSellerServiceResponse.getAadharNumber());
        return responseJson;
    }

    @PostMapping("/login-seller")
    public Map<String, Object> loginSeller(@RequestBody Map<String, Object> requestJson) {
        String email = requestJson.get("email").toString();
        String password = requestJson.get("password").toString();

        String jwt = sellersService.loginSeller(email, password);
        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("jwt", jwt);
        return responseJson;
    }

    @PutMapping("/update-seller")
    public Map<String, Object> updateSeller(@RequestBody Map<String, Object> requsetJson, @RequestHeader ("Authorization") String jwt) {
        Map<String, Object> jwtDetails = authenticationService.decodeJWT(jwt);
        String jwtEmail = jwtDetails.get("email").toString();

        SellersEntity updateSellerResponseEntity = sellersService.updateSeller(jwtEmail, requsetJson);

        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("message", "successfully updated seller");
        responseJson.put("sellerId", updateSellerResponseEntity.getSellerId());
        responseJson.put("sellerName", updateSellerResponseEntity.getSellerName());
        responseJson.put("email", updateSellerResponseEntity.getEmail());
        responseJson.put("aadharNumber", updateSellerResponseEntity.getAadharNumber());
        return responseJson;
    }

}
