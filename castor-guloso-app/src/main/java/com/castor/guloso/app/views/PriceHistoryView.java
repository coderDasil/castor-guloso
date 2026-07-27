package com.castor.guloso.app.views;

import com.castor.guloso.menu.api.MenuItemResponse;
import com.castor.guloso.menu.api.PriceHistoryResponse;
import com.castor.guloso.menu.service.MenuItemService;
import com.castor.guloso.menu.service.MenuPriceService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.text.NumberFormat;
import java.util.Locale;

@Route("menu-items/price-history")
@PermitAll
public class PriceHistoryView extends VerticalLayout implements HasUrlParameter<Long> {

    private final MenuPriceService menuPriceService;
    private final MenuItemService menuItemService;
    private final Grid<PriceHistoryResponse> grid = new Grid<>(PriceHistoryResponse.class);
    private final H2 title = new H2();
    private final Span currentPrice = new Span();

    public PriceHistoryView(MenuPriceService menuPriceService, MenuItemService menuItemService) {
        this.menuPriceService = menuPriceService;
        this.menuItemService = menuItemService;

        setSizeFull();
        setPadding(true);

        add(title);
        add(currentPrice);

        configureGrid();

        var backBtn = new Button(getTranslation("common.back"),
                e -> getUI().ifPresent(ui -> ui.navigate(MenuItemsView.class)));
        add(backBtn);

        addAndExpand(grid);
    }

    private void configureGrid() {
        grid.removeAllColumns();

        grid.addColumn(new LocalDateRenderer<>(PriceHistoryResponse::getEffectiveFrom, "dd/MM/yyyy"))
                .setHeader(getTranslation("menu.price.effective-from"))
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(new LocalDateRenderer<>(h -> h.getEffectiveTo(), "dd/MM/yyyy"))
                .setHeader(getTranslation("menu.price.effective-to"))
                .setAutoWidth(true);

        grid.addColumn(new NumberRenderer<>(
                        PriceHistoryResponse::getPrice,
                        NumberFormat.getCurrencyInstance(new Locale("pt", "BR")),
                        "R$ #,##0.00"
                ))
                .setHeader(getTranslation("menu.price.value"))
                .setTextAlign(ColumnTextAlign.END)
                .setAutoWidth(true);

        grid.addColumn(PriceHistoryResponse::getDaysActive)
                .setHeader(getTranslation("menu.price.days-active"))
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        grid.addColumn(new ComponentRenderer<>(history -> {
            if (history.isActive()) {
                var badge = new Span(getTranslation("menu.price.active"));
                badge.getStyle().set("color", "var(--lumo-success-color)");
                badge.getStyle().set("font-weight", "bold");
                return badge;
            }
            return new Span("");
        })).setHeader(getTranslation("menu.price.status"))
                .setAutoWidth(true);
    }

    @Override
    public void setParameter(BeforeEvent event, Long menuItemId) {
        menuItemService.findById(menuItemId).ifPresentOrElse(
                item -> {
                    title.setText(getTranslation("menu.price.history-title", item.getName()));

                    try {
                        var current = menuPriceService.getCurrentPrice(menuItemId);
                        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                        currentPrice.setText(getTranslation("menu.price.current-label", fmt.format(current.getPrice())));
                        currentPrice.getStyle().set("font-size", "1.2em");
                    } catch (Exception e) {
                        currentPrice.setText(getTranslation("menu.price.no-price"));
                    }

                    grid.setItems(menuPriceService.getPriceHistory(menuItemId));
                },
                () -> {
                    title.setText(getTranslation("error.not-found"));
                    currentPrice.setText("");
                }
        );
    }
}
