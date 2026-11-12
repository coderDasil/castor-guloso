package com.castor.guloso.whatsapp.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Dev / demo implementation. Never calls Meta.
 * Logs every outbound message and returns a synthetic message id.
 */
@Component
@ConditionalOnProperty(name = "whatsapp.mode", havingValue = "logging", matchIfMissing = true)
public class LoggingWhatsAppClient implements WhatsAppClient {

    private static final Logger log = LoggerFactory.getLogger(LoggingWhatsAppClient.class);
    private final AtomicLong counter = new AtomicLong(1);

    @Override
    public String sendText(String phoneE164, String body) {
        String msgId = "log-" + counter.getAndIncrement() + "-" + UUID.randomUUID().toString().substring(0, 8);
        log.info("[WhatsApp-LOGGING] TEXT → {} | id={} | body={}", phoneE164, msgId, body);
        return msgId;
    }

    @Override
    public String sendTemplate(String phoneE164, String templateName, String languageCode, String... params) {
        String msgId = "log-tpl-" + counter.getAndIncrement() + "-" + UUID.randomUUID().toString().substring(0, 8);
        log.info("[WhatsApp-LOGGING] TEMPLATE '{}' ({}) → {} | id={} | params={}",
                templateName, languageCode, phoneE164, msgId, String.join(" | ", params));
        return msgId;
    }
}
