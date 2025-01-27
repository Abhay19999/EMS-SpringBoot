package io.org.reactivestax.repository;


import io.org.reactivestax.dto.OtpDTO;
import io.org.reactivestax.dto.OtpVerificationDTO;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class IntegrationWithRestAssuredTest {

    private static final String BASE_URL = "http://localhost:8080/api/v1/otp";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        try {
            given()
                    .baseUri("http://localhost")
                    .port(8080)
                    .when()
                    .get("/api/v1/otp/status/1")
                    .then()
                    .statusCode(200); // Check if endpoint is accessible
        } catch (Exception e) {
            throw new IllegalStateException("The Spring Boot application is not running or the endpoint is unavailable",
                    e);
        }
    }

    @Test
    void testSmsEndpointWithRestAssured() {
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setClientId(100L);
        otpDTO.setEmail("abhaynimavat2410@gmail.com");
        otpDTO.setMobileNumber("6478181379");
        Response response = given()
                .log().all()
                .contentType("application/json")
                .body(otpDTO)
                .when()
                .post(BASE_URL + "/sms")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();
        String apiResponse = response.asString();
        assertThat(apiResponse).isNotNull();
    }

    @Test
    void testEmailEndPointWithRestAssured() {
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setClientId(100L);
        otpDTO.setEmail("abhaynimavat2410@gmail.com");
        otpDTO.setMobileNumber("6478181379");
        Response response = given()
                .log().all()
                .contentType("application/json")
                .body(otpDTO)
                .when()
                .post(BASE_URL + "/email")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();
        String apiResponse = response.asString();
        assertThat(apiResponse).isNotNull();
    }
    @Test
    void testCallEndPointWithRestAssured() {
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setClientId(100L);
        otpDTO.setEmail("abhaynimavat2410@gmail.com");
        otpDTO.setMobileNumber("6478181379");
        Response response = given()
                .log().all()
                .contentType("application/json")
                .body(otpDTO)
                .when()
                .post(BASE_URL + "/call")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();
        String apiResponse = response.asString();
        assertThat(apiResponse).isNotNull();
    }
    @Test
    void testVerifyEndPointWithRestAssured() {
        OtpVerificationDTO otpVerificationDTO = new OtpVerificationDTO();
        otpVerificationDTO.setClientId(100L);
        otpVerificationDTO.setOtp(123456L);

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body(otpVerificationDTO)
                .when()
                .put(BASE_URL + "/verify")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();
        String apiResponse = response.asString();
        assertThat(apiResponse).isNotNull();
    }

}
