package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.test.restaurant_service.controller.websocket.WebSocketSender;
import org.test.restaurant_service.dto.response.TelegramUserDTO;
import org.test.restaurant_service.dto.response.WaiterCallRequestDTO;
import org.test.restaurant_service.entity.StaffSendingOrder;
import org.test.restaurant_service.entity.TelegramUserEntity;
import org.test.restaurant_service.mapper.TelegramUserMapper;
import org.test.restaurant_service.service.TelegramUserService;
import org.test.restaurant_service.service.impl.StaffSendingOrderService;
import org.test.restaurant_service.service.impl.cache.WaiterCallCacheService;
import org.test.restaurant_service.telegram.handling.WorkTelegramBot;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/waiter-calls")
@RequiredArgsConstructor
public class WaiterCallController {

    private final WaiterCallCacheService waiterCallCacheService;
    private final TelegramUserService telegramUserService;
    private final WebSocketSender webSocketSender;
    private final WorkTelegramBot workTelegramBot;
    private final StaffSendingOrderService staffSendingOrderService;


    @GetMapping("/{tableNumber}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<WaiterCallRequestDTO> getCallByTable(@PathVariable Integer tableNumber) {
        WaiterCallRequestDTO call = waiterCallCacheService.getWaiterCallByTable(tableNumber);
        if (call == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(call);
    }

    @GetMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<List<WaiterCallRequestDTO>> getAllWaiterCalls() {
        List<WaiterCallRequestDTO> calls = waiterCallCacheService.getAllWaiterCalls();
        return ResponseEntity.ok(calls);
    }

    @DeleteMapping("/{tableNumber}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public ResponseEntity<Void> deleteCall(@PathVariable Integer tableNumber) {
        waiterCallCacheService.deleteWaiterCall(tableNumber);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{tableId}/call-waiter")
    @PreAuthorize("@securityService.userIsActivated(authentication)")
    public ResponseEntity<String> callWaiter(@PathVariable Integer tableId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long chatId = Long.parseLong(auth.getName());

        TelegramUserEntity user = telegramUserService.getByChatId(chatId);
        TelegramUserDTO dto = TelegramUserMapper.INSTANCE.toDto(user);

        WaiterCallRequestDTO request = WaiterCallRequestDTO.builder()
                .tableNumber(tableId)
                .requestTime(LocalTime.now())
                .telegramUser(dto)
                .build();

        waiterCallCacheService.saveWaiterCall(request);
        webSocketSender.sendCallToWaiter(request);

        List<StaffSendingOrder> staffList = staffSendingOrderService.getAllSendingState(true);
        String caption = buildWaiterNotificationMessage(request);

        for (StaffSendingOrder staff : staffList) {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(staff.getChatId().toString());
            sendPhoto.setParseMode("HTML");
            sendPhoto.setCaption(caption);

            if (dto.getPhotoUrl() != null && !dto.getPhotoUrl().isBlank()) {
                sendPhoto.setPhoto(new InputFile(dto.getPhotoUrl()));
            } else {
                sendPhoto.setPhoto(new InputFile(
                        "https://dummyimage.com/600x400/cccccc/000000.png&text=No+Photo"));
            }
            try {
                workTelegramBot.execute(sendPhoto);
            } catch (TelegramApiException e) {
                throw new RuntimeException("Ошибка отправки уведомления сотруднику", e);
            }
        }

        // 6) Отвечаем клиенту
        String userMsg = "✅ Официант был вызван к столику №" + tableId;
        return ResponseEntity.ok(userMsg);
    }

    private String buildWaiterNotificationMessage(WaiterCallRequestDTO dto) {
        return new StringBuilder()
                .append("<b>Вызов официанта</b>\n")
                .append("Стол: ").append(dto.getTableNumber()).append("\n")
                .append("Время: ").append(dto.getRequestTime().format(
                        DateTimeFormatter.ofPattern("HH:mm"))).append("\n")
                .append("Пользователь: ").append(dto.getTelegramUser().getFirstname())
                .toString();
    }
}
