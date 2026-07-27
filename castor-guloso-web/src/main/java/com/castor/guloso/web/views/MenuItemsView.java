package com.castor.guloso.web.views;

import com.castor.guloso.menu.api.MenuItemRequest;
import com.castor.guloso.menu.api.MenuItemResponse;
import com.castor.guloso.menu.service.MenuItemService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("menu-items")
@PermitAll
public class MenuItemsView extends VerticalLayout {

    private final MenuItemService menuItemService;
    private final Grid<MenuItemResponse> grid = new Grid<>(MenuItemResponse.class);

    public MenuItemsView(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
        setSizeFull();
        setPadding(true);

        add(new H2(getTranslation("menu.title")));

        // Toolbar
        var addBtn = new Button(getTranslation("menu.add"), e -> openForm(null));
        add(addBtn);

        // Grid
        grid.setColumns("id", "name", "description", "available");
        grid.getColumnByKey("id").setHeader("ID").setAutoWidth(true);
        grid.getColumnByKey("name").setHeader(getTranslation("menu.name")).setAutoWidth(true);
        grid.getColumnByKey("description").setHeader(getTranslation("menu.description"));
        grid.getColumnByKey("available").setHeader(getTranslation("menu.available")).setAutoWidth(true);

        // History button column
        grid.addColumn(new ComponentRenderer<>(item -> {
            var historyBtn = new Button(getTranslation("menu.price.view-history"), e ->
                    getUI().ifPresent(ui -> ui.navigate(
                            PriceHistoryView.class, item.getId())));
            historyBtn.getStyle().set("font-size", "var(--lumo-font-size-s)");
            return historyBtn;
        })).setHeader(getTranslation("menu.price.history"))
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                openForm(e.getValue());
            }
        });

        addAndExpand(grid);

        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(menuItemService.findAll());
    }

    private void openForm(MenuItemResponse existing) {
        var dialog = new Dialog();
        var title = new H2(existing == null ? getTranslation("menu.add") : getTranslation("menu.edit"));
        dialog.add(title);

        var nameField = new TextField(getTranslation("menu.name"));
        nameField.setWidthFull();

        var descField = new TextField(getTranslation("menu.description"));
        descField.setWidthFull();

        var availableCb = new Checkbox(getTranslation("menu.available"), true);

        if (existing != null) {
            nameField.setValue(existing.getName());
            descField.setValue(existing.getDescription() != null ? existing.getDescription() : "");
            availableCb.setValue(existing.isAvailable());
        }

        var form = new FormLayout(nameField, descField, availableCb);
        dialog.add(form);

        var saveBtn = new Button(getTranslation("common.save"), e -> {
            var request = MenuItemRequest.builder()
                    .name(nameField.getValue())
                    .description(descField.getValue().isEmpty() ? null : descField.getValue())
                    .available(availableCb.getValue())
                    .build();

            try {
                if (existing == null) {
                    menuItemService.create(request);
                    Notification.show(getTranslation("common.save"));
                } else {
                    menuItemService.update(existing.getId(), request);
                    Notification.show(getTranslation("common.save"));
                }
                dialog.close();
                refreshGrid();
            } catch (Exception ex) {
                Notification.show(getTranslation("error.generic") + ": " + ex.getMessage());
            }
        });

        var cancelBtn = new Button(getTranslation("common.cancel"), e -> dialog.close());
        dialog.add(new HorizontalLayout(saveBtn, cancelBtn));

        dialog.open();
    }
}
