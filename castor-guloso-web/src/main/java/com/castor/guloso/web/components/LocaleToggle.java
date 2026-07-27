package com.castor.guloso.web.components;

import com.castor.guloso.web.config.CastorGulosoI18NProvider;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;

import java.util.Locale;

/**
 * Language switcher component.
 * Toggles between Brazilian Portuguese and English.
 */
public class LocaleToggle extends HorizontalLayout {

    public LocaleToggle() {
        setPadding(false);
        setMargin(false);
        setSpacing(true);

        Button ptBr = new Button("🇧🇷 PT", e -> switchLocale(CastorGulosoI18NProvider.LOCALE_PT_BR));
        Button en = new Button("🇺🇸 EN", e -> switchLocale(CastorGulosoI18NProvider.LOCALE_EN));

        ptBr.setSizeUndefined();
        en.setSizeUndefined();

        ptBr.getStyle().set("cursor", "pointer");
        en.getStyle().set("cursor", "pointer");

        add(ptBr, en);
    }

    private void switchLocale(Locale locale) {
        VaadinSession.getCurrent().setLocale(locale);
        // Refresh the UI to apply the new locale
        getUI().ifPresent(ui -> ui.getPage().reload());
    }
}
