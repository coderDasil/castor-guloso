package com.castor.guloso.app.config;

import com.vaadin.flow.i18n.I18NProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Vaadin I18N provider for Castor Guloso.
 * <p>
 * Supports English (default) and Brazilian Portuguese.
 * Backed by {@code messages.properties} and {@code messages_pt_BR.properties}.
 */
@Component
public class CastorGulosoI18NProvider implements I18NProvider {

    private static final Logger log = LoggerFactory.getLogger(CastorGulosoI18NProvider.class);

    public static final Locale LOCALE_EN = Locale.US;
    public static final Locale LOCALE_PT_BR = new Locale("pt", "BR");

    private static final String BUNDLE_NAME = "messages";

    private static final List<Locale> PROVIDED_LOCALES = List.of(LOCALE_PT_BR, LOCALE_EN);

    @Override
    public List<Locale> getProvidedLocales() {
        return PROVIDED_LOCALES;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            log.warn("Null key requested");
            return "";
        }

        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale, this.getClass().getClassLoader());

        String value;
        try {
            value = bundle.getString(key);
        } catch (MissingResourceException e) {
            log.warn("Missing i18n key: {} for locale {}", key, locale);
            return "!" + key;
        }

        if (params != null && params.length > 0) {
            try {
                value = MessageFormat.format(value, params);
            } catch (IllegalArgumentException e) {
                log.error("Failed to format i18n key: {} with params: {}", key, params, e);
            }
        }

        return value;
    }
}
