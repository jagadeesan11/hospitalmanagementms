package com.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class SwaggerJsonGenerator {

    @Autowired
    private OpenAPI openAPI;

    @Bean
    public CommandLineRunner generateSwaggerJson() {
        return args -> {
            // Create resources directory if it doesn't exist
            File resourcesDir = new File("src/main/resources");
            if (!resourcesDir.exists()) {
                resourcesDir.mkdirs();
            }

            // Generate and save swagger.json
            ObjectMapper objectMapper = new ObjectMapper();
            String swaggerJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(openAPI);

            Files.write(Paths.get("src/main/resources/swagger.json"), swaggerJson.getBytes());

            // Also generate swagger.yaml for those who prefer YAML
            String swaggerYaml = Yaml.mapper().writerWithDefaultPrettyPrinter()
                    .writeValueAsString(openAPI);

            Files.write(Paths.get("src/main/resources/swagger.yaml"), swaggerYaml.getBytes());
        };
    }
}
