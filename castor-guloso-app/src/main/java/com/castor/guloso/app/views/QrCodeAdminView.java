package com.castor.guloso.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

/**
 * Página administrativa para geração e download de QR Codes.
 */
@Route("qrcode-admin")
@PermitAll
public class QrCodeAdminView extends VerticalLayout {

    private final Image qrImage = new Image();
    private final Anchor downloadLink = new Anchor();

    public QrCodeAdminView() {
        setSizeFull();
        setPadding(true);

        var title = new H2("📱 QR Code do Cardápio");
        add(title);

        add(new Paragraph("Gere QR Codes para imprimir e colocar nas mesas."));

        // Base URL
        var baseUrlField = new TextField("URL base do servidor");
        baseUrlField.setValue("http://192.168.X.X:7080");
        baseUrlField.setWidthFull();
        baseUrlField.setHelperText("Use o IP do servidor na rede local (ex: 192.168.0.10:7080)");

        // Mesa
        var mesaField = new IntegerField("Nº da mesa (opcional)");
        mesaField.setMin(1);
        mesaField.setMax(99);
        mesaField.setHelperText("Deixe vazio para QR code genérico");

        var generateBtn = new Button("🔄 Gerar QR Code", e -> {
            String baseUrl = baseUrlField.getValue();
            Integer mesa = mesaField.getValue();
            String src = "/api/qrcode?baseUrl=" + baseUrl;
            if (mesa != null) {
                src += "&mesa=" + mesa;
            }
            qrImage.setSrc(src);
            qrImage.setWidth("300px");
            qrImage.setHeight("300px");

            downloadLink.setHref(src);
            downloadLink.setText("📥 Baixar PNG" + (mesa != null ? " (Mesa " + mesa + ")" : ""));
            downloadLink.getElement().setAttribute("download", "qrcode-cardapio" +
                    (mesa != null ? "-mesa" + mesa : "") + ".png");
        });

        add(baseUrlField, mesaField, generateBtn);

        // QR code display
        var qrSection = new VerticalLayout();
        qrSection.setPadding(true);
        qrSection.setAlignItems(Alignment.CENTER);
        qrSection.addClassName("menu-card");
        qrSection.getStyle()
                .set("background", "white")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.08)")
                .set("padding", "1.5rem")
                .set("max-width", "400px");

        qrImage.setWidth("300px");
        qrImage.setHeight("300px");
        qrSection.add(qrImage);

        downloadLink.getStyle()
                .set("display", "inline-block")
                .set("margin-top", "1rem")
                .set("color", "#c8722e")
                .set("font-weight", "500");
        qrSection.add(downloadLink);

        add(qrSection);

        // Usage instructions
        var usage = new Div();
        usage.getStyle().set("margin-top", "2rem");
        usage.add(new H3("📋 Como usar"));
        usage.add(new Paragraph("1. Substitua a URL pelo IP do servidor na rede local"));
        usage.add(new Paragraph("2. Escolha o número da mesa (opcional)"));
        usage.add(new Paragraph("3. Clique em 'Gerar QR Code'"));
        usage.add(new Paragraph("4. Baixe o PNG e imprima"));
        usage.add(new Paragraph("5. Cole o QR code no material da mesa"));
        usage.add(new Paragraph("6. Clientes escaneiam e acessam o cardápio digital"));

        add(usage);
    }
}
