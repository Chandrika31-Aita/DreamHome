package com.DreamHome.DreamHome.services;

import com.DreamHome.DreamHome.entities.SellersEntity;
import com.DreamHome.DreamHome.repositories.SellersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SellersService {
    @Autowired
    private SellersRepository sellersRepository;

    @Autowired
    private AuthenticationService authenticationService;

    public SellersEntity registerSeller(SellersEntity newSellerDetails) {

        SellersEntity newSellerResponse = sellersRepository.save(newSellerDetails);
        return newSellerResponse;
    }

    public String loginSeller(String email, String password) {
        SellersEntity sellerDetails = sellersRepository.findByEmail(email);
        String dbPassword = sellerDetails.getPassword();


        Boolean passwordVerificationStatus = authenticationService.verifyPassword(password, dbPassword);

         if (passwordVerificationStatus) {
           String jwt = authenticationService.createJWT(email, sellerDetails.getSellerId(), 0); // No expiration
            return jwt;
      }
         else {
            return null;
        }
    }

    public SellersEntity updateSeller(String jwtEmail, Map<String,Object> requestJson){
        String encryptPassword = authenticationService.encryptPassword(requestJson.get("password").toString());

        SellersEntity sellerDetails = sellersRepository.findByEmail(jwtEmail);
        sellerDetails.setSellerName(requestJson.get("sellerName").toString());
        sellerDetails.setEmail(requestJson.get("email").toString());
        sellerDetails.setPassword(encryptPassword);
        sellerDetails.setAadharNumber(requestJson.get("aadharNumber").toString());

        SellersEntity updatedSellerResponseEntity = sellersRepository.save(sellerDetails);
        return updatedSellerResponseEntity;
    }
}
