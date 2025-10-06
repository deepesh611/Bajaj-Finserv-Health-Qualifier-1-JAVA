package com.bajaj.service;

import com.bajaj.dto.SolutionRequest;
import com.bajaj.dto.WebhookRequest;
import com.bajaj.dto.WebhookResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {

    private final RestTemplate restTemplate;
    private static final String GENERATE_WEBHOOK_URL =
            "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String SUBMIT_WEBHOOK_URL =
            "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    private static final String SQL_QUERY =
            "SELECT p.AMOUNT AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
                    "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, d.DEPARTMENT_NAME " +
                    "FROM PAYMENTS p JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                    "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                    "WHERE DAY(p.PAYMENT_TIME) != 1 ORDER BY p.AMOUNT DESC LIMIT 1";

    public WebhookService() {
        this.restTemplate = new RestTemplate();
    }

    public void executeWorkflow() {
        try {
            // Step 1: Generate Webhook
            System.out.println("Step 1: Generating webhook...");
            WebhookResponse webhookResponse = generateWebhook();

            if (webhookResponse == null || webhookResponse.getAccessToken() == null) {
                System.err.println("Failed to generate webhook. Response is null.");
                return;
            }

            System.out.println("Webhook generated successfully!");
            System.out.println("Webhook URL: " + webhookResponse.getWebhook());
            System.out.println("Access Token: " + webhookResponse.getAccessToken());

            // Step 2: Submit Solution
            System.out.println("\nStep 2: Submitting solution...");
            submitSolution(webhookResponse.getAccessToken());

            System.out.println("Solution submitted successfully!");

        } catch (Exception e) {
            System.err.println("Error in workflow execution: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private WebhookResponse generateWebhook() {
        WebhookRequest request = new WebhookRequest(
                "Deepesh Patil",
                "112215055",
                "112215055@cse.iiitp.ac.in"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                GENERATE_WEBHOOK_URL,
                entity,
                WebhookResponse.class
        );

        return response.getBody();
    }

    private void submitSolution(String accessToken) {
        SolutionRequest request = new SolutionRequest(SQL_QUERY);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);

        HttpEntity<SolutionRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                SUBMIT_WEBHOOK_URL,
                entity,
                String.class
        );

        System.out.println("Response from submission: " + response.getBody());
    }
}