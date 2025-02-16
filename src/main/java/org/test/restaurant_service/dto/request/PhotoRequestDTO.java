package org.test.restaurant_service.dto.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoRequestDTO {

    private Integer productId;

    @JsonIgnore
    private MultipartFile image; // Используется для загрузки фото
}
