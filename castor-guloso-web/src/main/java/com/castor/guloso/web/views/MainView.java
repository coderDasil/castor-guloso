package com.castor.guloso.web.views;

import com.castor.guloso.web.components.LocaleToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * Landing page — dashboard hub with navigation.
 */
@Route("")
public class MainView extends VerticalLayout {

    public MainView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(new H1(getTranslation("app.title")));
        add(new Paragraph(getTranslation("app.tagline")));

        // Navigation buttons
        add(new Button(getTranslation("customer.title"),
                e -> getUI().ifPresent(ui -> ui.navigate(CustomersView.class))));
        add(new Button(getTranslation("menu.title"),
                e -> getUI().ifPresent(ui -> ui.navigate(MenuItemsView.class))));
        add(new Button(getTranslation("dailymenu.title"),
                e -> getUI().ifPresent(ui -> ui.navigate(DailyMenuView.class))));

        // Language switcher
        add(new LocaleToggle());
    }
}
