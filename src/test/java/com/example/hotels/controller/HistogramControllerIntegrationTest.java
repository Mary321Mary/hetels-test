package com.example.hotels.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HistogramControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private HttpClient client;
    private ObjectMapper mapper;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        client = HttpClient.newHttpClient();
        mapper = new ObjectMapper();
        baseUrl = "http://localhost:" + port + "/property-view";
    }

    @Test
    void histogramByBrand_shouldReturnCounts() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/histogram/brand"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        Map<String, Object> body = mapper.readValue(response.body(), Map.class);
        assertThat(body.get("Hilton")).isEqualTo(1);
        assertThat(body.get("Marriott")).isEqualTo(1);
        assertThat(body.get("Europe Hotels")).isEqualTo(1);
    }

    @Test
    void histogramByCity_shouldReturnCounts() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/histogram/city"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        Map<String, Object> body = mapper.readValue(response.body(), Map.class);
        assertThat(body.get("Minsk")).isEqualTo(3);
    }

    @Test
    void histogramByCountry_shouldReturnCounts() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/histogram/country"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        Map<String, Object> body = mapper.readValue(response.body(), Map.class);
        assertThat(body.get("Belarus")).isEqualTo(3);
    }

    @Test
    void histogramByAmenities_shouldReturnCounts() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/histogram/amenities"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        Map<String, Object> body = mapper.readValue(response.body(), Map.class);
        assertThat(body.get("Free WiFi")).isEqualTo(3);
        assertThat(body.get("Free parking")).isEqualTo(1);
        assertThat(body.get("Spa")).isEqualTo(1);
        assertThat(body.get("Bar")).isEqualTo(1);
        assertThat(body.get("Fitness center")).isEqualTo(2);
    }

    @Test
    void histogramWithInvalidParam_shouldReturn400() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/histogram/invalid_param"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    void histogramByAmenities_shouldHaveAllExpectedKeys() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/histogram/amenities"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        Map<String, Object> body = mapper.readValue(response.body(), Map.class);
        assertThat(body).containsKeys(
                "Free parking", "Free WiFi", "Non-smoking rooms", "Concierge",
                "On-site restaurant", "Fitness center", "Pet-friendly rooms",
                "Room service", "Business center", "Meeting rooms", "Spa", "Bar"
        );
    }
}
