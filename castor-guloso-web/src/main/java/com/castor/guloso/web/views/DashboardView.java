package com.castor.guloso.web.views;

import com.castor.guloso.core.enums.OrderStatus;
import com.castor.guloso.order.api.KitchenSummaryItem;
import com.castor.guloso.order.api.OrderResponse;
import com.castor.guloso.order.service.OrderSessionService;
import com.castor.guloso.whatsapp.service.WhatsAppMessageService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

/**
 * Live order board — replaces the physical notebook for the owner.
 */
@Route("dashboard")
@PageTitle("Dashboard | Castor Guloso")
public class DashboardView extends VerticalLayout {

    private final OrderSessionService orderSessionService;
    private final WhatsAppMessageService whatsAppMessageService;

    private final Grid<OrderResponse> grid = new Grid<>(OrderResponse.class, false);
    private final Span statsLabel = new Span();
    private final Grid<KitchenSummaryItem> summaryGrid = new Grid<>(KitchenSummaryItem.class, false);

    public DashboardView(OrderSessionService orderSessionService,
                         WhatsAppMessageService whatsAppMessageService) {
        this.orderSessionService = orderSessionService;
        this.whatsAppMessageService = whatsAppMessageService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2(getTranslation("dashboard.title")));

        // Action bar
        Button sendBtn = new Button(getTranslation("dashboard.send"), e -> doSend());
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button refreshBtn = new Button(getTranslation("dashboard.refresh"), e -> refresh());
        Button finalizeBtn = new Button(getTranslation("dashboard.finalize"), e -> doFinalize());
        finalizeBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout actions = new HorizontalLayout(sendBtn, refreshBtn, finalizeBtn);
        actions.setAlignItems(Alignment.CENTER);
        add(actions);

        statsLabel.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.FontWeight.SEMIBOLD);
        add(statsLabel);

        // Orders grid
        configureOrderGrid();
        add(grid);
        grid.setSizeFull();

        // Kitchen summary
        add(new H3(getTranslation("dashboard.summary")));
        configureSummaryGrid();
        add(summaryGrid);
        summaryGrid.setHeight("200px");

        refresh();
    }

    private void configureOrderGrid() {
        grid.addColumn(OrderResponse::getCustomerName)
                .setHeader(getTranslation("dashboard.col.customer"))
                .setAutoWidth(true);
        grid.addColumn(OrderResponse::getCustomerPhone)
                .setHeader(getTranslation("dashboard.col.phone"))
                .setAutoWidth(true);
        grid.addColumn(o -> {
                    if (o.getMenuItemName() != null) return o.getMenuItemName();
                    if (o.getFreeTextItem() != null) return o.getFreeTextItem();
                    return "—";
                })
                .setHeader(getTranslation("dashboard.col.item"))
                .setAutoWidth(true);
        grid.addColumn(o -> statusLabel(o.getStatus()))
                .setHeader(getTranslation("dashboard.col.status"))
                .setAutoWidth(true);
        grid.addColumn(o -> o.getRepliedAt() != null ? o.getRepliedAt().toString() : "—")
                .setHeader(getTranslation("dashboard.col.replied"))
                .setAutoWidth(true);
    }

    private void configureSummaryGrid() {
        summaryGrid.addColumn(KitchenSummaryItem::getMenuItemName)
                .setHeader(getTranslation("dashboard.col.item"))
                .setAutoWidth(true);
        summaryGrid.addColumn(KitchenSummaryItem::getQuantity)
                .setHeader(getTranslation("dashboard.col.qty"))
                .setAutoWidth(true);
        summaryGrid.addColumn(i -> Boolean.TRUE.equals(i.getFreeText()) ? "✓" : "")
                .setHeader("Free text")
                .setAutoWidth(true);
    }

    private String statusLabel(OrderStatus status) {
        if (status == null) return "?";
        return switch (status) {
            case PENDING -> "🟡 " + getTranslation("order.status.pending");
            case CONFIRMED -> "✅ " + getTranslation("order.status.confirmed");
            case NO_REPLY -> "🔴 " + getTranslation("order.status.noreply");
        };
    }

    private void refresh() {
        List<OrderResponse> orders = orderSessionService.listOrdersForToday();
        grid.setItems(orders);

        long pending = orders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count();
        long confirmed = orders.stream().filter(o -> o.getStatus() == OrderStatus.CONFIRMED).count();
        long noReply = orders.stream().filter(o -> o.getStatus() == OrderStatus.NO_REPLY).count();
        statsLabel.setText(String.format(
                getTranslation("dashboard.stats"),
                orders.size(), pending, confirmed, noReply));

        try {
            summaryGrid.setItems(orderSessionService.kitchenSummary());
        } catch (Exception e) {
            summaryGrid.setItems(List.of());
        }
    }

    private void doSend() {
        try {
            int sent = whatsAppMessageService.sendDailyLunchRequests();
            Notification n = Notification.show(
                    getTranslation("dashboard.send.ok", sent),
                    4000, Notification.Position.TOP_CENTER);
            n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            refresh();
        } catch (Exception e) {
            Notification n = Notification.show(
                    getTranslation("dashboard.send.fail") + ": " + e.getMessage(),
                    5000, Notification.Position.TOP_CENTER);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void doFinalize() {
        try {
            orderSessionService.finalizeSession();
            Notification n = Notification.show(
                    getTranslation("dashboard.finalize.ok"),
                    4000, Notification.Position.TOP_CENTER);
            n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            refresh();
        } catch (Exception e) {
            Notification n = Notification.show(
                    getTranslation("dashboard.finalize.fail") + ": " + e.getMessage(),
                    5000, Notification.Position.TOP_CENTER);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
