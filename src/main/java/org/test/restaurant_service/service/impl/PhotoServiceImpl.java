package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.test.restaurant_service.dto.request.PhotoRequestDTO;
import org.test.restaurant_service.dto.response.PhotoResponseDTO;
import org.test.restaurant_service.entity.Photo;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.mapper.PhotoMapper;
import org.test.restaurant_service.repository.PhotoRepository;
import org.test.restaurant_service.repository.ProductRepository;
import org.test.restaurant_service.service.PhotoService;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private static final String IMAGE_DIRECTORY = "src/main/resources/static/images/";

    private final PhotoRepository photoRepository;
    private final ProductRepository productRepository;
    private final PhotoMapper photoMapper;

    @Override
    @Transactional(rollbackOn = Exception.class)


    public void savePhotos(List<PhotoRequestDTO> photoRequestDTOS) {
        for (PhotoRequestDTO requestDTO : photoRequestDTOS) {
            MultipartFile file = requestDTO.getFile();

            if (file.isEmpty()) {
                throw new BadRequestException("FILE IS EMPTY");
            }

            File directory = new File(IMAGE_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File destFile = new File(IMAGE_DIRECTORY + Objects.requireNonNull(file.getOriginalFilename()).replace(" ", ""));

            try (InputStream inputStream = file.getInputStream();
                 BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destFile))) {

                byte[] buffer = new byte[2048];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

            } catch (IOException e) {
                throw new BadRequestException("File could not be saved: " + e.getMessage());
            }
            create(requestDTO);
        }
    }

    @Override
    public PhotoResponseDTO create(PhotoRequestDTO requestDTO) {
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + requestDTO.getProductId()));
        Photo photo = photoMapper.toEntity(requestDTO);
        photo.setUrl("/static/images/"+ Objects.requireNonNull(requestDTO.getFile().getOriginalFilename()).replace(" ", ""));

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
