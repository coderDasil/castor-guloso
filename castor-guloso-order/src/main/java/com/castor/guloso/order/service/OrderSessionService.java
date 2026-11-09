package com.castor.guloso.order.service;

import com.castor.guloso.core.domain.Customer;
import com.castor.guloso.core.domain.DailyOrder;
import com.castor.guloso.core.domain.DailySession;
import com.castor.guloso.core.domain.MenuItem;
import com.castor.guloso.core.enums.OrderStatus;
import com.castor.guloso.core.enums.SessionStatus;
import com.castor.guloso.customer.domain.CustomerRepository;
import com.castor.guloso.menu.domain.MenuItemRepository;
import com.castor.guloso.order.api.KitchenSummaryItem;
import com.castor.guloso.order.api.OrderResponse;
import com.castor.guloso.order.domain.DailyOrderRepository;
import com.castor.guloso.order.domain.DailySessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Core order lifecycle: open session → create pending orders → confirm replies → finalize.
 * WhatsApp sending is intentionally left to the whatsapp module / a future scheduler.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderSessionService {

    private final DailySessionRepository sessionRepository;
    private final DailyOrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final MenuItemRepository menuItemRepository;

    public Optional<DailySession> findTodaySession() {
        return sessionRepository.findBySessionDate(LocalDate.now());
    }

    public Optional<DailySession> findOpenTodaySession() {
        return sessionRepository.findBySessionDateAndStatus(LocalDate.now(), SessionStatus.OPEN);
    }

    @Transactional
    public DailySession openOrGetTodaySession() {
        return sessionRepository.findBySessionDate(LocalDate.now())
                .orElseGet(() -> sessionRepository.save(DailySession.builder()
                        .sessionDate(LocalDate.now())
                        .status(SessionStatus.OPEN)
                        .build()));
    }

    /**
     * Creates a PENDING DailyOrder for every active customer that does not already
     * have an order in today's session. Returns the list of newly created orders.
     */
    @Transactional
    public List<OrderResponse> createPendingOrdersForActiveCustomers() {
        DailySession session = openOrGetTodaySession();
        if (session.getStatus() == SessionStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Today's session is already closed");
        }

        List<Customer> active = customerRepository.findByActiveTrue();
        List<DailyOrder> created = new ArrayList<>();

        for (Customer customer : active) {
            Optional<DailyOrder> existing = orderRepository.findBySessionAndCustomerId(session, customer.getId());
            if (existing.isEmpty()) {
                DailyOrder order = DailyOrder.builder()
                        .session(session)
                        .customer(customer)
                        .status(OrderStatus.PENDING)
                        .build();
                created.add(orderRepository.save(order));
            }
        }
        return created.stream().map(this::toResponse).toList();
    }

    public List<OrderResponse> listOrdersForToday() {
        return findTodaySession()
                .map(s -> orderRepository.findBySession(s).stream().map(this::toResponse).toList())
                .orElse(List.of());
    }

    public List<OrderResponse> listOrdersByStatus(OrderStatus status) {
        return findTodaySession()
                .map(s -> orderRepository.findBySessionAndStatus(s, status).stream().map(this::toResponse).toList())
                .orElse(List.of());
    }

    @Transactional
    public OrderResponse confirmOrder(Long orderId, Long menuItemId, String freeText) {
        DailyOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (order.getSession().getStatus() == SessionStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Session is closed");
        }

        if (menuItemId != null) {
            MenuItem item = menuItemRepository.findById(menuItemId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));
            order.setMenuItem(item);
            order.setFreeTextItem(null);
        } else if (freeText != null && !freeText.isBlank()) {
            order.setMenuItem(null);
            order.setFreeTextItem(freeText.trim());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide menuItemId or freeText");
        }

        order.setStatus(OrderStatus.CONFIRMED);
        order.setRepliedAt(LocalDateTime.now());
        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse confirmByCustomerPhone(String phone, Long menuItemId, String freeText) {
        DailySession session = findOpenTodaySession()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No open session today"));

        String normalized = phone == null ? "" : phone.replaceAll("[^0-9+]", "");
        DailyOrder order = orderRepository.findBySession(session).stream()
                .filter(o -> o.getCustomer().getPhone().equals(normalized)
                        || o.getCustomer().getPhone().endsWith(normalized)
                        || normalized.endsWith(o.getCustomer().getPhone()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No pending order for this phone"));

        return confirmOrder(order.getId(), menuItemId, freeText);
    }

    @Transactional
    public void markRemainingAsNoReply() {
        DailySession session = findOpenTodaySession()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No open session today"));

        List<DailyOrder> pending = orderRepository.findBySessionAndStatus(session, OrderStatus.PENDING);
        for (DailyOrder order : pending) {
            order.setStatus(OrderStatus.NO_REPLY);
            orderRepository.save(order);
        }
    }

    @Transactional
    public DailySession finalizeSession() {
        DailySession session = findOpenTodaySession()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No open session today"));

        markRemainingAsNoReply();
        session.setStatus(SessionStatus.CLOSED);
        session.setCutoffTime(LocalDateTime.now());
        return sessionRepository.save(session);
    }

    public List<KitchenSummaryItem> kitchenSummary() {
        DailySession session = findTodaySession()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No session today"));

        List<DailyOrder> confirmed = orderRepository.findBySessionAndStatus(session, OrderStatus.CONFIRMED);

        Map<String, KitchenSummaryItem> map = new LinkedHashMap<>();

        for (DailyOrder order : confirmed) {
            if (order.getMenuItem() != null) {
                String key = "id:" + order.getMenuItem().getId();
                map.compute(key, (k, existing) -> {
                    if (existing == null) {
                        return KitchenSummaryItem.builder()
                                .menuItemId(order.getMenuItem().getId())
                                .menuItemName(order.getMenuItem().getName())
                                .quantity(1)
                                .freeText(false)
                                .build();
                    }
                    existing.setQuantity(existing.getQuantity() + 1);
                    return existing;
                });
            } else if (order.getFreeTextItem() != null) {
                String key = "ft:" + order.getFreeTextItem().toLowerCase();
                map.compute(key, (k, existing) -> {
                    if (existing == null) {
                        return KitchenSummaryItem.builder()
                                .menuItemName(order.getFreeTextItem())
                                .quantity(1)
                                .freeText(true)
                                .build();
                    }
                    existing.setQuantity(existing.getQuantity() + 1);
                    return existing;
                });
            }
        }

        return new ArrayList<>(map.values());
    }

    private OrderResponse toResponse(DailyOrder order) {
        return OrderResponse.builder()
                .id(order.getId())
                .sessionId(order.getSession().getId())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getName())
                .customerPhone(order.getCustomer().getPhone())
                .menuItemId(order.getMenuItem() != null ? order.getMenuItem().getId() : null)
                .menuItemName(order.getMenuItem() != null ? order.getMenuItem().getName() : null)
                .freeTextItem(order.getFreeTextItem())
                .status(order.getStatus())
                .whatsappMessageId(order.getWhatsappMessageId())
                .repliedAt(order.getRepliedAt())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
