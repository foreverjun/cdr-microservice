package ru.nexignbootcamp.cdrmicroservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.nexignbootcamp.cdrmicroservice.service.DataGeneratorService;

import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {
    private final DataGeneratorService dataGeneratorService;
    public DataInitializer (DataGeneratorService dataGeneratorService){
        this.dataGeneratorService = dataGeneratorService;
    }

    @Override
    public void run(String... args) throws Exception {
        java.nio.file.Path reportsDir = Paths.get("reports");
        if (!Files.exists(reportsDir)) {
            Files.createDirectory(reportsDir);
        }
        dataGeneratorService.generateData();
    }
}
