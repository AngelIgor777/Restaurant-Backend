package org.test.restaurant_service.controller.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.test.restaurant_service.dto.request.SharedBucketProductRequestDTO;
import org.test.restaurant_service.dto.response.sharedBucket.UserBucketResponseDto;
import org.test.restaurant_service.service.SharedBucketService;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class OrderWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SharedBucketService sharedBucketService;


    @MessageMapping("/order/{sessionUuid}/addProduct")
    public void addProduct(@DestinationVariable UUID sessionUuid, SharedBucketProductRequestDTO request) {
        Integer sharedBucketId = request.getSharedBucketId();
        sharedBucketService.addProduct(sessionUuid, request);
        List<UserBucketResponseDto> updatedBucket = sharedBucketService.getUsersInfoInSharedBucketById(sharedBucketId);
        messagingTemplate.convertAndSend("/topic/order/" + sessionUuid, updatedBucket);
    }

    @MessageMapping("/order/{sessionUUID}/confirm")
    public void confirmOrder(@DestinationVariable UUID sessionUUID, UUID userUUID) {
        sharedBucketService.confirmUser(sessionUUID, userUUID);
        boolean allConfirmed = sharedBucketService.allUsersConfirmed(sessionUUID);

        if (allConfirmed) {
            messagingTemplate.convertAndSend("/topic/order/" + sessionUUID + "/progress", true);
        } else {
            messagingTemplate.convertAndSend("/topic/order/" + sessionUUID + "/progress", userUUID.toString());
        }
    }
}