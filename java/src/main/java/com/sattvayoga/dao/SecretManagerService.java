package com.sattvayoga.dao;



import com.sattvayoga.model.CustomException;
import com.sattvayoga.model.JavaMailCredentials;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.thirdparty.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.sql.SQLOutput;


@Service
public class SecretManagerService {
    private final SecretsManagerClient secretsManagerClient;

    @Autowired
    public SecretManagerService(SecretsManagerClient secretsManagerClient) {
        this.secretsManagerClient = secretsManagerClient;
    }

    @Cacheable("stripeSecretKey")
    public String getStripeKey() throws Throwable {
        String secretName = "SecretStripeKey";
        Region region = Region.of("us-east-2");

        // Create a Secrets Manager client
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse getSecretValueResponse;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            // For a list of exceptions thrown, see
            // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
            System.out.println("Could not retrieve Secret Value Response. Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to retrieve stripe secret");
        }

        String secret = getSecretValueResponse.secretString();

        return secret;
    }

    @Cacheable("webHookSecret")
    public String getWebHookSecret() throws Throwable {
        String secretName = "WebHookSecret";
        Region region = Region.of("us-east-2");

        // Create a Secrets Manager client
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse getSecretValueResponse;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            // For a list of exceptions thrown, see
            // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
            System.out.println("Could not retrieve Secret Value Response. Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to retrieve webhook secret");
        }

        String secret = getSecretValueResponse.secretString();

        return secret;
    }

    public JavaMailCredentials getEmailPassword() {
        JavaMailCredentials javaMailCredentials = new JavaMailCredentials("username", "password");
        return javaMailCredentials;

    }

    //TODO: if you want to deploy, uncomment below and comment out the method above with same name
//    @Cacheable("emailPasswordCache")
//    public JavaMailCredentials getEmailPassword() throws Throwable {
//        String secretName = "Java-Mail";
//        Region region = Region.of("us-east-2");
//
//        // Create a Secrets Manager client
//        SecretsManagerClient client = SecretsManagerClient.builder()
//                .region(region)
//                .build();
//
//        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
//                .secretId(secretName)
//                .build();
//
//        GetSecretValueResponse getSecretValueResponse;
//
//        try {
//            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
//        } catch (Exception e) {
//            // For a list of exceptions thrown, see
//            // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
//            System.out.println("Could not retrieve Secret Value Response. Error message: " + e.getMessage());
//            System.out.println("Cause: " + e.getCause());
//            throw new CustomException("Failed to retrieve credentials for email service");
//        }
//
//        String secret = getSecretValueResponse.secretString();
//
//        return parseEmailCredentialsFromSecret(secret);
//    }

    private JavaMailCredentials parseEmailCredentialsFromSecret(String secret)  {

        ObjectMapper objectMapper = new ObjectMapper();
        JavaMailCredentials emailCredentials = null;
        try {
            emailCredentials = objectMapper.readValue(secret, JavaMailCredentials.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return emailCredentials;
    }

 //   @Cacheable("apiKeyCache")
//    public String getApiKey() throws Throwable {
//
//        String secretName = "mapsKey";
//        Region region = Region.of("us-east-2");
//
//        // Create a Secrets Manager client
//        SecretsManagerClient client = SecretsManagerClient.builder()
//                .region(region)
//                .build();
//
//        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
//                .secretId(secretName)
//                .build();
//
//        GetSecretValueResponse getSecretValueResponse;
//
//        try {
//            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
//        } catch (Exception e) {
//            // For a list of exceptions thrown, see
//            // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
//            throw e.getCause();
//        }
//
//        String secret = getSecretValueResponse.secretString();
//
//        return secret;
//    }
}
