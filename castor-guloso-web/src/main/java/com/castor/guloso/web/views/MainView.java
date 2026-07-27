package com.castor.guloso.web.views;

import com.castor.guloso.web.components.LocaleToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * Landing page — will evolve into the admin dashboard.
 */
@Route("")
public class MainView extends VerticalLayout {

    public MainView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(new H1(getTranslation("app.title")));
        add(new Paragraph(getTranslation("app.tagline")));

        // Language switcher
        add(new LocaleToggle());
    }
}
