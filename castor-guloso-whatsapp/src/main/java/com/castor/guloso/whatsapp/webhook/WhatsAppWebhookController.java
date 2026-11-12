package com.castor.guloso.whatsapp.webhook;

import com.castor.guloso.order.service.OrderSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Meta WhatsApp Cloud API webhook endpoint.
 * GET  = verification challenge
 * POST = inbound messages (replies from clients)
 */
@RestController
@RequestMapping("/webhook/whatsapp")
public class WhatsAppWebhookController {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppWebhookController.class);

    private final OrderSessionService orderSessionService;
    private final String verifyToken;

    public WhatsAppWebhookController(OrderSessionService orderSessionService,
                                     @Value("${whatsapp.api.webhook-verify-token:castor_guloso_dev}") String verifyToken) {
        this.orderSessionService = orderSessionService;
        this.verifyToken = verifyToken;
    }

    /**
     * Meta webhook verification (hub.mode, hub.verify_token, hub.challenge).
     */
    @GetMapping
    public ResponseEntity<String> verify(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.verify_token", required = false) String token,
            @RequestParam(name = "hub.challenge", required = false) String challenge) {

        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            log.info("WhatsApp webhook verified successfully");
            return ResponseEntity.ok(challenge != null ? challenge : "");
        }
        log.warn("WhatsApp webhook verification failed (mode={}, token match={})", mode, verifyToken.equals(token));
        return ResponseEntity.status(403).body("Forbidden");
    }

    /**
     * Inbound messages. For now we extract text + phone and try to confirm the order.
     * Full Meta payload parsing can be expanded later.
     */
    @PostMapping
    public ResponseEntity<String> receive(@RequestBody Map<String, Object> payload) {
        log.debug("WhatsApp inbound payload: {}", payload);
        try {
            // Very defensive extraction of the first text message
            Object entry = payload.get("entry");
            if (entry instanceof java.util.List<?> entries && !entries.isEmpty()) {
                Map<?, ?> firstEntry = (Map<?, ?>) entries.get(0);
                Object changes = firstEntry.get("changes");
                if (changes instanceof java.util.List<?> changeList && !changeList.isEmpty()) {
                    Map<?, ?> change = (Map<?, ?>) changeList.get(0);
                    Map<?, ?> value = (Map<?, ?>) change.get("value");
                    if (value != null) {
                        Object messages = value.get("messages");
                        if (messages instanceof java.util.List<?> msgList && !msgList.isEmpty()) {
                            Map<?, ?> msg = (Map<?, ?>) msgList.get(0);
                            String from = String.valueOf(msg.get("from"));
                            Object textObj = msg.get("text");
                            String body = null;
                            if (textObj instanceof Map<?, ?> textMap) {
                                body = String.valueOf(textMap.get("body"));
                            }
                            if (from != null && body != null && !body.isBlank()) {
                                log.info("Inbound WhatsApp from {}: {}", from, body);
                                // Free-text confirmation for now
                                orderSessionService.confirmByCustomerPhone(from, null, body.trim());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Could not process inbound WhatsApp payload: {}", e.getMessage());
            // Always 200 so Meta does not retry aggressively
        }
        return ResponseEntity.ok("EVENT_RECEIVED");
    }
}
