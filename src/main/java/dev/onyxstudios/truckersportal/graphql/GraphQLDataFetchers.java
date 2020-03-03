package dev.onyxstudios.truckersportal.graphql;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCursor;
import dev.onyxstudios.truckersportal.utils.EmailUtils;
import graphql.schema.DataFetcher;
import dev.onyxstudios.truckersportal.TruckersPortal;
import dev.onyxstudios.truckersportal.utils.SecurityUtils;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GraphQLDataFetchers {

    public DataFetcher getLoadByIdFetcher() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                Document load = TruckersPortal.mongoUtils.getDocument("loads", new Document("id", environment.getArgument("id")));
                load.append("driver", TruckersPortal.mongoUtils.getDocument("drivers", new Document("id", load.getString("driverId"))).getString("name"));

                return load;
            }

            return null;
        };
    }

    public DataFetcher getDriverByIdFetcher() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                String id = environment.getArgument("id");
                Document driver = TruckersPortal.mongoUtils.getDocument("drivers", new Document("id", id));
                driver.append("earnings", getDriverEarnings(driver.getList("loadsComplete", String.class), driver.getDouble("payCut")));

                return driver;
            }

            return null;
        };
    }

    public DataFetcher getUserByIdFetcher() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                return TruckersPortal.mongoUtils.getDocument("users", new Document("id", environment.getArgument("id")));
            }

            return null;
        };
    }

    public DataFetcher addLoadFetcher() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                String loadId = "load-" + (int) Math.floor(100000 + Math.random() * 900000);
                String driverId = environment.getArgument("driverId");
                String status = environment.getArgument("status");

                Document document = new Document();
                document.append("id", loadId);
                document.append("brokerName", environment.getArgument("brokerName"));
                document.append("loadNumber", environment.getArgument("loadNumber"));
                document.append("rate", environment.getArgument("rate"));
                document.append("detention", environment.getArgument("detention"));
                document.append("driverId", driverId);
                document.append("status", environment.getArgument("status"));
                document.append("paid", environment.getArgument("paid") != null ? environment.getArgument("paid") : false);

                addLoadToDriver(driverId, loadId, status);
                TruckersPortal.mongoUtils.insertDocument("loads", document);
                return document;
            }

            return null;
        };
    }

    public DataFetcher addDriverFetcher() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                Document document = new Document();
                document.append("id", "driver-" + (int) Math.floor(100000 + Math.random() * 900000));
                document.append("name", environment.getArgument("name"));
                document.append("payCut", environment.getArgument("payCut"));
                document.append("loadsComplete", new ArrayList<>());
                document.append("phoneNumber", environment.getArgument("phoneNumber"));
                document.append("status", "Sitting");

                TruckersPortal.mongoUtils.insertDocument("drivers", document);
                return document;
            }

            return null;
        };
    }

    public DataFetcher addUserFetcher() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                String randomPassword = SecurityUtils.generateToken();
                String email = environment.getArgument("email");
                String firstName = environment.getArgument("firstName");

                Document document = new Document();
                document.append("id", "user-" + (int) Math.floor(100000 + Math.random() * 900000));
                document.append("firstName", firstName);
                document.append("lastName", environment.getArgument("lastName"));
                document.append("email", email);
                document.append("phoneNumber", environment.getArgument("phoneNumber"));
                document.append("permissions", environment.getArgument("permissions"));
                document.append("password", SecurityUtils.hash(randomPassword.toCharArray()));
                document.append("token", SecurityUtils.generateToken());

                TruckersPortal.mongoUtils.insertDocument("users", document);
                EmailUtils.sendRegistrationEmail(email, firstName, randomPassword);
                return document;
            }

            return null;
        };
    }

    public DataFetcher changeDriverStatus() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                String driverId = environment.getArgument("id");
                Document newData = TruckersPortal.mongoUtils.getDocument("drivers", new Document("id", driverId));
                newData.append("status", environment.getArgument("status"));

                TruckersPortal.mongoUtils.updateDocument("drivers", new Document("id", driverId), newData);
                return newData;
            }

            return null;
        };
    }

    public DataFetcher authenticateUserFetcher() {
        return environment -> {
            Document user = TruckersPortal.mongoUtils.getDocument("users", new Document("email", environment.getArgument("email")));
            String password = environment.getArgument("password");
            return SecurityUtils.authenticate(password.toCharArray(), user.getString("password")) ? user : null;
        };
    }

    public DataFetcher updateCarrierFetcher() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                TruckersPortal.carrierProfile.name = environment.getArgument("name");
                TruckersPortal.carrierProfile.email = environment.getArgument("email");
                TruckersPortal.carrierProfile.phoneNumber = environment.getArgument("phoneNumber");
                TruckersPortal.carrierProfile.street = environment.getArgument("street");
                TruckersPortal.carrierProfile.city = environment.getArgument("city");
                TruckersPortal.carrierProfile.state = environment.getArgument("state");
                TruckersPortal.carrierProfile.zipCode = environment.getArgument("zipCode");
                TruckersPortal.carrierProfile.factoring = environment.getArgument("factoring");
                TruckersPortal.carrierProfile.factoringProfile.name = environment.getArgument("factoringName");
                TruckersPortal.carrierProfile.factoringProfile.street = environment.getArgument("factoringStreet");
                TruckersPortal.carrierProfile.factoringProfile.city = environment.getArgument("factoringCity");
                TruckersPortal.carrierProfile.factoringProfile.state = environment.getArgument("factoringState");
                TruckersPortal.carrierProfile.factoringProfile.zipCode = environment.getArgument("factoringZip");

                TruckersPortal.carrierProfile.saveConfig();
                return TruckersPortal.carrierProfile.toDocument();
            }

            return null;
        };
    }

    public DataFetcher getCarrierProfile() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                return TruckersPortal.carrierProfile.toDocument();
            }

            return null;
        };
    }

    public DataFetcher getLoadsFetcher() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                List<Document> loads = new ArrayList<>();
                MongoCursor<Document> cursor = TruckersPortal.mongoUtils.getTableData("loads");

                while (cursor.hasNext()) {
                    Document load = cursor.next();
                    load.append("driver", TruckersPortal.mongoUtils.getDocument("drivers", new Document("id", load.getString("driverId"))).getString("name"));
                    loads.add(load);
                }

                return loads;
            }

            return null;
        };
    }

    public DataFetcher getDriversFetcher() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                List<Document> drivers = new ArrayList<>();
                MongoCursor<Document> cursor = TruckersPortal.mongoUtils.getTableData("drivers");
                while (cursor.hasNext()) {
                    Document driver = cursor.next();
                    driver.append("earnings", getDriverEarnings(driver.getList("loadsComplete", String.class), driver.getDouble("payCut")));
                    drivers.add(driver);
                }

                return drivers;
            }

            return new ArrayList<>();
        };
    }

    public DataFetcher getUsersFetcher() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                return Lists.newArrayList(TruckersPortal.mongoUtils.getTableData("users"));
            }

            return null;
        };
    }

    public DataFetcher getTotalRevenue() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                float revenue = 0;
                MongoCursor<Document> cursor = TruckersPortal.mongoUtils.getTableData("loads");
                while (cursor.hasNext()) {
                    Document load = cursor.next();
                    if (load.get("paid") != null && load.getBoolean("paid"))
                        revenue += load.getDouble("rate") + load.getDouble("detention");
                }

                return new Document("revenue", revenue);
            }

            return new Document("revenue", 0);
        };
    }

    public DataFetcher getUnpaidLoads() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                float unpaidRevenue = 0;
                MongoCursor<Document> cursor = TruckersPortal.mongoUtils.getTableData("loads");
                while (cursor.hasNext()) {
                    Document load = cursor.next();
                    if (load.get("paid") != null && !load.getBoolean("paid"))
                        unpaidRevenue += load.getDouble("rate") + load.getDouble("detention");
                }

                return new Document("revenue", unpaidRevenue);
            }

            return new Document("revenue", 0);
        };
    }

    public DataFetcher getCurrentLoads() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                int currentLoads = 0;
                MongoCursor<Document> cursor = TruckersPortal.mongoUtils.getTableData("loads");
                while (cursor.hasNext()) {
                    Document load = cursor.next();
                    if (!load.getString("status").equalsIgnoreCase("Complete"))
                        currentLoads++;
                }

                return new Document("loads", currentLoads);
            }

            return new Document("loads", 0);
        };
    }

    public DataFetcher getCompletedLoads() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                int completedLoads = 0;
                MongoCursor<Document> cursor = TruckersPortal.mongoUtils.getTableData("loads");
                while (cursor.hasNext()) {
                    Document load = cursor.next();
                    if (load.getString("status").equalsIgnoreCase("Complete"))
                        completedLoads++;
                }

                return new Document("loads", completedLoads);
            }

            return new Document("loads", 0);
        };
    }

    public DataFetcher updateUserFetcher() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                String userId = environment.getArgument("userId");
                Document newData = TruckersPortal.mongoUtils.getDocument("users", new Document("id", userId));
                newData.append("firstName", environment.getArgument("firstName"));
                newData.append("lastName", environment.getArgument("lastName"));
                newData.append("email", environment.getArgument("email"));
                newData.append("phoneNumber", environment.getArgument("phoneNumber"));

                TruckersPortal.mongoUtils.updateDocument("users", new Document("id", userId), newData);
                return newData;
            }

            return null;
        };
    }

    public DataFetcher updateUserPasswordFetcher() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                String userId = environment.getArgument("userId");
                Document user = TruckersPortal.mongoUtils.getDocument("users", new Document("id", userId));

                if(SecurityUtils.authenticate(environment.getArgument("currentPassword").toString().toCharArray(), user.getString("password"))) {
                    Document newData = new Document(user);
                    newData.append("password", SecurityUtils.hash(environment.getArgument("newPassword").toString().toCharArray()));
                    newData.append("token", SecurityUtils.generateToken());
                    TruckersPortal.mongoUtils.updateDocument("users", new Document("id", userId), newData);

                    EmailUtils.sendPasswordChangeEmail(newData.getString("email"), newData.getString("firstName"));
                    return newData;
                }

                return new Document("id", "INCORRECT");
            }

            return null;
        };
    }

    public DataFetcher updateLoadStatusFetcher() {
        return environment -> {
            if(authenticateToken(environment.getArgument("token")) != null) {
                String loadId = environment.getArgument("loadId");
                Document load = TruckersPortal.mongoUtils.getDocument("loads", new Document("id", loadId));
                load.put("status", environment.getArgument("status"));
                load.put("paid", environment.getArgument("paid") != null ? environment.getArgument("paid") : false);

                TruckersPortal.mongoUtils.updateDocument("loads", new Document("id", loadId), load);
                return load;
            }

            return null;
        };
    }

    public DataFetcher authenticateTokenFetcher() {
        return environment -> authenticateToken(environment.getArgument("token"));
    }

    public Document authenticateToken(String token) {
        return TruckersPortal.mongoUtils.getDocument("users", new Document("token", token));
    }

    public float getDriverEarnings(List<String> loads, double rawPayCut) {
        float earnings = 0;
        double payCut = 1 - (rawPayCut / 100);
        for (String loadId : loads) {
            Document load = TruckersPortal.mongoUtils.getDocument("loads", new Document("id", loadId));
            earnings += (load.getDouble("rate") + load.getDouble("detention")) * payCut;
        }

        return earnings;
    }

    public void addLoadToDriver(String driverId, String loadId, String status) {
        Document driver = TruckersPortal.mongoUtils.getDocument("drivers", new Document("id", driverId));

        List<String> loads = (List<String>) driver.get("loadsComplete");
        loads.add(loadId);
        driver.put("loadsComplete", loads);
        if(status.equalsIgnoreCase("in progress")) {
            driver.put("status", "Driving");
        }

        TruckersPortal.mongoUtils.updateDocument("drivers", new Document("id", driver.getString("id")), driver);
    }
}
