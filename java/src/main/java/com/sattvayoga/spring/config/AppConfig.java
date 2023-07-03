package com.sattvayoga.spring.config;

import javax.sql.DataSource;

import com.sattvayoga.dao.EventDao;
import software.amazon.awssdk.regions.Region;

import com.sattvayoga.dao.JdbcEventDao;
import com.sattvayoga.dao.SecretManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

//This is a Java Spring configuration class that defines a DataSource bean and an EventDao bean
// for a Spring application. The purpose of the code is to configure a database connection
// for the application and set up a bean for accessing and manipulating data in the database.


//@Configuration: This annotation indicates that this class is a Spring
// configuration class and that it will define beans for the Spring application context.
@Configuration

//@ComponentScan("com.sattvayoga.spring"): This annotation specifies the base package(s)
// for component scanning in the Spring application context. Spring will scan this package
// and its sub-packages for any Spring-managed components that need to be registered as beans.
@ComponentScan("com.sattvayoga.spring")

//@PropertySource("classpath:database.properties"): This annotation tells Spring to look
// for a properties file named "database.properties" in the classpath
// and load its contents into the Spring Environment.
// The properties in this file will be used to configure the database connection
@PropertySource("classpath:database.properties")
public class AppConfig {

//    @Autowired Environment environment: This field is annotated with @Autowired to tell
//    Spring to inject the Environment object into this class. The Environment object is used
//    to access properties loaded from the "database.properties" file.
    @Autowired
    Environment environment;

//private final String URL = "url";, private final String USER = "dbuser";,
// private final String DRIVER = "driver";, and private final String PASSWORD = "dbpassword";:
// These constants are used to define the keys for the properties in the "database.properties" file.
    private final String URL = "url";
    private final String USER = "dbuser";
    private final String DRIVER = "driver";
    private final String PASSWORD = "dbpassword";

//@Bean DataSource dataSource(): This method is annotated with
// @Bean to tell Spring that it should be called to create a new bean of
// type DataSource. The method creates a DriverManagerDataSource object,
// sets its URL, username, and password properties using values
// from the environment field, and returns it.
    @Bean
    DataSource dataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setUrl(environment.getProperty(URL));
        driverManagerDataSource.setUsername(environment.getProperty(USER));
        driverManagerDataSource.setPassword(environment.getProperty(PASSWORD));
//      driverManagerDataSource.setDriverClassName(environment.getProperty(DRIVER));
        return driverManagerDataSource;
    }

//@Bean public EventDao getEventDao(): This method is annotated with @Bean to tell
// Spring that it should be called to create a new bean of type EventDao.
// The method creates a new JdbcEventDao object and passes it the DataSource
// bean created by the dataSource() method.
    @Bean
    public EventDao getEventDao() {
        return new JdbcEventDao(dataSource());
    }

    @Bean
    public SecretsManagerClient secretManagerClient() {
        // Create and configure the SecretManagerClient instance
        return SecretsManagerClient.builder()
                .region(Region.of("us-east-2"))
                .build();
    }

    @Bean
    public SecretManagerService customSecretManagerService(SecretsManagerClient secretsManagerClient) {
        return new SecretManagerService(secretsManagerClient);
    }
}
