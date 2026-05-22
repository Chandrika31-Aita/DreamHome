package com.DreamHome.DreamHome.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${app.upload.images-dir:uploads/images}")
    private String imagesDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path folder = resolveImagesDir(imagesDir);
        try {
            Files.createDirectories(folder);
        } catch (IOException ignored) {
            // If this fails, uploads will fail anyway; don't block app startup.
        }
        String location = folder.toUri().toString(); // file:/.../uploads/images/

        registry.addResourceHandler("/images/**")
                // 1) uploaded files from disk
                .addResourceLocations(location)
                // 2) fallback bundled placeholder(s) from classpath
                .addResourceLocations("classpath:/static/images/");
    }

    private static Path resolveImagesDir(String configuredDir) {
        Path p = Paths.get(configuredDir);
        if (p.isAbsolute()) return p.normalize();
        String base = System.getProperty("user.dir");
        return Paths.get(base).resolve(p).normalize();
    }
}

