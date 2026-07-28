package com.castor.guloso.app.views;

import com.castor.guloso.menu.api.MenuItemResponse;
import com.castor.guloso.menu.service.DailyMenuService;
import com.castor.guloso.menu.service.MenuPriceService;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Página pública do cardápio digital do Restaurante Quaresma.
 * Mobile-first, sem login. Estilo sofisticado com CSS dedicado.
 */
@Route("cardapio")
@AnonymousAllowed
public class CardapioView extends VerticalLayout {

    private final DailyMenuService dailyMenuService;
    private final MenuPriceService menuPriceService;

    private static final NumberFormat BRL = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final String WHATSAPP_NUMBER = "5511955551001";

    public CardapioView(DailyMenuService dailyMenuService, MenuPriceService menuPriceService) {
        this.dailyMenuService = dailyMenuService;
        this.menuPriceService = menuPriceService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        addClassName("cardapio-root");

        buildHero();
        buildInfoBar();
        buildMenuSection();
        buildReviewsSection();
        buildInfoSection();
        buildCTASection();
        buildFooter();
    }

    // ── Hero ──

    private void buildHero() {
        var hero = new Div();
        hero.addClassName("hero");

        var name = new H1("Restaurante Quaresma");
        var tagline = new Paragraph("Comida brasileira de verdade desde 1990");
        tagline.addClassName("tagline");
        var badge = new Span("★★★★★ 4,2 · 570+ avaliações no Google");
        badge.addClassName("badge");

        hero.add(name, tagline, badge);
        add(hero);
    }

    // ── Info Bar ──

    private void buildInfoBar() {
        var bar = new Div();
        bar.addClassName("info-bar");
        bar.setText("🛵 Delivery e retirada  ·  Seg–Sáb 09h–17h  ·  Rua Ipanema, 488");
        add(bar);
    }

    // ── Menu Section ──

    private void buildMenuSection() {
        var section = new Div();
        section.addClassName("menu-section");

        var title = new H2("📋 Cardápio de Hoje");
        title.addClassName("section-title");

        var subtitle = new Paragraph("Pratos preparados com carinho, ingredientes frescos");
        subtitle.addClassName("section-subtitle");

        section.add(title, subtitle);

        dailyMenuService.findToday().ifPresentOrElse(
                menu -> {
                    if (menu.getItems().isEmpty()) {
                        section.add(emptyState("😕", "Nenhum item cadastrado para hoje"));
                    } else {
                        Map<Long, BigDecimal> prices = menuPriceService.getCurrentPrices();
                        for (MenuItemResponse item : menu.getItems()) {
                            section.add(buildMenuItemCard(item,
                                    prices.getOrDefault(item.getId(), BigDecimal.ZERO)));
                        }
                    }
                },
                () -> section.add(emptyState("📝",
                        "Cardápio de hoje ainda não foi definido",
                        "Volte mais tarde para conferir nossas opções!"))
        );

        add(section);
    }

    private Div buildMenuItemCard(MenuItemResponse item, BigDecimal price) {
        var card = new Div();
        card.addClassName("menu-card");

        var header = new Div();
        header.addClassName("item-header");

        var name = new Span(item.getName());
        name.addClassName("item-name");

        var priceSpan = new Span(BRL.format(price));
        priceSpan.addClassName("item-price");

        header.add(name, priceSpan);
        card.add(header);

        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            var desc = new Paragraph(item.getDescription());
            desc.addClassName("item-desc");
            card.add(desc);
        }

        var msg = "Olá! Gostaria de pedir o " + item.getName()
                + " (" + BRL.format(price) + ") para o almoço de hoje.";
        var waLink = "https://wa.me/" + WHATSAPP_NUMBER + "?text="
                + URLEncoder.encode(msg, StandardCharsets.UTF_8);

        var orderBtn = new Anchor(waLink, "🛒 Pedir via WhatsApp");
        orderBtn.addClassName("whatsapp-btn");
        orderBtn.setTarget("_blank");

        card.add(orderBtn);
        return card;
    }

    private Div emptyState(String emoji, String line1) {
        return emptyState(emoji, line1, null);
    }

    private Div emptyState(String emoji, String line1, String line2) {
        var div = new Div();
        div.addClassName("empty-state");
        var emojiSpan = new Span(emoji);
        emojiSpan.addClassName("emoji");
        div.add(emojiSpan);
        div.add(new Paragraph(line1));
        if (line2 != null) {
            div.add(new Paragraph(line2));
        }
        return div;
    }

    // ── Reviews ──

    private void buildReviewsSection() {
        var section = new Div();
        section.addClassName("reviews-section");

        var title = new H2("⭐ O que Nossos Clientes Dizem");
        title.addClassName("section-title");
        section.add(title);

        var badge = new Div();
        badge.addClassName("rating-badge");
        badge.setText("★★★★★ 4,2 · 570+ avaliações no Google");
        section.add(badge);

        addTestimonial(section, "★★★★★", "Maria S.",
                "Sempre peço o Prato Feito, é delicioso e o atendimento é muito rápido! Melhor almoço da Mooca.");
        addTestimonial(section, "★★★★★", "Carlos A.",
                "Comida caseira de primeira. O Filé de Frango é sensacional. Entrega sempre no horário.");
        addTestimonial(section, "★★★★★", "Ana P.",
                "Cardápio variado e preço justo. O Vegetariano do Dia é uma ótima opção para quem não come carne.");
        addTestimonial(section, "★★★★★", "João M.",
                "Pedido pelo WhatsApp é super prático. Em 30 minutos está na mesa. Recomendo demais!");

        add(section);
    }

    private void addTestimonial(Div parent, String stars, String author, String text) {
        var card = new Div();
        card.addClassName("review-card");

        var starsSpan = new Span(stars);
        starsSpan.addClassName("stars");

        var textSpan = new Paragraph(text);
        textSpan.addClassName("text");

        var authorSpan = new Span("— " + author);
        authorSpan.addClassName("author");

        card.add(starsSpan, textSpan, authorSpan);
        parent.add(card);
    }

    // ── Location & Info ──

    private void buildInfoSection() {
        var section = new Div();
        section.addClassName("info-section");

        var title = new H2("📍 Como Chegar");
        title.addClassName("section-title");
        section.add(title);

        var card = new Div();
        card.addClassName("info-card");

        // Row: Address
        card.add(infoRow("📍", "Endereço", "Rua Ipanema, 488 — Mooca, São Paulo"));
        // Row: Hours
        card.add(infoRow("🕐", "Horários", "Segunda a Sábado: 09h às 17h  ·  Domingo: Fechado"));
        // Row: Payment
        card.add(infoRow("💳", "Pagamento", "Cartões de crédito/débito, dinheiro, PIX"));
        // Row: Map
        section.add(card);

        var mapLink = new Anchor(
                "https://www.google.com/maps/place/Restaurante+Quaresma/@-23.5476651,-46.6074103,17z",
                "📍 Ver no Google Maps"
        );
        mapLink.setTarget("_blank");
        mapLink.addClassName("map-link");
        mapLink.getStyle().set("display", "inline-block").set("margin-top", "0.75rem");
        section.add(mapLink);

        add(section);
    }

    private Div infoRow(String icon, String label, String value) {
        var row = new Div();
        row.addClassName("info-row");

        var iconSpan = new Span(icon);
        iconSpan.addClassName("info-icon");

        var body = new Div();
        var labelSpan = new Span(label);
        labelSpan.addClassName("info-label");
        var valueSpan = new Span(value);
        valueSpan.addClassName("info-value");

        body.add(labelSpan);body.add(valueSpan);

        row.add(iconSpan, body);
        return row;
    }

    // ── CTA ──

    private void buildCTASection() {
        var wrapper = new Div();
        wrapper.addClassName("cta-wrapper");

        var msg = "Olá! Gostaria de fazer um pedido.";
        var waLink = "https://wa.me/" + WHATSAPP_NUMBER + "?text="
                + URLEncoder.encode(msg, StandardCharsets.UTF_8);

        var btn = new Anchor(waLink, "💬 Fazer Pedido Agora");
        btn.addClassName("cta-btn");
        btn.setTarget("_blank");

        wrapper.add(btn);
        add(wrapper);
    }

    // ── Footer ──

    private void buildFooter() {
        var footer = new Div();
        footer.addClassName("footer");
        footer.setText("🦫 Castor Guloso © 2026 — Tecnologia a serviço da sua fome");
        add(footer);
    }
}
