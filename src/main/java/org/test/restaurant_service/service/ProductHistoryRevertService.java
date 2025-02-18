package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.response.ProductResponseDTO;

public interface ProductHistoryRevertService {
    ProductResponseDTO revertProductToPickedProductHistory(Integer productId, Integer historyId);
}
