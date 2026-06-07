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
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HotelControllerIntegrationTest {

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

    // ---- GET /hotels ----

    @Test
    void getAllHotels_shouldReturnSeededHotels() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/hotels"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        List<Map> hotels = mapper.readValue(response.body(), List.class);
        assertThat(hotels).hasSizeGreaterThanOrEqualTo(3);
        assertThat(hotels.get(0)).containsKey("id");
        assertThat(hotels.get(0)).containsKey("name");
        assertThat(hotels.get(0)).containsKey("description");
        assertThat(hotels.get(0)).containsKey("address");
        assertThat(hotels.get(0)).containsKey("phone");
    }

    // ---- GET /hotels/{id} ----

    @Test
    void getHotelById_shouldReturnDetailedHotel() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/hotels/1"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        Map<String, Object> body = mapper.readValue(response.body(), Map.class);
        assertThat(body.get("id")).isEqualTo(1);
        assertThat(body.get("name")).isEqualTo("DoubleTree by Hilton Minsk");
        assertThat(body.get("brand")).isEqualTo("Hilton");

        Map<String, Object> address = (Map<String, Object>) body.get("address");
        assertThat(address.get("city")).isEqualTo("Minsk");
        assertThat(address.get("country")).isEqualTo("Belarus");

        Map<String, Object> contacts = (Map<String, Object>) body.get("contacts");
        assertThat(contacts.get("phone")).isEqualTo("+375 17 309-80-00");

        Map<String, Object> arrival = (Map<String, Object>) body.get("arrivalTime");
        assertThat(arrival.get("checkIn")).isEqualTo("14:00");

        List<String> amenities = (List<String>) body.get("amenities");
        assertThat(amenities).hasSize(10);
        assertThat(amenities).contains("Free WiFi");
    }

    @Test
    void getHotelById_shouldReturn404WhenNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/hotels/999"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(404);
    }

    // ---- POST /hotels ----

    @Test
    void createHotel_shouldReturn201() throws Exception {
        String json = """
                {
                    "name": "Integration Test Hotel",
                    "description": "Created by integration test",
                    "brand": "TestBrand"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/hotels"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(201);
        Map<String, Object> body = mapper.readValue(response.body(), Map.class);
        assertThat(body.get("name")).isEqualTo("Integration Test Hotel");
        assertThat(body.get("id")).isNotNull();
    }

    @Test
    void createHotel_shouldReturn400WhenNameMissing() throws Exception {
        String json = """
                {
                    "description": "No name provided"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/hotels"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    void createHotel_withFullNestedObjects_shouldReturn201() throws Exception {
        String json = """
                {
                    "name": "Full Nested Hotel",
                    "description": "With all nested objects",
                    "brand": "LuxBrand",
                    "address": { "houseNumber": 42, "street": "Luxury Lane", "city": "Paris", "country": "France", "postCode": "75001" },
                    "contacts": { "phone": "+33 1 23 45 67 89", "email": "lux@hotel.fr" },
                    "arrivalTime": { "checkIn": "16:00", "checkOut": "10:00" }
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/hotels"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(201);
        Map<String, Object> body = mapper.readValue(response.body(), Map.class);
        assertThat(body.get("name")).isEqualTo("Full Nested Hotel");
    }

    // ---- POST /hotels/{id}/amenities ----

    @Test
    void addAmenities_shouldAppendAndReturnUpdated() throws Exception {
        String json = """
                ["Sauna", "Rooftop bar"]
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/hotels/2/amenities"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        Map<String, Object> body = mapper.readValue(response.body(), Map.class);
        assertThat(body.get("id")).isEqualTo(2);
        List<String> amenities = (List<String>) body.get("amenities");
        assertThat(amenities).contains("Sauna", "Rooftop bar", "Free WiFi");
    }

    @Test
    void addAmenities_shouldReturn404WhenHotelNotFound() throws Exception {
        String json = """
                ["WiFi"]
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/hotels/999/amenities"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(404);
    }
}
