package com.castor.guloso.app.views;

import com.castor.guloso.menu.api.DailyMenuRequest;
import com.castor.guloso.menu.api.DailyMenuResponse;
import com.castor.guloso.menu.api.MenuItemResponse;
import com.castor.guloso.menu.service.DailyMenuService;
import com.castor.guloso.menu.service.MenuItemService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Route("today-menu")
@PermitAll
public class DailyMenuView extends VerticalLayout {

    private final DailyMenuService dailyMenuService;
    private final MenuItemService menuItemService;

    private final CheckboxGroup<MenuItemResponse> itemsGroup = new CheckboxGroup<>();
    private final Paragraph currentMenuInfo = new Paragraph();

    public DailyMenuView(DailyMenuService dailyMenuService, MenuItemService menuItemService) {
        this.dailyMenuService = dailyMenuService;
        this.menuItemService = menuItemService;

        setSizeFull();
        setPadding(true);

        add(new H2(getTranslation("dailymenu.title")));

        // Show current menu info
        add(currentMenuInfo);
        refreshCurrentInfo();

        // Available items picker
        add(new Paragraph(getTranslation("dailymenu.items")));

        itemsGroup.setItems(menuItemService.findAvailable());
        itemsGroup.setItemLabelGenerator(MenuItemResponse::getName);
        add(itemsGroup);

        // Save button
        var saveBtn = new Button(getTranslation("common.save"), e -> saveMenu());
        add(saveBtn);
    }

    private void refreshCurrentInfo() {
        dailyMenuService.findToday().ifPresentOrElse(
                menu -> {
                    var names = menu.getItems().stream()
                            .map(MenuItemResponse::getName)
                            .collect(Collectors.joining(", "));
                    currentMenuInfo.setText("📋 " + getTranslation("dailymenu.date") + ": "
                            + menu.getMenuDate() + " → " + names);
                    itemsGroup.setValue(menu.getItems().stream()
                            .collect(Collectors.toSet()));
                },
                () -> currentMenuInfo.setText(getTranslation("common.no-results"))
        );
    }

    private void saveMenu() {
        Set<Long> selectedIds = itemsGroup.getSelectedItems().stream()
                .map(MenuItemResponse::getId)
                .collect(Collectors.toSet());

        var request = DailyMenuRequest.builder()
                .menuDate(LocalDate.now())
                .menuItemIds(selectedIds)
                .build();

        try {
            dailyMenuService.setDailyMenu(request);
            Notification.show(getTranslation("common.save"));
            refreshCurrentInfo();
        } catch (Exception e) {
            Notification.show(getTranslation("error.generic") + ": " + e.getMessage());
        }
    }
}
