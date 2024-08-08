package com.github.gluhov.fakepaymentprovider.it;

import com.github.gluhov.fakepaymentprovider.config.PostgreSqlTestContainerConfig;
import com.github.gluhov.fakepaymentprovider.rest.PaymentTopUpRestControllerV1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Base64;

import static com.github.gluhov.fakepaymentprovider.service.MerchantData.merchant;
import static com.github.gluhov.fakepaymentprovider.service.TransactionData.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import({PostgreSqlTestContainerConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
public class ItTopUpRestControllerV1Test extends AbstractRestControllerTest {

    private static final String REST_URL = PaymentTopUpRestControllerV1.REST_URL;

    @Autowired
    private WebTestClient webTestClient;

    private String token;

    @BeforeEach
    public void setUp() {
        if (token == null) {
            token = getToken(merchant.getMerchantId(), merchant.getSecretKey());
        }
    }

    private String getToken(String merchantId, String secretKey) {
        String credentials = merchantId + ":" + secretKey;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    @Test
    @DisplayName("Test topup get info functionality")
    public void givenFieldId_whenGetTopup_thenSuccessResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(REST_URL + "/" + TRANSACTION_UUID + "/details")
                .headers(headers -> headers.setBasicAuth(token))
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.body.transaction_id").isEqualTo(transaction.getId().toString())
                .jsonPath("$.body.notification_url").isEqualTo(transaction.getNotificationUrl())
                .jsonPath("$.body.amount").isEqualTo(transaction.getAmount())
                .jsonPath("$.body.currency").isEqualTo(transaction.getCurrency());
    }

    @Test
    @DisplayName("Test get not found id topup info functionality")
    public void givenFieldId_whenGetTopup_thenNotFoundResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(REST_URL + "/" + TRANSACTION_NOT_FOUND_UUID + "/details")
                .headers(headers -> headers.setBasicAuth(token))
                .exchange();

        resp.expectStatus().isNotFound()
                .expectBody()
                .consumeWith(System.out::println);
    }

    @Test
    @DisplayName("Test get between topup info functionality")
    public void givenStartDateEndDate_whenGetBetweenTopup_thenSuccessResponse() {
        long startDate = LocalDate.now().minusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endDate = LocalDate.now().minusDays(1).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(REST_URL + "/list?start_date=" + startDate + "&end_date=" + endDate)
                .headers(headers -> headers.setBasicAuth(token))
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.body.*").isArray()
                .jsonPath("$.body.transaction_list[0].transaction_id").isEqualTo(TRANSACTION_UUID.toString());
    }

    @Test
    @DisplayName("Test get between not found topup info functionality")
    public void givenStartDateEndDate_whenGetBetweenNotFoundTopup_thenSuccessResponse() {
        long startDate = LocalDate.now().minusDays(3).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endDate = LocalDate.now().minusDays(3).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(REST_URL + "/list?start_date=" + startDate + "&end_date=" + endDate)
                .headers(headers -> headers.setBasicAuth(token))
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.body.transaction_list.*").isEmpty();
    }

    @Test
    @DisplayName("Test get between topup info with default date functionality")
    public void givenNoDate_whenGetBetweenTopup_thenSuccessResponse() {
        WebTestClient.ResponseSpec resp = webTestClient.get()
                .uri(REST_URL + "/list")
                .headers(headers -> headers.setBasicAuth(token))
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.body.transaction_list.*").isArray();
    }

    @Test
    @DisplayName("Test create transaction functionality")
    public void givenTransaction_whenCreateTopupWithNewCustomer_thenSuccessResponse() {
        String json = "{\n" +
                "    \"payment_method\": {\n" +
                "        \"type\": \"CARD\"\n" +
                "    },\n" +
                "    \"amount\": \"1000\",\n" +
                "    \"currency\": \"USD\",\n" +
                "    \"card_data\": {\n" +
                "        \"card_number\": \"4102778822334893\",\n" +
                "        \"exp_date\": \"11/23\",\n" +
                "        \"cvv\": \"566\"\n" +
                "    },\n" +
                "    \"language\": \"en\",\n" +
                "    \"notification_url\": \"https://proselyte.net/webhook/transaction\",\n" +
                "    \"customer\": {\n" +
                "        \"first_name\": \"John\",\n" +
                "        \"last_name\": \"Doe\",\n" +
                "        \"country\": \"BR\"\n" +
                "    }\n" +
                "}";

        WebTestClient.ResponseSpec resp = webTestClient.post()
                .uri(REST_URL)
                .headers(headers -> headers.setBasicAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.transaction_id").exists()
                .jsonPath("$.transaction_id").isNotEmpty();
    }

    @Test
    @DisplayName("Test create transaction functionality")
    public void givenTransaction_whenCreateTopup_thenSuccessResponse() {
        String json = "{\n" +
                "    \"payment_method\": {\n" +
                "        \"type\": \"CARD\"\n" +
                "    },\n" +
                "    \"amount\": \"350\",\n" +
                "    \"currency\": \"USD\",\n" +
                "    \"card_data\": {\n" +
                "        \"card_number\": \"1234567812345678\",\n" +
                "        \"exp_date\": \"12/25\",\n" +
                "        \"cvv\": \"123\"\n" +
                "    },\n" +
                "    \"language\": \"en\",\n" +
                "    \"notification_url\": \"https://proselyte.net/webhook/transaction\",\n" +
                "    \"customer\": {\n" +
                "        \"first_name\": \"John\",\n" +
                "        \"last_name\": \"Doel\",\n" +
                "        \"country\": \"BRL\"\n" +
                "    }\n" +
                "}";

        WebTestClient.ResponseSpec resp = webTestClient.post()
                .uri(REST_URL)
                .headers(headers -> headers.setBasicAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange();

        resp.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.transaction_id").exists()
                .jsonPath("$.transaction_id").isNotEmpty();
    }
}