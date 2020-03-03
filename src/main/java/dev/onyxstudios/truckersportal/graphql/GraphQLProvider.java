package dev.onyxstudios.truckersportal.graphql;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;

@Component
public class GraphQLProvider {

    private GraphQL graphQL;
    @Autowired
    public GraphQLDataFetchers graphQLDataFetchers;

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

    @PostConstruct
    public void init() throws IOException {
        URL url = Resources.getResource("schema.graphqls");
        String sdl = Resources.toString(url, Charsets.UTF_8);
        GraphQLSchema graphQLSchema = buildSchema(sdl);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    private GraphQLSchema buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring wiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();

        return schemaGenerator.makeExecutableSchema(typeRegistry, wiring);
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type(TypeRuntimeWiring.newTypeWiring("Query")
                        .dataFetcher("loadById", graphQLDataFetchers.getLoadByIdFetcher())
                        .dataFetcher("driverById", graphQLDataFetchers.getDriverByIdFetcher())
                        .dataFetcher("userById", graphQLDataFetchers.getUserByIdFetcher())
                        .dataFetcher("getLoads", graphQLDataFetchers.getLoadsFetcher())
                        .dataFetcher("getUsers", graphQLDataFetchers.getUsersFetcher())
                        .dataFetcher("getDrivers", graphQLDataFetchers.getDriversFetcher())
                        .dataFetcher("getTotalRevenue", graphQLDataFetchers.getTotalRevenue())
                        .dataFetcher("getUnpaidLoads", graphQLDataFetchers.getUnpaidLoads())
                        .dataFetcher("getCurrentLoads", graphQLDataFetchers.getCurrentLoads())
                        .dataFetcher("getCompletedLoads", graphQLDataFetchers.getCompletedLoads())
                        .dataFetcher("getCarrierProfile", graphQLDataFetchers.getCarrierProfile())
                )
                .type(TypeRuntimeWiring.newTypeWiring("Mutation")
                        .dataFetcher("addLoad", graphQLDataFetchers.addLoadFetcher())
                        .dataFetcher("addDriver", graphQLDataFetchers.addDriverFetcher())
                        .dataFetcher("addUser", graphQLDataFetchers.addUserFetcher())
                        .dataFetcher("changeDriverStatus", graphQLDataFetchers.changeDriverStatus())
                        .dataFetcher("authenticateUser", graphQLDataFetchers.authenticateUserFetcher())
                        .dataFetcher("authenticateToken", graphQLDataFetchers.authenticateTokenFetcher())
                        .dataFetcher("updateCarrier", graphQLDataFetchers.updateCarrierFetcher())
                        .dataFetcher("updateUser", graphQLDataFetchers.updateUserFetcher())
                        .dataFetcher("updateUserPassword", graphQLDataFetchers.updateUserPasswordFetcher())
                        .dataFetcher("updateLoadStatus", graphQLDataFetchers.updateLoadStatusFetcher())
                        .dataFetcher("updateDriverStatus", graphQLDataFetchers.updateDriverStatusFetcher())
                ).build();
    }
}
