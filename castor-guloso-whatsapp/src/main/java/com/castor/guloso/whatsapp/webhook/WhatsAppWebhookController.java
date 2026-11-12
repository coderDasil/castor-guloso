package com.castor.guloso.whatsapp.webhook;

import com.castor.guloso.order.service.OrderSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

/**
 * Meta WhatsApp Cloud API webhook endpoint.
 * GET  = verification challenge
 * POST = inbound messages (replies from clients) — signature verified
 */
@RestController
@RequestMapping("/webhook/whatsapp")
public class WhatsAppWebhookController {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppWebhookController.class);

    private final OrderSessionService orderSessionService;
    private final ObjectMapper objectMapper;
    private final String verifyToken;
    private final String appSecret;

    public WhatsAppWebhookController(OrderSessionService orderSessionService,
                                     ObjectMapper objectMapper,
                                     @Value("${whatsapp.api.webhook-verify-token:castor_guloso_dev}") String verifyToken,
                                     @Value("${whatsapp.api.app-secret:}") String appSecret) {
        this.orderSessionService = orderSessionService;
        this.objectMapper = objectMapper;
        this.verifyToken = verifyToken;
        this.appSecret = appSecret;
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
     * Inbound messages. Validates X-Hub-Signature-256 before processing.
     */
    @PostMapping
    public ResponseEntity<String> receive(
            HttpServletRequest request,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signatureHeader) {

        try {
            byte[] rawBody = request.getInputStream().readAllBytes();

            // Signature check (skip only when app-secret is empty — e.g. pure logging mode)
            if (StringUtils.hasText(appSecret)) {
                if (!isValidSignature(rawBody, signatureHeader)) {
                    log.warn("Invalid WhatsApp webhook signature");
                    return ResponseEntity.status(401).body("Invalid signature");
                }
            } else {
                log.debug("Skipping signature check (whatsapp.api.app-secret not set)");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> payload = objectMapper.readValue(rawBody, Map.class);
            processInbound(payload);

        } catch (Exception e) {
            log.warn("Could not process inbound WhatsApp payload: {}", e.getMessage());
            // Always 200 so Meta does not retry aggressively
        }
        return ResponseEntity.ok("EVENT_RECEIVED");
    }

    private boolean isValidSignature(byte[] rawBody, String signatureHeader) {
        if (!StringUtils.hasText(signatureHeader) || !signatureHeader.startsWith("sha256=")) {
            return false;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] expected = mac.doFinal(rawBody);
            String expectedHex = "sha256=" + HexFormat.of().formatHex(expected);

            // Constant-time comparison
            return MessageDigest.isEqual(
                    expectedHex.getBytes(StandardCharsets.UTF_8),
                    signatureHeader.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("Signature verification error: {}", e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private void processInbound(Map<String, Object> payload) {
        Object entry = payload.get("entry");
        if (!(entry instanceof List<?> entries) || entries.isEmpty()) {
            return;
        }
        Map<?, ?> firstEntry = (Map<?, ?>) entries.get(0);
        Object changes = firstEntry.get("changes");
        if (!(changes instanceof List<?> changeList) || changeList.isEmpty()) {
            return;
        }
        Map<?, ?> change = (Map<?, ?>) changeList.get(0);
        Map<?, ?> value = (Map<?, ?>) change.get("value");
        if (value == null) {
            return;
        }
        Object messages = value.get("messages");
        if (!(messages instanceof List<?> msgList) || msgList.isEmpty()) {
            return;
        }
        Map<?, ?> msg = (Map<?, ?>) msgList.get(0);
        String from = String.valueOf(msg.get("from"));
        Object textObj = msg.get("text");
        String body = null;
        if (textObj instanceof Map<?, ?> textMap) {
            body = String.valueOf(textMap.get("body"));
        }
        if (from != null && body != null && !body.isBlank()) {
            log.info("Inbound WhatsApp from {}: {}", from, body);
            orderSessionService.confirmByCustomerPhone(from, null, body.trim());
        }
    }
}
