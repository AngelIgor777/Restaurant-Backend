package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.SharedBucketRequestDTO;
import org.test.restaurant_service.dto.response.sharedBucket.SharedBucketProductPayloadResponseDto;
import org.test.restaurant_service.dto.response.sharedBucket.SharedBucketResponseDTO;
import org.test.restaurant_service.entity.SharedBucket;
import org.test.restaurant_service.mapper.SharedBucketMapper;
import org.test.restaurant_service.service.SharedBucketService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shared-buckets")
@RequiredArgsConstructor
public class SharedBucketController {

    private final SharedBucketService sharedBucketService;

    @PostMapping
    public ResponseEntity<SharedBucketResponseDTO> createSharedBucket(@RequestBody SharedBucketRequestDTO dto) {
        SharedBucketResponseDTO response = sharedBucketService.createSharedBucket(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SharedBucketProductPayloadResponseDto> getSharedBucketById(
            @PathVariable Integer id,
            @RequestParam(required = false) UUID userUUID
    ) {
        return ResponseEntity.ok(sharedBucketService.getSharedBucketById(id, userUUID));
    }

    @GetMapping
    public ResponseEntity<List<SharedBucketResponseDTO>> getAllSharedBuckets() {
        List<SharedBucketResponseDTO> responseList = sharedBucketService.getAllSharedBuckets();
        return ResponseEntity.ok(responseList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSharedBucket(@PathVariable Integer id) {
        sharedBucketService.deleteSharedBucket(id);
        return ResponseEntity.noContent().build();
    }
}
