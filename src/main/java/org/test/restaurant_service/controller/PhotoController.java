package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.PhotoRequestDTO;
import org.test.restaurant_service.dto.response.PhotoResponseDTO;
import org.test.restaurant_service.service.PhotoService;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;


@Slf4j
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
    public ResponseEntity<Resource> getPhoto(@RequestParam String photoName) {
        Resource resource = new FileSystemResource("uploads/images/" + photoName);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType;
        try (InputStream inputStream = resource.getInputStream()) {
            contentType = Files.probeContentType(inputStream.available() > 0 ? resource.getFile().toPath() : null);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
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
