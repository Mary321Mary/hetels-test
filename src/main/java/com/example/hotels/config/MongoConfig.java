package com.example.hotels.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * MongoDB connection configuration.
 * Active only when the 'mongodb' profile is enabled.
 */
@Configuration
@Profile("mongodb")
public class MongoConfig {

    @Value("${mongodb.uri:mongodb://localhost:27017}")
    private String mongoUri;

    @Value("${mongodb.database:hotelsdb}")
    private String databaseName;

    @Bean(destroyMethod = "close")
    public MongoClient mongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .build();
        return MongoClients.create(settings);
    }

    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(databaseName);
    }
}
