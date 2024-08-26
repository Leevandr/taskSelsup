package com.levandr;

import java.util.concurrent.TimeUnit;

public class TestCrptApi {

    public static void main(String[] args) {
        CrptApi api = new CrptApi(TimeUnit.MINUTES, 5);

        CrptApi.ProductDocument.Product product = CrptApi.ProductDocument.Product.builder()
                .certificate_document("Test Certificate")
                .certificate_document_date("2024-01-01")
                .certificate_document_number("123456")
                .owner_inn("1234567890")
                .producer_inn("0987654321")
                .production_date("2024-01-01")
                .tnved_code("12345678")
                .uit_code("12345678901234")
                .uitu_code("12345678901234")
                .build();

        CrptApi.ProductDocument document = CrptApi.ProductDocument.builder()
                .participantInn("1234567890")
                .doc_id("doc123")
                .doc_status("NEW")
                .doc_type("LP_INTRODUCE_GOODS")
                .importRequest(true)
                .owner_inn("1234567890")
                .producer_inn("0987654321")
                .production_date("2024-01-01")
                .production_type("TEST")
                .products(new CrptApi.ProductDocument.Product[]{product})
                .reg_date("2024-01-01")
                .reg_number("reg123")
                .build();

        String signature = "TestSignature";

        try {
            api.createDocumentForIntroduction(document, signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
