package org.test.restaurant_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.test.restaurant_service.dto.request.ProductRequestDTO;
import org.test.restaurant_service.dto.response.ProductAndPhotosResponseDTO;
import org.test.restaurant_service.dto.response.ProductIdsResponse;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.entity.Photo;
import org.test.restaurant_service.entity.Product;

import java.math.BigDecimal;
import java.util.List;


public interface ProductService {

    Product parseRequest(String name, String description, Integer typeId, BigDecimal price, String cookingTime);

    Product create(Product product, Integer typeId);

    Product create(ProductRequestDTO productRequestDTO);

    Product update(Product product, Integer id, List<Photo> photos);

    void delete(Integer id);

    ProductAndPhotosResponseDTO getById(Integer id);

    Product getSimpleById(Integer id);

    Page<ProductResponseDTO> getAll(Integer typeId, Pageable pageable);

    ProductResponseDTO getByName(String product);

    List<ProductResponseDTO> getByTypeName(String typeName);

    List<ProductResponseDTO> getTop10WeekProducts(Pageable pageable);

    boolean existByName(String name);

    Page<Product> searchProducts(String searchTerm, int page, int size);

    List<ProductIdsResponse> getAllProductsId();


    void markAvailability(Integer id, boolean available);

}
