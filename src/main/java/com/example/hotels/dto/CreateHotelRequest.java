package com.example.hotels.dto;

public class CreateHotelRequest {

    private String name;
    private String description;
    private String brand;
    private AddressRequest address;
    private ContactsRequest contacts;
    private ArrivalTimeRequest arrivalTime;

    public CreateHotelRequest() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public AddressRequest getAddress() { return address; }
    public void setAddress(AddressRequest address) { this.address = address; }

    public ContactsRequest getContacts() { return contacts; }
    public void setContacts(ContactsRequest contacts) { this.contacts = contacts; }

    public ArrivalTimeRequest getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(ArrivalTimeRequest arrivalTime) { this.arrivalTime = arrivalTime; }

    public static class AddressRequest {
        private Integer houseNumber;
        private String street;
        private String city;
        private String country;
        private String postCode;

        public AddressRequest() {}

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

    public static class ContactsRequest {
        private String phone;
        private String email;

        public ContactsRequest() {}

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ArrivalTimeRequest {
        private String checkIn;
        private String checkOut;

        public ArrivalTimeRequest() {}

        public String getCheckIn() { return checkIn; }
        public void setCheckIn(String checkIn) { this.checkIn = checkIn; }
        public String getCheckOut() { return checkOut; }
        public void setCheckOut(String checkOut) { this.checkOut = checkOut; }
    }
}
