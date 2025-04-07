package org.test.restaurant_service.dto.response.printer;

public class Address {
    private String city;
    private String street;
    private String homeNumber;
    private String apartmentNumber;


    public Address() {
    }

    public Address(String city, String street, String homeNumber, String apartmentNumber) {
        this.city = city;
        this.street = street;
        this.homeNumber = homeNumber;
        this.apartmentNumber = apartmentNumber;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setHomeNumber(String homeNumber) {
        this.homeNumber = homeNumber;
    }

    public String getCity() {
        return city;
    }

    public String getHomeNumber() {
        return homeNumber;
    }

    public String getStreet() {
        return street;
    }
}
