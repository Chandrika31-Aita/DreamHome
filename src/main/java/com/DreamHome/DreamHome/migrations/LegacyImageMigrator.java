package com.DreamHome.DreamHome.migrations;

import com.DreamHome.DreamHome.entities.HouseDetailsEntity;
import com.DreamHome.DreamHome.repositories.HouseDetailsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * One-time safety net:
 * If DB entries already reference /images/<file>, but the new upload folder is empty,
 * try to copy images from older legacy locations into the current configured images dir.
 */
@Component
public class LegacyImageMigrator implements ApplicationRunner {

    private final HouseDetailsRepository houseDetailsRepository;

    @Value("${app.upload.images-dir:uploads/images}")
    private String imagesDir;

    @Value("${app.upload.legacy-images-dirs:C:/Users/Chand/Desktop/DreamHome(2)/src/main/resources/static/images}")
    private String legacyImagesDirs;

    public LegacyImageMigrator(HouseDetailsRepository houseDetailsRepository) {
        this.houseDetailsRepository = houseDetailsRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        Path destDir = resolveDir(imagesDir);
        try {
            Files.createDirectories(destDir);
        } catch (IOException ignored) {
            return;
        }

        List<HouseDetailsEntity> all = houseDetailsRepository.findAll();
        if (all.isEmpty()) return;

        String[] legacyDirs = legacyImagesDirs.split("\\s*;\\s*");
        for (HouseDetailsEntity house : all) {
            String imagePath = house.getImagePath();
            String fileName = extractFileName(imagePath);
            if (fileName == null) continue;

            Path dest = destDir.resolve(fileName);
            if (Files.exists(dest)) continue;

            for (String legacyDirRaw : legacyDirs) {
                if (legacyDirRaw == null || legacyDirRaw.trim().isEmpty()) continue;
                Path legacyDir = resolveDir(legacyDirRaw.trim());
                Path src = legacyDir.resolve(fileName);
                if (!Files.exists(src)) continue;
                try {
                    Files.copy(src, dest);
                } catch (IOException ignored) {
                    // best-effort
                }
                break;
            }
        }
    }

    private static String extractFileName(String imagePath) {
        if (imagePath == null) return null;
        String p = imagePath.trim();
        if (p.isEmpty()) return null;
        // expected: /images/<filename>
        int idx = p.lastIndexOf('/');
        if (idx < 0 || idx == p.length() - 1) return null;
        return p.substring(idx + 1);
    }

    private static Path resolveDir(String configuredDir) {
        Path p = Paths.get(configuredDir);
        if (p.isAbsolute()) return p.normalize();
        String base = System.getProperty("user.dir");
        return Paths.get(base).resolve(p).normalize();
    }
}

