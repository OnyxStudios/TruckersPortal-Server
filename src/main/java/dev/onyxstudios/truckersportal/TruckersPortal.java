package dev.onyxstudios.truckersportal;

import dev.onyxstudios.truckersportal.fileupload.storage.StorageProperties;
import dev.onyxstudios.truckersportal.fileupload.storage.StorageService;
import dev.onyxstudios.truckersportal.utils.CarrierProfile;
import dev.onyxstudios.truckersportal.utils.FactoringProfile;
import dev.onyxstudios.truckersportal.utils.MongoUtils;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.*;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class TruckersPortal {

    public static YamlFile CONFIG = new YamlFile("config.yml");
    public static MongoUtils mongoUtils;
    public static String[] COLLECTIONS = new String[]{"loads", "drivers", "users", "tokens"};
    public static CarrierProfile carrierProfile;
    public static String USERNAME;
    public static String PASSWORD;

    public static void main(String[] args) throws IOException, InvalidConfigurationException {
        if(!CONFIG.exists()) {
            throw new FileNotFoundException("Could not find config.yml! Make sure you read the Getting Started Guide!");
        }

        CONFIG.load();
        FactoringProfile factoringProfile = CONFIG.getBoolean("factoring") ?
                new FactoringProfile(
                        CONFIG.getString("factoring-name"),
                        CONFIG.getString("factoring-street"),
                        CONFIG.getString("factoring-city"),
                        CONFIG.getString("factoring-state"),
                        CONFIG.getString("factoring-zipCode")
                ) : new FactoringProfile();
        carrierProfile = new CarrierProfile(
                CONFIG.getString("name"),
                CONFIG.getString("email"),
                CONFIG.getString("phoneNumber"),
                CONFIG.getString("street"),
                CONFIG.getString("city"),
                CONFIG.getString("state"),
                CONFIG.getString("zipCode"),
                CONFIG.getString("domain"),
                CONFIG.getBoolean("factoring"),
                factoringProfile
        );

        USERNAME = CONFIG.getString("gmail");
        PASSWORD = CONFIG.getString("gmail-password");
        mongoUtils = new MongoUtils(CONFIG.getString("mongodb-connection"), "truckersportal");
        for (String collection : COLLECTIONS) {
            if (!mongoUtils.collectionExists(collection))
                mongoUtils.createMongoCollection(collection);
        }

        SpringApplication.run(TruckersPortal.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> storageService.init();
    }
}
