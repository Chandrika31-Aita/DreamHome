package com.DreamHome.DreamHome.controllers;


import com.DreamHome.DreamHome.entities.HouseDetailsEntity;
import com.DreamHome.DreamHome.services.AuthenticationService;
import com.DreamHome.DreamHome.services.HouseDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/houseDetails")
public class HouseDetailsController {
    @Autowired
    private HouseDetailsService houseDetailsService;

    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/upload-house")
    public ResponseEntity<Map<String, Object>> uploadHouse(
            @RequestParam("houseModel") String houseModel,
            @RequestParam("sqft") Long sqft,
            @RequestParam("price") Long price,
            @RequestParam("city") String city,
            @RequestParam("state") String state,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam("status") String status,
            @RequestParam("adminStatus") String adminStatus,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestHeader("Authorization") String jwtHeader
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            // ======= VALIDATE JWT =======
            if (jwtHeader == null || jwtHeader.isEmpty()) {
                response.put("error", "Authorization header is missing");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Remove "Bearer " prefix and trim spaces
            String jwt = jwtHeader.replaceFirst("(?i)^Bearer\\s+", "");
            Map<String, Object> jwtDetails = authenticationService.decodeJWT(jwt);
            String sellerId = jwtDetails.get("sellerOrBuyerOrAdminId").toString();

            // ======= CREATE HOUSE ENTITY =======
            HouseDetailsEntity house = new HouseDetailsEntity();
            house.setHouseModel(houseModel);
            house.setSqft(sqft);
            house.setPrice(price);
            house.setCity(city);
            house.setState(state);
            house.setContactNumber(contactNumber);
            house.setSellerId(sellerId);
            house.setStatus(status);
            house.setAdminStatus("pending");

            // ======= UPLOAD HOUSE WITH IMAGE =======
            HouseDetailsEntity savedHouse = houseDetailsService.uploadHouse(house, image);

            // ======= BUILD RESPONSE =======
            response.put("message", "House uploaded successfully");
            response.put("houseId", savedHouse.getHouseId());
            response.put("houseModel", savedHouse.getHouseModel());
            response.put("sqft", savedHouse.getSqft());
            response.put("price", savedHouse.getPrice());
            response.put("city", savedHouse.getCity());
            response.put("state", savedHouse.getState());
            response.put("contactNumber", savedHouse.getContactNumber());
            response.put("sellerId", savedHouse.getSellerId());
            response.put("imagePath", savedHouse.getImagePath());
            response.put("status", savedHouse.getStatus());
            response.put("adminStatus", savedHouse.getAdminStatus());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




    @GetMapping("/retrieve-houses")
    public Map<String, Object> retrieveHouses(@RequestHeader("Authorization") String jwt,@RequestParam String city) {

        authenticationService.decodeJWT(jwt);

        List<HouseDetailsEntity> housesList = houseDetailsService.retrieveHouses(city);

        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("houses_list", housesList);

        return responseJson;
    }

    @GetMapping("/retrieve-seller-houses")
    public Map<String, Object> retrieveSellerHouses(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> responseJson = new HashMap<>();
        try {
            // Remove "Bearer " if present
            String jwt = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

            // Decode JWT
            Map<String, Object> jwtDetails = authenticationService.decodeJWT(jwt);
            String sellerId = jwtDetails.get("sellerOrBuyerOrAdminId").toString();

            // Safety check
            if (sellerId == null || sellerId.isEmpty()) {
                throw new IllegalArgumentException("Invalid sellerId from JWT");
            }

            // Retrieve houses
            List<HouseDetailsEntity> sellerHousesList = houseDetailsService.retrieveSellerHouses(sellerId);
            responseJson.put("seller_houses_list", sellerHousesList);

        } catch (Exception e) {
            e.printStackTrace(); // Logs full stack trace
            responseJson.put("error", e.getMessage());
            responseJson.put("seller_houses_list", new ArrayList<>()); // safe fallback
        }
        return responseJson;
    }



    @GetMapping("/retrieve-all-houses")
    public Map<String, Object> retrieveAllHouses(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> responseJson = new HashMap<>();
        try {
            String jwt = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
            authenticationService.decodeJWT(jwt);

            List<HouseDetailsEntity> allHouses = houseDetailsService.retrieveAllHouses();
            responseJson.put("houses_list", allHouses);
        } catch (Exception e) {
            e.printStackTrace();
            responseJson.put("error", e.getMessage());
            responseJson.put("houses_list", new ArrayList<>());
        }
        return responseJson;
    }


    @PutMapping("/update-house")
    public Map<String, Object> updateHouse(@RequestBody Map<String, Object> requsetJson,
                                           @RequestHeader ("Authorization") String jwt,
                                           @RequestParam("houseId") Long houseId){
        Map<String, Object> jwtDetails = authenticationService.decodeJWT(jwt);
        String jwtSellerId=jwtDetails.get("sellerOrBuyerOrAdminId").toString();

        HouseDetailsEntity updatedHouseDetails = houseDetailsService.updateHouse(jwtSellerId, houseId, requsetJson);

        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("message", "successfully updated house");
        responseJson.put("houseId", updatedHouseDetails.getHouseId());
        responseJson.put("houseModel", updatedHouseDetails.getHouseModel());
        responseJson.put("sqft", updatedHouseDetails.getSqft());
        responseJson.put("price", updatedHouseDetails.getPrice());
        responseJson.put("city", updatedHouseDetails.getCity());
        responseJson.put("state", updatedHouseDetails.getState());
        responseJson.put("contactNumber", updatedHouseDetails.getContactNumber());
        responseJson.put("sellerId", updatedHouseDetails.getSellerId());
        responseJson.put("imagePath", updatedHouseDetails.getImagePath());
        responseJson.put("status", updatedHouseDetails.getStatus());
        return responseJson;
    }

    @DeleteMapping("/delete-house")
    public Map<String, Object> deleteHouse(@RequestParam("houseId") Long houseId,@RequestHeader("Authorization") String jwt) {

        Map<String, Object> jwtDetails = authenticationService.decodeJWT(jwt);
        String jwtSellerId=jwtDetails.get("sellerOrBuyerOrAdminId").toString();

        Boolean deletedHouseDetails = houseDetailsService.deleteHouse( jwtSellerId,houseId);

        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("status", deletedHouseDetails);
        responseJson.put("message", "successfully deleted");
        return responseJson;
    }

    @GetMapping("/pending-houses")
    public Map<String, Object> getPendingHouses(@RequestHeader("Authorization") String jwt) {
        authenticationService.decodeJWT(jwt);

        List<HouseDetailsEntity> pendingHouses = houseDetailsService.getPendingHouses();
        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("pending_houses", pendingHouses);
        return responseJson;
    }


    @PutMapping("/update-adminStatus")
    public Map<String, Object> updateAdminStatus(@RequestBody Map<String, Object> requsetJson,
                                                 @RequestHeader ("Authorization") String jwt,
                                                 @RequestParam("houseId") Long houseId) {
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
        }

        Map<String, Object> jwtDetails = authenticationService.decodeJWT(jwt);
        String jwtAdminId = jwtDetails.get("sellerOrBuyerOrAdminId").toString();

        HouseDetailsEntity updatedAdminStatus = houseDetailsService.updateAdminStatus(jwtAdminId, houseId, requsetJson);

        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("message", "successfully updated house");
        responseJson.put("adminStatus", updatedAdminStatus.getAdminStatus());
        return responseJson;
    }


}
