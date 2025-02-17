package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.PhotoRequestDTO;
import org.test.restaurant_service.dto.response.PhotoResponseDTO;
import org.test.restaurant_service.entity.Photo;
import org.test.restaurant_service.mapper.PhotoMapper;
import org.test.restaurant_service.service.PhotoService;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/photos")
public class PhotoController {

    private final PhotoService photoService;
    private final PhotoMapper photoMapper;

    public PhotoController(@Qualifier("photoServiceImplS3") PhotoService photoService, PhotoMapper photoMapper) {
        this.photoService = photoService;
        this.photoMapper = photoMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PhotoResponseDTO create(@Valid @RequestBody PhotoRequestDTO requestDTO) {
        Photo photo = photoMapper.toEntity(requestDTO);
        return photoService.create(photo);
    }

    @GetMapping("/product/{productId}")
    public List<PhotoResponseDTO> getPhotosByProductId(@PathVariable Integer productId) {
        return photoService.getPhotosByProductId(productId);
    }

    @GetMapping("/resource")
    @ResponseBody
    public ResponseEntity<Resource> getPhoto(@RequestParam String photoName) {
        Resource image = photoService.getImage(photoName);

        String contentType = photoService.getContentType(image);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(image);
    }

    @PatchMapping("/{id}")
    public PhotoResponseDTO update(@PathVariable Integer id, @Valid @RequestBody PhotoRequestDTO requestDTO) {
        return photoService.update(id, requestDTO);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam String photoUrl) {

        List<String> imageNameList = List.of(photoUrl);
        photoService.deletePhotos(imageNameList);
    }
}
