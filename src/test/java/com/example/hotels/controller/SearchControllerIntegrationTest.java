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
class SearchControllerIntegrationTest {

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
    void searchByName_shouldReturnMatchingHotels() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/search?name=Hilton"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        List<Map> hotels = mapper.readValue(response.body(), List.class);
        assertThat(hotels).hasSize(1);
        assertThat((String) hotels.get(0).get("name")).contains("Hilton");
    }

    @Test
    void searchByBrand_shouldReturnMatchingHotels() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/search?brand=Marriott"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        List<Map> hotels = mapper.readValue(response.body(), List.class);
        assertThat(hotels).hasSize(1);
        assertThat((String) hotels.get(0).get("name")).contains("Marriott");
    }

    @Test
    void searchByCity_shouldReturnAllMinskHotels() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/search?city=Minsk"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        List<Map> hotels = mapper.readValue(response.body(), List.class);
        assertThat(hotels).hasSize(3);
    }

    @Test
    void searchByCountry_shouldReturnAllBelarusHotels() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/search?country=Belarus"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        List<Map> hotels = mapper.readValue(response.body(), List.class);
        assertThat(hotels).hasSize(3);
    }

    @Test
    void searchByAmenities_shouldFilterCorrectly() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/search?amenities=Free%20parking"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        List<Map> hotels = mapper.readValue(response.body(), List.class);
        assertThat(hotels).hasSize(1);
        assertThat((String) hotels.get(0).get("name")).contains("DoubleTree");
    }

    @Test
    void searchWithMultipleFilters_shouldApplyAndLogic() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/search?brand=Hilton&city=Minsk"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        List<Map> hotels = mapper.readValue(response.body(), List.class);
        assertThat(hotels).hasSize(1);
    }

    @Test
    void searchWithNoFilters_shouldReturnAll() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/search"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        List<Map> hotels = mapper.readValue(response.body(), List.class);
        assertThat(hotels).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void searchWithNonExistentBrand_shouldReturnEmpty() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/search?brand=NonExistentBrandXYZ"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        List<Map> hotels = mapper.readValue(response.body(), List.class);
        assertThat(hotels).isEmpty();
    }

    @Test
    void searchCaseInsensitive_shouldWork() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/search?name=hilton"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        List<Map> hotels = mapper.readValue(response.body(), List.class);
        assertThat(hotels).hasSize(1);
    }
}
