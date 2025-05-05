package org.test.restaurant_service.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.mapper.ProductMapper;
import org.test.restaurant_service.service.ProductAndPhotoService;
import org.test.restaurant_service.service.ProductAndProductHistoryService;
import org.test.restaurant_service.service.ProductService;
import org.test.restaurant_service.service.impl.ProductTranslReadService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final ProductAndPhotoService productAndPhotoService;
    private final ProductAndProductHistoryService productAndProductHistoryService;
    private final ProductTranslReadService productTranslReadService;

    public ProductController(@Qualifier("productServiceWithS3Impl") ProductService productService, @Qualifier("productAndAndPhotoServiceWithSaveInS3Impl") ProductAndPhotoService productAndPhotoService, ProductAndProductHistoryService productAndProductHistoryService, ProductTranslReadService productTranslReadService) {
        this.productService = productService;
        this.productAndPhotoService = productAndPhotoService;
        this.productAndProductHistoryService = productAndProductHistoryService;
        this.productTranslReadService = productTranslReadService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ProductResponseDTO create(@RequestParam("name") String name,
                                     @RequestParam("description") String description,
                                     @RequestParam("typeId") Integer typeId,
                                     @RequestParam("price") BigDecimal price,
                                     @RequestParam("cookingTime") String cookingTime,
                                     @RequestParam("file") MultipartFile file) {

        Product product = productService.parseRequest(name, description, typeId, price, cookingTime);

        List<MultipartFile> multipartFiles = List.of(file);
        return productAndPhotoService.createProductAndPhotos(product, typeId, multipartFiles);
    }


    @PatchMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public ProductResponseDTO update(@RequestParam("id") Integer id,
                                     @RequestParam("name") String name,
                                     @RequestParam("description") String description,
                                     @RequestParam("typeId") Integer typeId,
                                     @RequestParam("price") BigDecimal price,
                                     @RequestParam("cookingTime") String cookingTime,
                                     @RequestParam(value = "file", required = false) MultipartFile file) {

        Product product = productService.parseRequest(name, description, typeId, price, cookingTime);

        Product update = productAndProductHistoryService.update(product, id, file);
        return ProductMapper.INSTANCE.toResponseDTO(update);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        productService.delete(id);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getById(@PathVariable Integer id,
                                      @RequestHeader(name = "Accept-Language",
                                              defaultValue = "ru") String lang) {
        return productTranslReadService.one(id, lang);
    }

    @GetMapping
    public Page<ProductResponseDTO> getAll(@RequestParam(required = false) Integer typeId,
                                           @RequestHeader(name = "Accept-Language",
                                                   defaultValue = "ru") String lang,
                                           Pageable pageable) {
        return productTranslReadService.list(typeId, lang, pageable);
    }

    @GetMapping("/top-weekly")
    public List<ProductResponseDTO> getTop10WeekProducts(
            @RequestHeader(name = "Accept-Language", defaultValue = "ru") String lang,
            Pageable pageable) {
        return productTranslReadService.topWeek(lang, pageable);
    }

    @GetMapping("/search")
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> searchProducts(@RequestParam String query,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") @Valid @Max(30) int size) {
        Page<Product> products = productService.searchProducts(query, page, size);
        return products.map(ProductMapper.INSTANCE::toResponseDTO);
    }
}
