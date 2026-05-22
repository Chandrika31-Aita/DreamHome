package com.DreamHome.DreamHome.services;

import com.DreamHome.DreamHome.entities.HouseDetailsEntity;
import com.DreamHome.DreamHome.repositories.AdminRepository;
import com.DreamHome.DreamHome.repositories.HouseDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class HouseDetailsService {
    @Autowired
    private HouseDetailsRepository houseDetailsRepository;

    // Removed unused sellersRepository to keep linter clean

    @Autowired
    private AdminRepository adminRepository;

    @Value("${app.upload.images-dir:uploads/images}")
    private String imagesDir;

    public HouseDetailsEntity uploadHouse(HouseDetailsEntity house, MultipartFile image) {
        try {
            if (image != null && !image.isEmpty()) {
                // Save images outside the classpath so it works from a packaged JAR.
                Path folder = resolveImagesDir(imagesDir);
                if (!Files.exists(folder)) {
                    Files.createDirectories(folder);
                }

                // Clean file name
                String originalFileName = image.getOriginalFilename();
                String safeFileName = System.currentTimeMillis() + "_" +
                        originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

                Path fullPath = folder.resolve(safeFileName);
                Files.write(fullPath, image.getBytes());

                house.setImagePath("/images/" + safeFileName);
            } else {
                // Keep non-null for DB constraint; UI already falls back to placeholders.
                house.setImagePath("");
            }

            return houseDetailsRepository.save(house);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + e.getMessage(), e);
        }
    }

    private static Path resolveImagesDir(String configuredDir) {
        Path p = Paths.get(configuredDir);
        if (p.isAbsolute()) return p.normalize();
        // user.dir is the folder where the app is launched from (works for IDE + JAR).
        String base = System.getProperty("user.dir");
        return Paths.get(base).resolve(p).normalize();
    }



    public List<HouseDetailsEntity> retrieveHouses(String city) {
        List<HouseDetailsEntity> housesList = houseDetailsRepository.findByCity(city);
        return housesList;
    }

    public List<HouseDetailsEntity> retrieveSellerHouses(String sellerId) {
        List<HouseDetailsEntity> sellershouseList = houseDetailsRepository.findBySellerId(sellerId);
        return sellershouseList;
    }

    public List<HouseDetailsEntity> retrieveAllHouses() {
        return houseDetailsRepository.findAll();
    }

    public HouseDetailsEntity updateHouse(String jwtSellerId, Long houseId, Map<String, Object> requestJson) {

        HouseDetailsEntity houseDetails = houseDetailsRepository.findByHouseId(houseId);

        if (Objects.equals(houseDetails.getSellerId(), jwtSellerId)) {
            houseDetails.setHouseModel(requestJson.get("houseModel").toString());
            houseDetails.setSqft(Long.parseLong(requestJson.get("sqft").toString()));
            houseDetails.setPrice(Long.parseLong(requestJson.get("price").toString()));
            houseDetails.setCity(requestJson.get("city").toString());
            houseDetails.setState(requestJson.get("state").toString());
            houseDetails.setContactNumber(requestJson.get("contactNumber").toString());
            houseDetails.setImagePath(requestJson.get("imagePath").toString());
            houseDetails.setStatus(requestJson.get("status").toString());
            houseDetails.setAdminStatus(requestJson.get("adminStatus").toString());



            HouseDetailsEntity updatedHouseDetails = houseDetailsRepository.save(houseDetails);
            return updatedHouseDetails;
        } else {
            return null;
        }
    }

    public Boolean deleteHouse(String jwtSellerId, Long houseId) {

        HouseDetailsEntity houseDetails = houseDetailsRepository.findByHouseId(houseId);
        if (Objects.equals(houseDetails.getSellerId(), jwtSellerId)) {
            houseDetailsRepository.deleteByHouseId(houseId);
            return true;
        } else {
            return null;
        }
    }

    public List<HouseDetailsEntity> getPendingHouses() {
        return houseDetailsRepository.findByAdminStatus("pending");
    }


    public HouseDetailsEntity updateAdminStatus(String jwtAdminId, Long houseId, Map<String, Object> requestJson) {
        HouseDetailsEntity houseDetails = houseDetailsRepository.findByHouseId(houseId);

        adminRepository.findByAdminId(jwtAdminId);

        houseDetails.setAdminStatus(requestJson.get("adminStatus").toString());

        HouseDetailsEntity updatedAdminStatus = houseDetailsRepository.save(houseDetails);
        return updatedAdminStatus;
    }


}

