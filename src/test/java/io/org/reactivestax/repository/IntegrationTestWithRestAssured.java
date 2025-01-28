package io.org.reactivestax.repository;


import io.org.reactivestax.dto.OtpDTO;
import io.org.reactivestax.dto.OtpVerificationDTO;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationWithRestAssuredTest {


    @LocalServerPort
    private  int port;

    private  String baseUrl;

    @BeforeAll
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
       baseUrl = "http://localhost:"+port+"/api/v1/otp";

        try {
            given()
                    .baseUri("http://localhost")
                    .port(port)
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
        Response response = getResponse("sms");
        String apiResponse = response.asString();
        assertThat(apiResponse).isNotNull();
    }
    @Test
    void testEmailEndPointWithRestAssured() {
        Response response = getResponse("email");
        String apiResponse = response.asString();
        assertThat(apiResponse).isNotNull();
    }
    @Test
    void testCallEndPointWithRestAssured() {
        Response response = getResponse("call");
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
                .put(baseUrl + "/verify")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();
        String apiResponse = response.asString();
        assertThat(apiResponse).isNotNull();
        assertEquals("Client does not exists", apiResponse);
    }
    private Response getResponse(String contactMethod) {
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setClientId(100L);
        otpDTO.setEmail("abhaynimavat2410@gmail.com");
        otpDTO.setMobileNumber("6478181379");
        return given()
                .log().all()
                .contentType("application/json")
                .body(otpDTO)
                .when()
                .post(baseUrl + "/"+contactMethod)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();
    }
}
