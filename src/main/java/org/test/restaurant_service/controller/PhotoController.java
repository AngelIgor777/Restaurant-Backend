package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.test.restaurant_service.dto.request.PhotoRequestDTO;
import org.test.restaurant_service.dto.response.PhotoResponseDTO;
import org.test.restaurant_service.service.PhotoService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PhotoResponseDTO create(@Valid @RequestBody PhotoRequestDTO requestDTO) {
        return photoService.create(requestDTO);
    }

    @GetMapping("/product/{productId}")
    public List<PhotoResponseDTO> getPhotosByProductId(@PathVariable Integer productId) {
        return photoService.getPhotosByProductId(productId);
    }

    @GetMapping("/resource")
    @ResponseBody
    public ResponseEntity<Resource> getPhoto(@RequestParam String photoPath) {
        Resource file = new ClassPathResource(photoPath);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(file);
    }

    @PatchMapping("/{id}")
    public PhotoResponseDTO update(@PathVariable Integer id, @Valid @RequestBody PhotoRequestDTO requestDTO) {
        return photoService.update(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        photoService.delete(id);
    }
}
