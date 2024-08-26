package com.levandr;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class CrptApi {
    private final int requestLimit;
    private final TimeUnit timeUnit;
    private final HttpClient httpClient;
    private final ScheduledExecutorService scheduler;
    private final ReentrantLock lock;
    private int requestCount;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        this.httpClient = HttpClient.newHttpClient();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.lock = new ReentrantLock();
        this.requestCount = 0;

        this.scheduler.scheduleAtFixedRate(() -> {
            lock.lock();
            try {
                requestCount = 0;
            } finally {
                lock.unlock();
            }
        }, 0, 1, timeUnit);
    }

    public void createDocumentForIntroduction(ProductDocument document, String signature) throws IOException, InterruptedException {
        lock.lock();
        try {
            while (requestCount >= requestLimit) {
                lock.unlock();
                Thread.sleep(100);
                lock.lock();
            }

            requestCount++;

            String requestBody = new Gson().toJson(document);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://ismp.crpt.ru/api/v3/lk/documents/create"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response: " + response.body());

        } finally {
            lock.unlock();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class ProductDocument {
        private String participantInn;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private boolean importRequest;
        private String owner_inn;
        private String producer_inn;
        private String production_date;
        private String production_type;
        private Product[] products;
        private String reg_date;
        private String reg_number;

        @Data
        @Builder
        @AllArgsConstructor
        public static class Product {
            private String certificate_document;
            private String certificate_document_date;
            private String certificate_document_number;
            private String owner_inn;
            private String producer_inn;
            private String production_date;
            private String tnved_code;
            private String uit_code;
            private String uitu_code;
        }
    }
}
