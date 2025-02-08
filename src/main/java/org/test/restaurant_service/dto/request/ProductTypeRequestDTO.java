package org.test.restaurant_service.dto.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@ToString
public class ProductTypeRequestDTO {
    @NotBlank
    @Size(max = 128)
    private String name;
}
