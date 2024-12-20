package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.PhotoRequestDTO;
import org.test.restaurant_service.dto.response.PhotoResponseDTO;
import org.test.restaurant_service.entity.Photo;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.mapper.PhotoMapper;
import org.test.restaurant_service.repository.PhotoRepository;
import org.test.restaurant_service.repository.ProductRepository;
import org.test.restaurant_service.service.PhotoService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {
    private final PhotoRepository photoRepository;
    private final ProductRepository productRepository;
    private final PhotoMapper photoMapper;

    @Override
    public PhotoResponseDTO create(PhotoRequestDTO requestDTO) {
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + requestDTO.getProductId()));
        Photo photo = photoMapper.toEntity(requestDTO);
        photo.setProduct(product);
        photo = photoRepository.save(photo);
        return photoMapper.toResponseDTO(photo);
    }

    @Override
    public List<PhotoResponseDTO> getPhotosByProductId(Integer productId) {
        return photoRepository.findAllByProductId(productId)
                .stream()
                .map(photoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PhotoResponseDTO update(Integer id, PhotoRequestDTO requestDTO) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Photo not found with id " + id));
        photoMapper.updateEntityFromRequestDTO(requestDTO, photo);
        if (requestDTO.getProductId() != null) {
            Product product = productRepository.findById(requestDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + requestDTO.getProductId()));
            photo.setProduct(product);
        }
        photo = photoRepository.save(photo);
        return photoMapper.toResponseDTO(photo);
    }

    @Override
    public void delete(Integer id) {
        if (!photoRepository.existsById(id)) {
            throw new EntityNotFoundException("Photo not found with id " + id);
        }
        photoRepository.deleteById(id);
    }
}
