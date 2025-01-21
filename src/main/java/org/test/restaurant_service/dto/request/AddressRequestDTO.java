package org.test.restaurant_service.dto.request;

import lombok.*;

import javax.validation.constraints.*;

/**
 * DTO for handling address requests.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequestDTO {

    /**
     * Name of the city.
     */
    @NotBlank(message = "City name cannot be blank")
    @Size(max = 64, message = "City name must be at most 64 characters")
    private String city;

    /**
     * Name of the street.
     */
    @NotBlank(message = "Street name cannot be blank")
    @Size(max = 128, message = "Street name must be at most 128 characters")
    private String street;

    /**
     * Home number of the address.
     */
    @NotBlank(message = "Home number cannot be blank")
    @Size(max = 128, message = "Home number must be at most 128 characters")
    private String homeNumber;

    /**
     * Apartment number of the address, if applicable.
     */
    @Size(max = 8, message = "Apartment number must be at most 8 characters")
    private String apartmentNumber;


    /**
     * Indicates whether the user is registered in the system.
     */
    private boolean isRegisterUser;

    /**
     * ID of the registered user, if applicable.
     * Required if isRegisterUser is true.
     */
    @Positive(message = "User ID must be a positive number")
    private Integer userId;

}
