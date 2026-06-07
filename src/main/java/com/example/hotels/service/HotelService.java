package com.example.hotels.service;

import com.example.hotels.dto.CreateHotelRequest;
import com.example.hotels.dto.HotelDetailDto;
import com.example.hotels.dto.HotelSummaryDto;
import com.example.hotels.entity.Address;
import com.example.hotels.entity.ArrivalTime;
import com.example.hotels.entity.Contacts;
import com.example.hotels.entity.Hotel;
import com.example.hotels.repository.IHotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class HotelService {

    private final IHotelRepository hotelRepository;

    public HotelService(IHotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public List<HotelSummaryDto> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::toSummaryDto)
                .toList();
    }

    public HotelDetailDto getHotelById(Long id) {
        Hotel hotel = hotelRepository.findByIdWithAmenities(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found with id: " + id));
        return toDetailDto(hotel);
    }

    public List<HotelSummaryDto> searchHotels(String name, String brand, String city, String country, List<String> amenities) {
        String nameParam = (name != null && !name.isBlank()) ? name.trim() : null;
        String brandParam = (brand != null && !brand.isBlank()) ? brand.trim() : null;
        String cityParam = (city != null && !city.isBlank()) ? city.trim() : null;
        String countryParam = (country != null && !country.isBlank()) ? country.trim() : null;

        List<Hotel> hotels = hotelRepository.searchByFilters(nameParam, brandParam, cityParam, countryParam);

        // Post-filter by amenities (AND logic: hotel must contain ALL specified amenities)
        if (amenities != null && !amenities.isEmpty()) {
            List<String> required = amenities.stream()
                    .filter(a -> a != null && !a.isBlank())
                    .map(String::toLowerCase)
                    .toList();
            if (!required.isEmpty()) {
                hotels = hotels.stream()
                        .filter(h -> {
                            List<String> hotelAmenities = h.getAmenities() != null
                                    ? h.getAmenities().stream().map(String::toLowerCase).toList()
                                    : List.of();
                            return hotelAmenities.containsAll(required);
                        })
                        .toList();
            }
        }

        return hotels.stream().map(this::toSummaryDto).toList();
    }

    public HotelSummaryDto createHotel(CreateHotelRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hotel name is required");
        }

        Address address = null;
        if (request.getAddress() != null) {
            CreateHotelRequest.AddressRequest ar = request.getAddress();
            address = new Address(ar.getHouseNumber(), ar.getStreet(), ar.getCity(), ar.getCountry(), ar.getPostCode());
        }

        Contacts contacts = null;
        if (request.getContacts() != null) {
            CreateHotelRequest.ContactsRequest cr = request.getContacts();
            contacts = new Contacts(cr.getPhone(), cr.getEmail());
        }

        ArrivalTime arrivalTime = null;
        if (request.getArrivalTime() != null) {
            CreateHotelRequest.ArrivalTimeRequest atr = request.getArrivalTime();
            arrivalTime = new ArrivalTime(atr.getCheckIn(), atr.getCheckOut());
        }

        Hotel hotel = new Hotel(
                request.getName().trim(),
                request.getDescription(),
                request.getBrand(),
                address,
                contacts,
                arrivalTime
        );

        Hotel saved = hotelRepository.save(hotel);
        return toSummaryDto(saved);
    }

    public HotelDetailDto addAmenities(Long id, List<String> newAmenities) {
        Hotel hotel = hotelRepository.findByIdWithAmenities(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found with id: " + id));

        if (newAmenities != null) {
            List<String> existing = hotel.getAmenities() != null ? hotel.getAmenities() : new ArrayList<>();
            // Add only amenities that are not already present (case-insensitive check)
            List<String> existingLower = existing.stream().map(String::toLowerCase).toList();
            for (String amenity : newAmenities) {
                if (amenity != null && !amenity.isBlank() && !existingLower.contains(amenity.toLowerCase())) {
                    existing.add(amenity);
                }
            }
            hotel.setAmenities(existing);
        }

        Hotel saved = hotelRepository.save(hotel);
        return toDetailDto(saved);
    }

    public Map<String, Long> getHistogram(String param) {
        if (param == null || param.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter is required");
        }

        List<Hotel> hotels = hotelRepository.findAll();

        return switch (param.toLowerCase()) {
            case "brand" -> groupByField(hotels, h -> h.getBrand());
            case "city" -> groupByField(hotels, h -> h.getAddress() != null ? h.getAddress().getCity() : null);
            case "country" -> groupByField(hotels, h -> h.getAddress() != null ? h.getAddress().getCountry() : null);
            case "amenities" -> buildAmenitiesHistogram(hotels);
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid parameter: " + param + ". Supported: brand, city, country, amenities");
        };
    }

    private Map<String, Long> groupByField(List<Hotel> hotels, Function<Hotel, String> extractor) {
        return hotels.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .filter(v -> !v.isBlank())
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()));
    }

    private Map<String, Long> buildAmenitiesHistogram(List<Hotel> hotels) {
        return hotels.stream()
                .filter(h -> h.getAmenities() != null)
                .flatMap(h -> h.getAmenities().stream())
                .filter(a -> a != null && !a.isBlank())
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()));
    }

    private HotelSummaryDto toSummaryDto(Hotel hotel) {
        String addressStr = null;
        if (hotel.getAddress() != null) {
            Address a = hotel.getAddress();
            addressStr = String.format("%s %s, %s, %s, %s",
                    a.getHouseNumber(), a.getStreet(), a.getCity(), a.getPostCode(), a.getCountry());
        }
        String phone = hotel.getContacts() != null ? hotel.getContacts().getPhone() : null;
        return new HotelSummaryDto(hotel.getId(), hotel.getName(), hotel.getDescription(), addressStr, phone);
    }

    private HotelDetailDto toDetailDto(Hotel hotel) {
        HotelDetailDto.AddressDto addressDto = null;
        if (hotel.getAddress() != null) {
            Address a = hotel.getAddress();
            addressDto = new HotelDetailDto.AddressDto(a.getHouseNumber(), a.getStreet(), a.getCity(), a.getCountry(), a.getPostCode());
        }
        HotelDetailDto.ContactsDto contactsDto = null;
        if (hotel.getContacts() != null) {
            Contacts c = hotel.getContacts();
            contactsDto = new HotelDetailDto.ContactsDto(c.getPhone(), c.getEmail());
        }
        HotelDetailDto.ArrivalTimeDto arrivalDto = null;
        if (hotel.getArrivalTime() != null) {
            ArrivalTime at = hotel.getArrivalTime();
            arrivalDto = new HotelDetailDto.ArrivalTimeDto(at.getCheckIn(), at.getCheckOut());
        }
        List<String> amenities = hotel.getAmenities() != null ? new ArrayList<>(hotel.getAmenities()) : new ArrayList<>();
        return new HotelDetailDto(hotel.getId(), hotel.getName(), hotel.getDescription(), hotel.getBrand(),
                addressDto, contactsDto, arrivalDto, amenities);
    }
}
