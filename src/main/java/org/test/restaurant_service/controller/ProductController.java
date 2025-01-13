package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.test.restaurant_service.dto.request.ProductRequestDTO;
import org.test.restaurant_service.dto.response.ProductAndPhotosResponseDTO;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.service.ProductAndPhotoService;
import org.test.restaurant_service.service.ProductService;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductAndPhotoService productAndPhotoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestParam("name") String name,
                       @RequestParam("description") String description,
                       @RequestParam("typeId") Integer typeId,
                       @RequestParam("price") BigDecimal price,
                       @RequestParam("cookingTime") String cookingTime,
                       @RequestParam("file") MultipartFile file) {

        Product product = parseRequest(name, description, typeId, price, cookingTime);

        // Обрабатываем файл и данные
        List<MultipartFile> multipartFiles = List.of(file);
        productAndPhotoService.createProductAndPhotos(product, typeId, multipartFiles);
    }

    private Product parseRequest(String name, String description, Integer typeId, BigDecimal price, String cookingTime) {
        Product productRequestDTO = new Product();
        productRequestDTO.setName(name);
        productRequestDTO.setDescription(description);
        productRequestDTO.setPrice(price);
        productRequestDTO.setCookingTime(LocalTime.parse(cookingTime));
        return productRequestDTO;
    }

    //TODO обновление продукта с фотографией
    @PatchMapping("/{id}")
    public ProductResponseDTO update(@PathVariable Integer id, @Valid @RequestBody ProductRequestDTO requestDTO) {
        return productService.update(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        productService.delete(id);
    }

    @GetMapping("/{id}")
    public ProductAndPhotosResponseDTO getById(@PathVariable Integer id) {
        return productService.getById(id);
    }

    @GetMapping
    public Page<ProductResponseDTO> getAll(@RequestParam(required = false) Integer typeId, Pageable pageable) {
        return productService.getAll(typeId, pageable);
    }
}
