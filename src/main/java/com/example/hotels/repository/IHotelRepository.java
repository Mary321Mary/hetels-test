package com.example.hotels.repository;

import com.example.hotels.entity.Hotel;

import java.util.List;
import java.util.Optional;

/**
 * Common repository interface that abstracts over JPA and MongoDB implementations.
 * The service layer depends on this interface, enabling database switching via Spring profiles.
 */
public interface IHotelRepository {

    List<Hotel> findAll();

    Optional<Hotel> findByIdWithAmenities(Long id);

    List<Hotel> searchByFilters(String name, String brand, String city, String country);

    Hotel save(Hotel hotel);
}
