package org.test.restaurant_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AddressResponseDTO {

    private Integer id;

    private String city;

    private String street;

    private String homeNumber;

    private String apartmentNumber;

    private UUID userUUID;
}
