package com.sattvayoga.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sattvayoga.model.JavaMailCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;


@Service
public class SecretManagerService {
    private final SecretsManagerClient secretsManagerClient;

    @Autowired
    public SecretManagerService(SecretsManagerClient secretsManagerClient) {
        this.secretsManagerClient = secretsManagerClient;
    }

    @Cacheable("apiKeyCache")
    public String getApiKey() throws Throwable {

        String secretName = "mapsKey";
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
            throw e.getCause();
        }

        String secret = getSecretValueResponse.secretString();

        return secret;
    }

    @Cacheable("emailPasswordCache")
    public JavaMailCredentials getEmailPassword() throws Throwable {
        String secretName = "Java-Mail";
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
            throw e.getCause();
        }

        String secret = getSecretValueResponse.secretString();

        return parseEmailCredentialsFromSecret(secret);
    }

    private JavaMailCredentials parseEmailCredentialsFromSecret(String secret) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaMailCredentials emailCredentials = objectMapper.readValue(secret, JavaMailCredentials.class);
        return emailCredentials;
    }
}
