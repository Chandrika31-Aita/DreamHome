package com.DreamHome.DreamHome.services;

import com.DreamHome.DreamHome.entities.BuyersEntity;
import com.DreamHome.DreamHome.entities.SellersEntity;
import com.DreamHome.DreamHome.repositories.BuyersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BuyersService {
    @Autowired
    private BuyersRepository buyersRepository;

    @Autowired
    private AuthenticationService authenticationService;

    public BuyersEntity registerBuyer(BuyersEntity newBuyerDetails){
        BuyersEntity newBuyerResponse = buyersRepository.save(newBuyerDetails);
        return newBuyerResponse;
    }


    public String loginBuyer(String email, String password) {
        BuyersEntity buyerDetails = buyersRepository.findByEmail(email);
        String dbPassword = buyerDetails.getPassword();


        Boolean passwordVerificationStatus = authenticationService.verifyPassword(password, dbPassword);

         if (passwordVerificationStatus) {
            String jwt = authenticationService.createJWT(email, buyerDetails.getBuyerId(), 0); // No expiration
                return jwt;
         } else {
            return null;
         }

    }

    public BuyersEntity updateBuyer(String jwtEmail, Map<String,Object> requestJson) {
        String encryptPassword = authenticationService.encryptPassword(requestJson.get("password").toString());

        BuyersEntity buyerDetails = buyersRepository.findByEmail(jwtEmail);
        buyerDetails.setBuyerName(requestJson.get("buyerName").toString());
        buyerDetails.setEmail(requestJson.get("email").toString());
        buyerDetails.setPassword(encryptPassword);

        BuyersEntity updatedBuyerResponseEntity = buyersRepository.save(buyerDetails);
        return updatedBuyerResponseEntity;
    }


}
