package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.test.restaurant_service.dto.response.ProductAndPhotosResponseDTO;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.mapper.ProductMapper;
import org.test.restaurant_service.service.PhotoService;
import org.test.restaurant_service.service.ProductAndPhotoService;
import org.test.restaurant_service.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductAndPhotoService productAndPhotoService;
    private final ProductMapper productMapper;
    private final PhotoService photoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestParam("name") String name,
                       @RequestParam("description") String description,
                       @RequestParam("typeId") Integer typeId,
                       @RequestParam("price") BigDecimal price,
                       @RequestParam("cookingTime") String cookingTime,
                       @RequestParam("file") MultipartFile file) {

        Product product = productService.parseRequest(name, description, typeId, price, cookingTime);

        List<MultipartFile> multipartFiles = List.of(file);
        productAndPhotoService.createProductAndPhotos(product, typeId, multipartFiles);
    }


    @PatchMapping
    public ProductResponseDTO update(@RequestParam("id") Integer id,
                                     @RequestParam("name") String name,
                                     @RequestParam("description") String description,
                                     @RequestParam("typeId") Integer typeId,
                                     @RequestParam("price") BigDecimal price,
                                     @RequestParam("cookingTime") String cookingTime) {

        Product product = productService.parseRequest(name, description, typeId, price, cookingTime);
        Product update = productService.update(id, product);
        return productMapper.toResponseDTO(update);
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
