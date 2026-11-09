package com.castor.guloso.web.views;

import com.castor.guloso.core.domain.Tag;
import com.castor.guloso.customer.api.CustomerRequest;
import com.castor.guloso.customer.api.CustomerResponse;
import com.castor.guloso.customer.service.CustomerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("customers")
@PermitAll
public class CustomersView extends VerticalLayout {

    private final CustomerService customerService;
    private final Grid<CustomerResponse> grid = new Grid<>(CustomerResponse.class, false);

    public CustomersView(CustomerService customerService) {
        this.customerService = customerService;
        setSizeFull();
        setPadding(true);

        add(new H2(getTranslation("customer.title")));

        var addBtn = new Button(getTranslation("customer.add"), e -> openForm(null));
        add(addBtn);

        grid.addColumn(CustomerResponse::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(CustomerResponse::getName).setHeader(getTranslation("customer.name")).setAutoWidth(true);
        grid.addColumn(CustomerResponse::getPhone).setHeader(getTranslation("customer.phone")).setAutoWidth(true);
        grid.addColumn(c -> c.isActive()
                        ? getTranslation("customer.active")
                        : getTranslation("customer.inactive"))
                .setHeader(getTranslation("customer.active")).setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<>(customer -> {
            HorizontalLayout badges = new HorizontalLayout();
            badges.setSpacing(true);
            if (customer.getTags() != null) {
                for (CustomerResponse.TagSummary tag : customer.getTags()) {
                    Span badge = new Span(tag.getName());
                    badge.getStyle()
                            .set("background-color", tag.getColor() != null ? tag.getColor() : "#888")
                            .set("color", "white")
                            .set("padding", "2px 8px")
                            .set("border-radius", "12px")
                            .set("font-size", "var(--lumo-font-size-xs)");
                    badges.add(badge);
                }
            }
            return badges;
        })).setHeader(getTranslation("customer.tags")).setFlexGrow(1);

        grid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                openForm(e.getValue());
            }
        });

        addAndExpand(grid);
        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(customerService.findAll());
    }

    private void openForm(CustomerResponse existing) {
        Dialog dialog = new Dialog();
        dialog.setWidth("480px");
        dialog.add(new H2(existing == null
                ? getTranslation("customer.add")
                : getTranslation("customer.edit")));

        TextField nameField = new TextField(getTranslation("customer.name"));
        nameField.setWidthFull();
        nameField.setRequired(true);

        TextField phoneField = new TextField(getTranslation("customer.phone"));
        phoneField.setWidthFull();
        phoneField.setRequired(true);

        Checkbox activeCb = new Checkbox(getTranslation("customer.active"), true);

        TextArea notesField = new TextArea(getTranslation("customer.notes"));
        notesField.setWidthFull();
        notesField.setMaxHeight("100px");

        if (existing != null) {
            nameField.setValue(existing.getName());
            phoneField.setValue(existing.getPhone());
            activeCb.setValue(existing.isActive());
            notesField.setValue(existing.getNotes() != null ? existing.getNotes() : "");
        }

        FormLayout form = new FormLayout(nameField, phoneField, activeCb, notesField);
        dialog.add(form);

        // Tag management (only when editing)
        if (existing != null) {
            HorizontalLayout tagRow = new HorizontalLayout();
            tagRow.setWidthFull();
            tagRow.setAlignItems(Alignment.END);

            ComboBox<Tag> tagCombo = new ComboBox<>(getTranslation("customer.add-tag"));
            tagCombo.setItems(customerService.listActiveTags());
            tagCombo.setItemLabelGenerator(Tag::getName);
            tagCombo.setWidthFull();

            Button addTagBtn = new Button(getTranslation("customer.add-tag"), e -> {
                Tag selected = tagCombo.getValue();
                if (selected != null) {
                    try {
                        customerService.addTag(existing.getId(), selected.getId(), null);
                        Notification.show(getTranslation("common.save"));
                        dialog.close();
                        refreshGrid();
                    } catch (Exception ex) {
                        Notification.show(getTranslation("error.generic") + ": " + ex.getMessage());
                    }
                }
            });
            tagRow.add(tagCombo, addTagBtn);
            tagRow.setFlexGrow(1, tagCombo);
            dialog.add(tagRow);
        }

        Button saveBtn = new Button(getTranslation("common.save"), e -> {
            CustomerRequest request = CustomerRequest.builder()
                    .name(nameField.getValue())
                    .phone(phoneField.getValue())
                    .active(activeCb.getValue())
                    .notes(notesField.getValue())
                    .build();

            try {
                if (existing == null) {
                    customerService.create(request);
                } else {
                    customerService.update(existing.getId(), request);
                }
                Notification.show(getTranslation("common.save"));
                dialog.close();
                refreshGrid();
            } catch (Exception ex) {
                Notification.show(getTranslation("error.generic") + ": " + ex.getMessage());
            }
        });

        Button cancelBtn = new Button(getTranslation("common.cancel"), e -> dialog.close());

        HorizontalLayout actions = new HorizontalLayout(saveBtn, cancelBtn);
        if (existing != null) {
            Button deactivateBtn = new Button(getTranslation("customer.inactive"), e -> {
                customerService.deactivate(existing.getId());
                Notification.show(getTranslation("common.save"));
                dialog.close();
                refreshGrid();
            });
            actions.add(deactivateBtn);
        }
        dialog.add(actions);
        dialog.open();
    }
}
