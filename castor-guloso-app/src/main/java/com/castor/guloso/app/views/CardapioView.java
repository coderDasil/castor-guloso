package com.castor.guloso.app.views;

import com.castor.guloso.menu.api.DailyMenuResponse;
import com.castor.guloso.menu.api.MenuItemResponse;
import com.castor.guloso.menu.api.MenuPriceResponse;
import com.castor.guloso.menu.service.DailyMenuService;
import com.castor.guloso.menu.service.MenuPriceService;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Página pública do cardápio digital do Restaurante Quaresma.
 * Mobile-first, sem necessidade de login.
 */
@Route("cardapio")
@AnonymousAllowed
public class CardapioView extends VerticalLayout {

    private final DailyMenuService dailyMenuService;
    private final MenuPriceService menuPriceService;

    private static final NumberFormat BRL = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public CardapioView(DailyMenuService dailyMenuService, MenuPriceService menuPriceService) {
        this.dailyMenuService = dailyMenuService;
        this.menuPriceService = menuPriceService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
                .set("background", "linear-gradient(180deg, #fff8f0 0%, #fff 100%)")
                .set("font-family", "'Segoe UI', Roboto, sans-serif");

        buildHeader();
        buildDeliveryInfo();
        buildMenuSection();
        buildReviewsSection();
        buildInfoSection();
        buildWhatsAppButton();
        buildFooter();
    }

    private void buildHeader() {
        var header = new VerticalLayout();
        header.setPadding(true);
        header.setAlignItems(Alignment.CENTER);
        header.getStyle()
                .set("background", "linear-gradient(135deg, #c8722e 0%, #8b5e2b 100%)")
                .set("color", "white")
                .set("text-align", "center")
                .set("padding", "2rem 1rem");

        var name = new H1("Restaurante Quaresma");
        name.getStyle()
                .set("margin", "0")
                .set("font-size", "1.8rem")
                .set("font-weight", "700");

        var tagline = new Paragraph("Comida brasileira de verdade — desde 1990");
        tagline.getStyle()
                .set("margin", "0.5rem 0 0")
                .set("font-size", "1rem")
                .set("opacity", "0.9");

        header.add(name, tagline);
        add(header);
    }

    private void buildDeliveryInfo() {
        var info = new Paragraph("🛵 Delivery e retirada na loja • Seg–Sáb 09h–17h");
        info.getStyle()
                .set("text-align", "center")
                .set("padding", "0.75rem 1rem")
                .set("margin", "0")
                .set("background", "#f0e6d8")
                .set("font-size", "0.9rem")
                .set("font-weight", "500");
        add(info);
    }

    private void buildMenuSection() {
        var section = new VerticalLayout();
        section.setPadding(true);
        section.getStyle().set("max-width", "600px").set("margin", "0 auto").set("width", "100%");

        var title = new H2("📋 Cardápio de Hoje");
        title.getStyle()
                .set("font-size", "1.3rem")
                .set("color", "#5a3e1b")
                .set("margin", "1rem 0 0.5rem");

        section.add(title);

        // Try to load today's menu
        dailyMenuService.findToday().ifPresentOrElse(
                menu -> {
                    if (menu.getItems().isEmpty()) {
                        section.add(new Paragraph("Nenhum item cadastrado para hoje."));
                    } else {
                        Map<Long, java.math.BigDecimal> prices = menuPriceService.getCurrentPrices();
                        for (MenuItemResponse item : menu.getItems()) {
                            section.add(buildMenuItemCard(item, prices.getOrDefault(item.getId(), java.math.BigDecimal.ZERO)));
                        }
                    }
                },
                () -> {
                    section.add(new Paragraph("Cardápio de hoje ainda não foi definido."));
                    section.add(new Paragraph("Volte mais tarde!"));
                }
        );

        add(section);
    }

    private VerticalLayout buildMenuItemCard(MenuItemResponse item, java.math.BigDecimal price) {
        var card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(false);
        card.getStyle()
                .set("background", "white")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.08)")
                .set("padding", "1rem")
                .set("margin-bottom", "0.75rem");

        var nameRow = new HorizontalLayout();
        nameRow.setWidthFull();
        nameRow.setAlignItems(Alignment.CENTER);

        var name = new Span(item.getName());
        name.getStyle()
                .set("font-size", "1.1rem")
                .set("font-weight", "600")
                .set("color", "#333");

        var priceSpan = new Span(BRL.format(price));
        priceSpan.getStyle()
                .set("font-size", "1.1rem")
                .set("font-weight", "700")
                .set("color", "#c8722e")
                .set("margin-left", "auto");

        nameRow.add(name, priceSpan);
        card.add(nameRow);

        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            var desc = new Paragraph(item.getDescription());
            desc.getStyle()
                    .set("font-size", "0.85rem")
                    .set("color", "#666")
                    .set("margin", "0.3rem 0 0");
            card.add(desc);
        }

        // WhatsApp order button
        var orderBtn = new Anchor(
                "https://wa.me/5511955551001?text=" + java.net.URLEncoder.encode(
                        "Olá! Gostaria de pedir o " + item.getName() + " (" + BRL.format(price) + ") para o almoço de hoje.",
                        java.nio.charset.StandardCharsets.UTF_8),
                "🛒 Pedir via WhatsApp"
        );
        orderBtn.getStyle()
                .set("display", "inline-block")
                .set("background", "#25D366")
                .set("color", "white")
                .set("padding", "0.5rem 1rem")
                .set("border-radius", "8px")
                .set("text-decoration", "none")
                .set("font-size", "0.85rem")
                .set("font-weight", "500")
                .set("margin-top", "0.5rem")
                .set("text-align", "center");

        orderBtn.setTarget("_blank");
        card.add(orderBtn);

        return card;
    }

    private void buildReviewsSection() {
        var section = new VerticalLayout();
        section.setPadding(true);
        section.getStyle()
                .set("max-width", "600px")
                .set("margin", "1rem auto")
                .set("width", "100%");

        var title = new H2("⭐ O que nossos clientes dizem");
        title.getStyle().set("font-size", "1.3rem").set("color", "#5a3e1b");

        section.add(title);

        // Google rating badge
        var ratingBadge = new Paragraph("★★★★★ 4,2 • 570+ avaliações no Google");
        ratingBadge.getStyle()
                .set("font-size", "1rem")
                .set("color", "#e67e22")
                .set("margin", "0.3rem 0");

        section.add(ratingBadge);

        // Curated testimonials
        addTestimonial(section, "Maria S.", "Sempre peço o Prato Feito, é delicioso e o atendimento é muito rápido! Melhor almoço da Mooca.");
        addTestimonial(section, "Carlos A.", "Comida caseira de primeira. O Filé de Frango é sensacional. Entrega sempre no horário.");
        addTestimonial(section, "Ana Paula", "Cardápio variado e preço justo. O Vegetariano do Dia é uma ótima opção para quem não come carne.");
        addTestimonial(section, "João Mendes", "Pedido pelo WhatsApp é super prático. Em 30 minutos está na mesa. Recomendo demais!");

        add(section);
    }

    private void addTestimonial(VerticalLayout parent, String name, String text) {
        var card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(false);
        card.getStyle()
                .set("background", "white")
                .set("border-radius", "10px")
                .set("box-shadow", "0 1px 4px rgba(0,0,0,0.06)")
                .set("padding", "0.75rem 1rem")
                .set("margin-bottom", "0.5rem");

        var stars = new Span("★★★★★");
        stars.getStyle().set("color", "#f1c40f").set("font-size", "0.9rem");

        var reviewText = new Paragraph(text);
        reviewText.getStyle()
                .set("font-size", "0.9rem")
                .set("color", "#444")
                .set("margin", "0.3rem 0")
                .set("font-style", "italic");

        var author = new Span("— " + name);
        author.getStyle()
                .set("font-size", "0.8rem")
                .set("color", "#888");

        card.add(stars, reviewText, author);
        parent.add(card);
    }

    private void buildInfoSection() {
        var section = new VerticalLayout();
        section.setPadding(true);
        section.getStyle()
                .set("max-width", "600px")
                .set("margin", "0 auto")
                .set("width", "100%");

        var title = new H2("📍 Localização");
        title.getStyle().set("font-size", "1.3rem").set("color", "#5a3e1b");

        section.add(title);

        section.add(new Paragraph("Rua Ipanema, 488 — Mooca, São Paulo"));
        section.add(new Paragraph("Segunda a Sábado: 09h — 17h"));
        section.add(new Paragraph("Domingo: Fechado"));

        var mapLink = new Anchor(
                "https://www.google.com/maps/place/Restaurante+Quaresma/@-23.5476651,-46.6074103,17z",
                "📍 Ver no Google Maps"
        );
        mapLink.setTarget("_blank");
        mapLink.getStyle()
                .set("color", "#c8722e")
                .set("font-weight", "500");
        section.add(mapLink);

        // Payment methods
        var paymentTitle = new Paragraph("💳 Aceitamos: cartões de crédito/débito, dinheiro, PIX");
        paymentTitle.getStyle()
                .set("font-size", "0.85rem")
                .set("color", "#666")
                .set("margin-top", "0.5rem");
        section.add(paymentTitle);

        add(section);
    }

    private void buildWhatsAppButton() {
        var wrapper = new VerticalLayout();
        wrapper.setPadding(true);
        wrapper.setAlignItems(Alignment.CENTER);
        wrapper.getStyle().set("padding", "1.5rem 1rem");

        var btn = new Anchor(
                "https://wa.me/5511955551001?text=Olá! Gostaria de fazer um pedido.",
                "💬 Fazer Pedido Agora"
        );
        btn.setTarget("_blank");
        btn.getStyle()
                .set("display", "inline-block")
                .set("background", "#25D366")
                .set("color", "white")
                .set("padding", "1rem 2rem")
                .set("border-radius", "50px")
                .set("text-decoration", "none")
                .set("font-size", "1.1rem")
                .set("font-weight", "600")
                .set("box-shadow", "0 4px 15px rgba(37, 211, 102, 0.3)")
                .set("text-align", "center");

        wrapper.add(btn);
        add(wrapper);
    }

    private void buildFooter() {
        var footer = new Paragraph("🦫 Castor Guloso © 2026 — Tecnologia a serviço da sua fome");
        footer.getStyle()
                .set("text-align", "center")
                .set("padding", "1rem")
                .set("margin", "0")
                .set("background", "#2c3e50")
                .set("color", "white")
                .set("font-size", "0.8rem");
        add(footer);
    }
}
