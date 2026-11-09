package com.castor.guloso.order.api;

import com.castor.guloso.core.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private Long sessionId;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long menuItemId;
    private String menuItemName;
    private String freeTextItem;
    private OrderStatus status;
    private String whatsappMessageId;
    private LocalDateTime repliedAt;
    private LocalDateTime createdAt;
}
