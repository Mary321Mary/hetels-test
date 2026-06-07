package com.example.hotels.controller;

import com.example.hotels.dto.CreateHotelRequest;
import com.example.hotels.dto.HotelDetailDto;
import com.example.hotels.dto.HotelSummaryDto;
import com.example.hotels.service.HotelService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public List<HotelSummaryDto> getAllHotels() {
        return hotelService.getAllHotels();
    }

    @GetMapping("/{id}")
    public HotelDetailDto getHotelById(@PathVariable Long id) {
        return hotelService.getHotelById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HotelSummaryDto createHotel(@RequestBody CreateHotelRequest request) {
        return hotelService.createHotel(request);
    }

    @PostMapping("/{id}/amenities")
    public HotelDetailDto addAmenities(@PathVariable Long id, @RequestBody List<String> amenities) {
        return hotelService.addAmenities(id, amenities);
    }
}
