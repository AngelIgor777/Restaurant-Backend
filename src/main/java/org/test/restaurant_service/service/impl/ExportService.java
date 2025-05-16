package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.*;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.service.OrderService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportService {

    private static final Logger log = LoggerFactory.getLogger(ExportService.class);
    private final OrderService orderService;


    public ResponseEntity<byte[]> exportOrdersToExcel(Order.OrderStatus status, LocalDateTime from, LocalDateTime to) {
        List<OrderProductResponseWithPayloadDto> orders = orderService.getAllOrdersProductResponseWithPayloadDto(status, from, to);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orders");

        String[] headers = {
                "ID заказа", "Метод оплаты", "Общая цена", "Создано",
                "Блюда"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        int rowNum = 1;
        for (OrderProductResponseWithPayloadDto dto : orders) {
            Row row = sheet.createRow(rowNum++);

            int col = 0;

            OrderResponseDTO order = dto.getOrderResponseDTO();

            if (order != null) {
                String createdAt = order.getCreatedAt().format(formatter);
                row.createCell(col++).setCellValue(safeString(order.getId()));
                row.createCell(col++).setCellValue(safeString(order.getPaymentMethod()));
                row.createCell(col++).setCellValue(order.getTotalPrice() != null ? order.getTotalPrice().toString() : "");
                row.createCell(col++).setCellValue(createdAt);
            }

            List<ProductResponseDTO> products = order != null ? order.getProducts() : null;
            String productsString = "";
            if (products != null) {
                productsString = products.stream()
                        .map(p -> safeString(p.getName()+"("+p.getPrice()) + "лей) x" + (p.getQuantity() != null ? p.getQuantity() : ""))
                        .collect(Collectors.joining(", "));
            }
            row.createCell(col++).setCellValue(productsString);
        }

        // Autosize columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            workbook.write(out);
        } catch (IOException e) {
            log.error("Error when writing info to workbook", e);
        }
        try {
            workbook.close();
        } catch (IOException e) {
            log.error("Error when closing info to workbook", e);
        }

        String fileName = from.format(formatter) + "-" + to.format(formatter) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(out.toByteArray());
    }

    // Helper method to avoid nulls
    private String safeString(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}
