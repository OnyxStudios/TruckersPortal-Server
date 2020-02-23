package dev.onyxstudios.truckersportal;

import dev.onyxstudios.truckersportal.fileupload.storage.StorageProperties;
import dev.onyxstudios.truckersportal.fileupload.storage.StorageService;
import dev.onyxstudios.truckersportal.utils.MongoUtils;
import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.*;
import java.net.URISyntaxException;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class TruckersPortal {

    public static String URL;
    public static MongoUtils mongoUtils;
    public static String[] COLLECTIONS = new String[]{"loads", "drivers", "users", "tokens", "carrier"};

    public static void main(String[] args) {

        try {
            File file = new File(TruckersPortal.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath() + "/keystore_pass.txt");

            if(file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                URL = reader.readLine();
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mongoUtils = new MongoUtils(URL, "truckersportal");
        for (String collection : COLLECTIONS) {
            if(!mongoUtils.collectionExists(collection))
                mongoUtils.createMongoCollection(collection);
        }

        if (mongoUtils.getFirstDocument("carrier") == null) {
            Document document = new Document();
            document.append("name", "Truckers Portal");
            document.append("email", "truckersportal@gmail.com");
            document.append("number", "(313) 315-6458");
            document.append("street", "1111 Some Street");
            document.append("city", "Some City");
            document.append("state", "MD");
            document.append("zipCode", "48321");

            document.append("factoring", false);
            document.append("factoringName", "");
            document.append("factoringStreet", "");
            document.append("factoringCity", "");
            document.append("factoringState", "");
            document.append("factoringZip", "");
        }

        SpringApplication.run(TruckersPortal.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }
}
