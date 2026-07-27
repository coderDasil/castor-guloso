package com.castor.guloso.menu.service;

import com.castor.guloso.core.domain.MenuItem;
import com.castor.guloso.core.domain.MenuPrice;
import com.castor.guloso.menu.api.MenuPriceRequest;
import com.castor.guloso.menu.api.MenuPriceResponse;
import com.castor.guloso.menu.api.PriceHistoryResponse;
import com.castor.guloso.menu.domain.MenuItemRepository;
import com.castor.guloso.menu.domain.MenuPriceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MenuPriceService {

    private final MenuPriceRepository menuPriceRepository;
    private final MenuItemRepository menuItemRepository;

    /**
     * Get the current active price for a menu item.
     */
    public MenuPriceResponse getCurrentPrice(Long menuItemId) {
        List<MenuPrice> prices = menuPriceRepository.findCurrentPrice(menuItemId, LocalDate.now());
        if (prices.isEmpty()) {
            throw new EntityNotFoundException("No active price for menu item " + menuItemId);
        }
        return toResponse(prices.get(0));
    }

    /**
     * Get full price history for a menu item, newest first.
     */
    public List<PriceHistoryResponse> getPriceHistory(Long menuItemId) {
        return menuPriceRepository.findHistoryByItemId(menuItemId).stream()
                .map(this::toHistoryResponse)
                .toList();
    }

    /**
     * Get active price for all menu items as a map: itemId → price.
     */
    public Map<Long, BigDecimal> getCurrentPrices() {
        List<MenuItem> items = menuItemRepository.findAll();
        LocalDate today = LocalDate.now();

        return items.stream().collect(Collectors.toMap(
                MenuItem::getId,
                item -> {
                    List<MenuPrice> prices = menuPriceRepository.findCurrentPrice(item.getId(), today);
                    return prices.isEmpty() ? BigDecimal.ZERO : prices.get(0).getPrice();
                }
        ));
    }

    /**
     * Set a new price for a menu item.
     * Closes the current active price (sets effectiveTo) and creates a new record.
     */
    @Transactional
    public MenuPriceResponse setPrice(Long menuItemId, MenuPriceRequest request) {
        MenuItem item = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found: " + menuItemId));

        LocalDate effectiveFrom = request.getEffectiveFrom() != null
                ? request.getEffectiveFrom()
                : LocalDate.now();

        // Close the current active price if it exists
        List<MenuPrice> currentPrices = menuPriceRepository.findCurrentPrice(menuItemId, effectiveFrom);
        for (MenuPrice current : currentPrices) {
            current.setEffectiveTo(effectiveFrom.minusDays(1));
            menuPriceRepository.save(current);
        }

        // Create new price record
        MenuPrice newPrice = MenuPrice.builder()
                .menuItem(item)
                .price(request.getPrice())
                .effectiveFrom(effectiveFrom)
                .build();

        return toResponse(menuPriceRepository.save(newPrice));
    }

    private MenuPriceResponse toResponse(MenuPrice mp) {
        return MenuPriceResponse.builder()
                .id(mp.getId())
                .menuItemId(mp.getMenuItem().getId())
                .price(mp.getPrice())
                .effectiveFrom(mp.getEffectiveFrom())
                .effectiveTo(mp.getEffectiveTo())
                .createdAt(mp.getCreatedAt())
                .active(mp.getEffectiveTo() == null)
                .build();
    }

    private PriceHistoryResponse toHistoryResponse(MenuPrice mp) {
        long daysActive = 0;
        LocalDate from = mp.getEffectiveFrom();
        LocalDate to = mp.getEffectiveTo() != null ? mp.getEffectiveTo() : LocalDate.now();
        if (from != null && to != null) {
            daysActive = ChronoUnit.DAYS.between(from, to);
        }

        return PriceHistoryResponse.builder()
                .id(mp.getId())
                .menuItemId(mp.getMenuItem().getId())
                .price(mp.getPrice())
                .effectiveFrom(mp.getEffectiveFrom())
                .effectiveTo(mp.getEffectiveTo())
                .createdAt(mp.getCreatedAt())
                .active(mp.getEffectiveTo() == null)
                .daysActive((int) daysActive)
                .build();
    }
}
