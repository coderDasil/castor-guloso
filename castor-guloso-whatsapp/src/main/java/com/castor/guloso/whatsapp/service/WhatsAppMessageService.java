package com.castor.guloso.whatsapp.service;

import com.castor.guloso.core.domain.DailyOrder;
import com.castor.guloso.core.domain.MenuItem;
import com.castor.guloso.menu.domain.DailyMenuRepository;
import com.castor.guloso.menu.domain.MenuItemRepository;
import com.castor.guloso.order.api.OrderResponse;
import com.castor.guloso.order.domain.DailyOrderRepository;
import com.castor.guloso.order.service.OrderSessionService;
import com.castor.guloso.whatsapp.client.WhatsAppClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * High-level operations: open session, create pending orders, send lunch requests.
 */
@Service
@RequiredArgsConstructor
public class WhatsAppMessageService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppMessageService.class);

    private final WhatsAppClient whatsAppClient;
    private final OrderSessionService orderSessionService;
    private final DailyOrderRepository orderRepository;
    private final DailyMenuRepository dailyMenuRepository;
    private final MenuItemRepository menuItemRepository;

    /**
     * Full morning flow:
     * 1. Open (or get) today's session
     * 2. Create PENDING orders for all active customers
     * 3. Send a personalized WhatsApp message for each new order
     * 4. Store the provider message id on the order
     *
     * @return number of messages actually sent
     */
    @Transactional
    public int sendDailyLunchRequests() {
        List<OrderResponse> created = orderSessionService.createPendingOrdersForActiveCustomers();
        if (created.isEmpty()) {
            log.info("No new pending orders to send for today");
            return 0;
        }

        String menuText = buildTodayMenuText();
        int sent = 0;

        for (OrderResponse or : created) {
            String body = buildRequestBody(or.getCustomerName(), menuText);
            try {
                String msgId = whatsAppClient.sendText(or.getCustomerPhone(), body);
                orderRepository.findById(or.getId()).ifPresent(order -> {
                    order.setWhatsappMessageId(msgId);
                    orderRepository.save(order);
                });
                sent++;
            } catch (Exception e) {
                log.error("Failed to send WhatsApp to {} (order {}): {}",
                        or.getCustomerPhone(), or.getId(), e.getMessage());
            }
        }
        log.info("Daily lunch requests: {} sent out of {} pending", sent, created.size());
        return sent;
    }

    private String buildTodayMenuText() {
        return dailyMenuRepository.findByMenuDate(LocalDate.now())
                .map(dm -> dm.getItems().stream()
                        .map(MenuItem::getName)
                        .collect(Collectors.joining(", ")))
                .orElseGet(() -> menuItemRepository.findByAvailableTrue().stream()
                        .map(MenuItem::getName)
                        .collect(Collectors.joining(", ")));
    }

    private String buildRequestBody(String customerName, String menuText) {
        // Simple free-form for now. Later replace with approved utility template.
        return String.format(
                "Olá %s! 🦫 Cardápio de hoje: %s. Responda com o prato desejado ou o número.",
                customerName != null ? customerName : "cliente",
                menuText.isBlank() ? "(ainda não definido)" : menuText);
    }
}
