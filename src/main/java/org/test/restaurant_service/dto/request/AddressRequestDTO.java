package org.test.restaurant_service.dto.request;

import lombok.*;

import java.util.UUID;

/**
 * DTO for handling address requests.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequestDTO {

    private String city;

    private String street;

    private String homeNumber;

    private String apartmentNumber;

    private UUID userUUID;

}
