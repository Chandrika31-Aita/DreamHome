package com.DreamHome.DreamHome.controllers;

import com.DreamHome.DreamHome.entities.BuyersEntity;
import com.DreamHome.DreamHome.entities.SellersEntity;
import com.DreamHome.DreamHome.services.AuthenticationService;
import com.DreamHome.DreamHome.services.BuyersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/buyer")

public class BuyersApiController {
    @Autowired
    private BuyersService buyersService;

    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/register-buyer")
    public Map<String, Object> registerBuyer(@RequestBody Map<String, Object> requestJson) {

        String encryptedPassword = authenticationService.encryptPassword(requestJson.get("password").toString());

        UUID buyerId = UUID.randomUUID();
        String buyerIdString = buyerId.toString();

        BuyersEntity newBuyerServiceRequest = new BuyersEntity();
        newBuyerServiceRequest.setBuyerId(buyerIdString);
        newBuyerServiceRequest.setBuyerName(requestJson.get("buyerName").toString());
        newBuyerServiceRequest.setEmail(requestJson.get("email").toString());
        newBuyerServiceRequest.setPassword(encryptedPassword);

        BuyersEntity newBuyerServiceResponse = buyersService.registerBuyer(newBuyerServiceRequest);


        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("message", "successfully registered buyer");
        responseJson.put("buyerId", newBuyerServiceResponse.getBuyerId());
        responseJson.put("buyerName", newBuyerServiceResponse.getBuyerName());
        responseJson.put("email", newBuyerServiceResponse.getEmail());
        return responseJson;
    }


    @PostMapping("/login-buyer")
    public Map<String , Object> loginBuyer(@RequestBody Map<String, Object> requestJson) {
        String email = requestJson.get("email").toString();
        String password = requestJson.get("password").toString();

        String jwt = buyersService.loginBuyer(email, password);
        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("jwt", jwt);
        return responseJson;
    }

    @PutMapping("/update-buyer")
    public Map<String, Object> updateBuyer(@RequestBody Map<String, Object> requsetJson, @RequestHeader ("Authorization") String jwt) {
        Map<String, Object> jwtDetails = authenticationService.decodeJWT(jwt);
        String jwtEmail = jwtDetails.get("email").toString();

        BuyersEntity updateBuyerResponseEntity = buyersService.updateBuyer(jwtEmail, requsetJson);

        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("message", "successfully updated buyer");
        responseJson.put("buyerId", updateBuyerResponseEntity.getBuyerId());
        responseJson.put("buyerName", updateBuyerResponseEntity.getBuyerName());
        responseJson.put("email", updateBuyerResponseEntity.getEmail());
        return responseJson;
    }

}

