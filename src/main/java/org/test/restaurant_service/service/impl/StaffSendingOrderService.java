package org.test.restaurant_service.service.impl;

import org.test.restaurant_service.entity.StaffSendingOrder;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.repository.StaffSendingOrderRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffSendingOrderService {
    private final StaffSendingOrderRepository staffSendingOrderRepository;

    public void setStaffSendingState(Long chatId, boolean sendingState) {
        StaffSendingOrder staffSendingOrder = staffSendingOrderRepository.findByChatId(chatId)
                .orElseThrow(() -> new EntityNotFoundException("StaffSendingOrder not found for chatId: " + chatId));
        staffSendingOrder.setSendingOn(sendingState);
        staffSendingOrderRepository.save(staffSendingOrder);
    }

    public void createStaffSendingOrder(User user, Long chatId) {
        if (staffSendingOrderRepository.findByChatId(chatId).isEmpty()) {
            StaffSendingOrder staffSendingOrder = new StaffSendingOrder();
            staffSendingOrder.setChatId(chatId);
            staffSendingOrder.setUser(user);
            staffSendingOrder.setSendingOn(false);
            staffSendingOrderRepository.save(staffSendingOrder);
        }
    }

    public List<StaffSendingOrder> getAllSendingState(boolean sendingState) {
        return staffSendingOrderRepository.findAllBySendingOn(sendingState);
    }
}
