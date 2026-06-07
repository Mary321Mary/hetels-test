package com.example.hotels.dto;

import java.util.List;

public class HotelDetailDto {

    private Long id;
    private String name;
    private String description;
    private String brand;
    private AddressDto address;
    private ContactsDto contacts;
    private ArrivalTimeDto arrivalTime;
    private List<String> amenities;

    public HotelDetailDto() {
    }

    public HotelDetailDto(Long id, String name, String description, String brand,
                          AddressDto address, ContactsDto contacts,
                          ArrivalTimeDto arrivalTime, List<String> amenities) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.address = address;
        this.contacts = contacts;
        this.arrivalTime = arrivalTime;
        this.amenities = amenities;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public AddressDto getAddress() { return address; }
    public void setAddress(AddressDto address) { this.address = address; }

    public ContactsDto getContacts() { return contacts; }
    public void setContacts(ContactsDto contacts) { this.contacts = contacts; }

    public ArrivalTimeDto getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(ArrivalTimeDto arrivalTime) { this.arrivalTime = arrivalTime; }

    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }

    public static class AddressDto {
        private Integer houseNumber;
        private String street;
        private String city;
        private String country;
        private String postCode;

        public AddressDto() {}
        public AddressDto(Integer houseNumber, String street, String city, String country, String postCode) {
            this.houseNumber = houseNumber;
            this.street = street;
            this.city = city;
            this.country = country;
            this.postCode = postCode;
        }

        public Integer getHouseNumber() { return houseNumber; }
        public void setHouseNumber(Integer houseNumber) { this.houseNumber = houseNumber; }
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getPostCode() { return postCode; }
        public void setPostCode(String postCode) { this.postCode = postCode; }
    }

    public static class ContactsDto {
        private String phone;
        private String email;

        public ContactsDto() {}
        public ContactsDto(String phone, String email) {
            this.phone = phone;
            this.email = email;
        }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ArrivalTimeDto {
        private String checkIn;
        private String checkOut;

        public ArrivalTimeDto() {}
        public ArrivalTimeDto(String checkIn, String checkOut) {
            this.checkIn = checkIn;
            this.checkOut = checkOut;
        }

        public String getCheckIn() { return checkIn; }
        public void setCheckIn(String checkIn) { this.checkIn = checkIn; }
        public String getCheckOut() { return checkOut; }
        public void setCheckOut(String checkOut) { this.checkOut = checkOut; }
    }
}
