package com.example.hotels.service;

import com.example.hotels.dto.CreateHotelRequest;
import com.example.hotels.dto.HotelDetailDto;
import com.example.hotels.dto.HotelSummaryDto;
import com.example.hotels.entity.Address;
import com.example.hotels.entity.ArrivalTime;
import com.example.hotels.entity.Contacts;
import com.example.hotels.entity.Hotel;
import com.example.hotels.repository.IHotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private IHotelRepository hotelRepository;

    @InjectMocks
    private HotelService hotelService;

    private Hotel sampleHotel;

    @BeforeEach
    void setUp() {
        sampleHotel = new Hotel("Test Hotel", "A test hotel", "TestBrand",
                new Address(1, "Main St", "TestCity", "TestCountry", "12345"),
                new Contacts("+1234567890", "test@test.com"),
                new ArrivalTime("14:00", "12:00"));
        sampleHotel.setId(1L);
        sampleHotel.setAmenities(new ArrayList<>(List.of("Free WiFi", "Pool")));
    }

    // ---- getAllHotels ----

    @Test
    void getAllHotels_shouldReturnAllHotels() {
        when(hotelRepository.findAll()).thenReturn(List.of(sampleHotel));

        List<HotelSummaryDto> result = hotelService.getAllHotels();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Hotel");
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void getAllHotels_shouldReturnEmptyListWhenNoHotels() {
        when(hotelRepository.findAll()).thenReturn(Collections.emptyList());

        List<HotelSummaryDto> result = hotelService.getAllHotels();

        assertThat(result).isEmpty();
    }

    // ---- getHotelById ----

    @Test
    void getHotelById_shouldReturnHotelDetailDto() {
        when(hotelRepository.findByIdWithAmenities(1L)).thenReturn(Optional.of(sampleHotel));

        HotelDetailDto result = hotelService.getHotelById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Hotel");
        assertThat(result.getBrand()).isEqualTo("TestBrand");
        assertThat(result.getAddress()).isNotNull();
        assertThat(result.getAddress().getCity()).isEqualTo("TestCity");
        assertThat(result.getContacts()).isNotNull();
        assertThat(result.getContacts().getPhone()).isEqualTo("+1234567890");
        assertThat(result.getArrivalTime()).isNotNull();
        assertThat(result.getArrivalTime().getCheckIn()).isEqualTo("14:00");
        assertThat(result.getAmenities()).containsExactly("Free WiFi", "Pool");
    }

    @Test
    void getHotelById_shouldThrow404WhenNotFound() {
        when(hotelRepository.findByIdWithAmenities(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.getHotelById(999L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Hotel not found");
    }

    // ---- createHotel ----

    @Test
    void createHotel_shouldCreateAndReturnSummary() {
        CreateHotelRequest request = new CreateHotelRequest();
        request.setName("New Hotel");
        request.setDescription("Description");
        request.setBrand("NewBrand");

        Hotel savedHotel = new Hotel("New Hotel", "Description", "NewBrand", null, null, null);
        savedHotel.setId(2L);
        when(hotelRepository.save(any(Hotel.class))).thenReturn(savedHotel);

        HotelSummaryDto result = hotelService.createHotel(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Hotel");
        assertThat(result.getId()).isEqualTo(2L);
        verify(hotelRepository).save(any(Hotel.class));
    }

    @Test
    void createHotel_shouldThrow400WhenNameIsNull() {
        CreateHotelRequest request = new CreateHotelRequest();

        assertThatThrownBy(() -> hotelService.createHotel(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("name is required");
    }

    @Test
    void createHotel_shouldThrow400WhenNameIsBlank() {
        CreateHotelRequest request = new CreateHotelRequest();
        request.setName("   ");

        assertThatThrownBy(() -> hotelService.createHotel(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("name is required");
    }

    @Test
    void createHotel_withNestedObjects() {
        CreateHotelRequest request = new CreateHotelRequest();
        request.setName("Full Hotel");
        request.setAddress(new CreateHotelRequest.AddressRequest());
        request.getAddress().setHouseNumber(10);
        request.getAddress().setStreet("Oak Ave");
        request.getAddress().setCity("Springfield");
        request.getAddress().setCountry("USA");
        request.getAddress().setPostCode("62704");
        request.setContacts(new CreateHotelRequest.ContactsRequest());
        request.getContacts().setPhone("+1-555");
        request.getContacts().setEmail("hotel@test.com");
        request.setArrivalTime(new CreateHotelRequest.ArrivalTimeRequest());
        request.getArrivalTime().setCheckIn("15:00");
        request.getArrivalTime().setCheckOut("11:00");

        Hotel savedHotel = new Hotel("Full Hotel", null, null,
                new Address(10, "Oak Ave", "Springfield", "USA", "62704"),
                new Contacts("+1-555", "hotel@test.com"),
                new ArrivalTime("15:00", "11:00"));
        savedHotel.setId(3L);
        when(hotelRepository.save(any(Hotel.class))).thenReturn(savedHotel);

        HotelSummaryDto result = hotelService.createHotel(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Full Hotel");
        verify(hotelRepository).save(any(Hotel.class));
    }

    // ---- addAmenities ----

    @Test
    void addAmenities_shouldAppendNewAmenities() {
        when(hotelRepository.findByIdWithAmenities(1L)).thenReturn(Optional.of(sampleHotel));
        when(hotelRepository.save(any(Hotel.class))).thenAnswer(inv -> inv.getArgument(0));

        HotelDetailDto result = hotelService.addAmenities(1L, List.of("Spa", "Gym"));

        assertThat(result.getAmenities()).contains("Free WiFi", "Pool", "Spa", "Gym");
        assertThat(result.getAmenities()).hasSize(4);
    }

    @Test
    void addAmenities_shouldNotDuplicateExisting() {
        when(hotelRepository.findByIdWithAmenities(1L)).thenReturn(Optional.of(sampleHotel));
        when(hotelRepository.save(any(Hotel.class))).thenAnswer(inv -> inv.getArgument(0));

        HotelDetailDto result = hotelService.addAmenities(1L, List.of("Free WiFi", "New Amenity"));

        assertThat(result.getAmenities()).hasSize(3);
        assertThat(result.getAmenities()).contains("Free WiFi", "Pool", "New Amenity");
    }

    @Test
    void addAmenities_shouldBeCaseInsensitive() {
        when(hotelRepository.findByIdWithAmenities(1L)).thenReturn(Optional.of(sampleHotel));
        when(hotelRepository.save(any(Hotel.class))).thenAnswer(inv -> inv.getArgument(0));

        HotelDetailDto result = hotelService.addAmenities(1L, List.of("free wifi", "FREE WIFI"));

        assertThat(result.getAmenities()).hasSize(2);
        assertThat(result.getAmenities()).containsExactly("Free WiFi", "Pool");
    }

    @Test
    void addAmenities_shouldThrow404WhenHotelNotFound() {
        when(hotelRepository.findByIdWithAmenities(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.addAmenities(999L, List.of("WiFi")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Hotel not found");
    }

    // ---- searchHotels ----

    @Test
    void searchHotels_shouldReturnMatchingHotels() {
        when(hotelRepository.searchByFilters("Test", null, null, null))
                .thenReturn(List.of(sampleHotel));

        List<HotelSummaryDto> result = hotelService.searchHotels("Test", null, null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Hotel");
    }

    @Test
    void searchHotels_shouldFilterByAmenities() {
        when(hotelRepository.searchByFilters(null, null, null, null))
                .thenReturn(List.of(sampleHotel));

        List<HotelSummaryDto> result = hotelService.searchHotels(null, null, null, null, List.of("Free WiFi"));

        assertThat(result).hasSize(1);
    }

    @Test
    void searchHotels_shouldExcludeHotelWithoutRequiredAmenity() {
        when(hotelRepository.searchByFilters(null, null, null, null))
                .thenReturn(List.of(sampleHotel));

        List<HotelSummaryDto> result = hotelService.searchHotels(null, null, null, null, List.of("Sauna"));

        assertThat(result).isEmpty();
    }

    // ---- getHistogram ----

    @Test
    void getHistogram_byBrand() {
        Hotel h2 = new Hotel("Hotel 2", null, "Brand B", null, null, null);
        h2.setId(2L);
        Hotel h3 = new Hotel("Hotel 3", null, "TestBrand", null, null, null);
        h3.setId(3L);

        when(hotelRepository.findAll()).thenReturn(List.of(sampleHotel, h2, h3));

        Map<String, Long> result = hotelService.getHistogram("brand");

        assertThat(result).containsEntry("TestBrand", 2L);
        assertThat(result).containsEntry("Brand B", 1L);
    }

    @Test
    void getHistogram_byCity() {
        when(hotelRepository.findAll()).thenReturn(List.of(sampleHotel));

        Map<String, Long> result = hotelService.getHistogram("city");

        assertThat(result).containsEntry("TestCity", 1L);
    }

    @Test
    void getHistogram_byCountry() {
        when(hotelRepository.findAll()).thenReturn(List.of(sampleHotel));

        Map<String, Long> result = hotelService.getHistogram("country");

        assertThat(result).containsEntry("TestCountry", 1L);
    }

    @Test
    void getHistogram_byAmenities() {
        Hotel h2 = new Hotel("Hotel 2", null, null, null, null, null);
        h2.setId(2L);
        h2.setAmenities(new ArrayList<>(List.of("Free WiFi", "Spa")));

        when(hotelRepository.findAll()).thenReturn(List.of(sampleHotel, h2));

        Map<String, Long> result = hotelService.getHistogram("amenities");

        assertThat(result).containsEntry("Free WiFi", 2L);
        assertThat(result).containsEntry("Pool", 1L);
        assertThat(result).containsEntry("Spa", 1L);
    }

    @Test
    void getHistogram_invalidParam_shouldThrow400() {
        assertThatThrownBy(() -> hotelService.getHistogram("invalid"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid parameter");
    }

    @Test
    void getHistogram_blankParam_shouldThrow400() {
        assertThatThrownBy(() -> hotelService.getHistogram(""))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Parameter is required");
    }
}
