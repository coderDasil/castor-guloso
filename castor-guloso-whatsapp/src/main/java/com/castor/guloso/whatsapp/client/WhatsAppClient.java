package com.castor.guloso.whatsapp.client;

/**
 * Abstraction over WhatsApp Business Cloud API.
 * Implementations: LoggingWhatsAppClient (dev) and MetaWhatsAppClient (prod).
 */
public interface WhatsAppClient {

    /**
     * Send a free-form text message (service window) or utility template.
     *
     * @param phoneE164 destination phone in E.164 or local format
     * @param body      message text
     * @return provider message id (or a synthetic id in logging mode)
     */
    String sendText(String phoneE164, String body);

    /**
     * Send a pre-approved utility template (used for the daily lunch request).
     *
     * @param phoneE164 destination
     * @param templateName Meta template name
     * @param languageCode e.g. "pt_BR" or "en"
     * @param params ordered body parameters
     * @return provider message id
     */
    String sendTemplate(String phoneE164, String templateName, String languageCode, String... params);
}
