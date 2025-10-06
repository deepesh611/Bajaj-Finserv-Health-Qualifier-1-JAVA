package com.bajaj.config;

import com.bajaj.service.WebhookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    private final WebhookService webhookService;

    public StartupRunner(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("===========================================");
        System.out.println("Starting Bajaj Webhook Application...");
        System.out.println("===========================================\n");

        webhookService.executeWorkflow();

        System.out.println("\n===========================================");
        System.out.println("Workflow completed!");
        System.out.println("===========================================");
    }
}