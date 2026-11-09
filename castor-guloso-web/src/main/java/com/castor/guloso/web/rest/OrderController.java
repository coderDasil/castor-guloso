package com.castor.guloso.web.rest;

import com.castor.guloso.core.domain.DailySession;
import com.castor.guloso.core.enums.OrderStatus;
import com.castor.guloso.order.api.KitchenSummaryItem;
import com.castor.guloso.order.api.OrderResponse;
import com.castor.guloso.order.service.OrderSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderSessionService orderSessionService;

    @PostMapping("/session/open")
    public ResponseEntity<DailySession> openSession() {
        return ResponseEntity.ok(orderSessionService.openOrGetTodaySession());
    }

    @PostMapping("/session/create-pending")
    public List<OrderResponse> createPending() {
        return orderSessionService.createPendingOrdersForActiveCustomers();
    }

    @GetMapping("/today")
    public List<OrderResponse> today() {
        return orderSessionService.listOrdersForToday();
    }

    @GetMapping("/today/{status}")
    public List<OrderResponse> todayByStatus(@PathVariable OrderStatus status) {
        return orderSessionService.listOrdersByStatus(status);
    }

    @PostMapping("/{id}/confirm")
    public OrderResponse confirm(@PathVariable Long id,
                                 @RequestBody Map<String, Object> body) {
        Long menuItemId = body.get("menuItemId") != null
                ? Long.valueOf(body.get("menuItemId").toString()) : null;
        String freeText = body.get("freeText") != null ? body.get("freeText").toString() : null;
        return orderSessionService.confirmOrder(id, menuItemId, freeText);
    }

    @PostMapping("/confirm-by-phone")
    public OrderResponse confirmByPhone(@RequestBody Map<String, Object> body) {
        String phone = body.get("phone") != null ? body.get("phone").toString() : null;
        Long menuItemId = body.get("menuItemId") != null
                ? Long.valueOf(body.get("menuItemId").toString()) : null;
        String freeText = body.get("freeText") != null ? body.get("freeText").toString() : null;
        return orderSessionService.confirmByCustomerPhone(phone, menuItemId, freeText);
    }

    @PostMapping("/session/finalize")
    public ResponseEntity<DailySession> finalizeSession() {
        return ResponseEntity.ok(orderSessionService.finalizeSession());
    }

    @GetMapping("/summary")
    public List<KitchenSummaryItem> kitchenSummary() {
        return orderSessionService.kitchenSummary();
    }
}
