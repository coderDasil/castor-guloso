package com.castor.guloso.menu.service;

import com.castor.guloso.core.domain.DailyMenu;
import com.castor.guloso.core.domain.MenuItem;
import com.castor.guloso.menu.api.DailyMenuRequest;
import com.castor.guloso.menu.api.DailyMenuResponse;
import com.castor.guloso.menu.api.MenuItemResponse;
import com.castor.guloso.menu.domain.DailyMenuRepository;
import com.castor.guloso.menu.domain.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DailyMenuService {

    private final DailyMenuRepository dailyMenuRepository;
    private final MenuItemRepository menuItemRepository;

    public Optional<DailyMenuResponse> findToday() {
        return findByDate(LocalDate.now());
    }

    public Optional<DailyMenuResponse> findByDate(LocalDate date) {
        return dailyMenuRepository.findByMenuDate(date).map(this::toResponse);
    }

    public List<DailyMenuResponse> findAll() {
        return dailyMenuRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public DailyMenuResponse setDailyMenu(DailyMenuRequest request) {
        LocalDate date = request.getMenuDate() != null ? request.getMenuDate() : LocalDate.now();

        // Upsert: remove existing menu for date if any
        dailyMenuRepository.findByMenuDate(date).ifPresent(dailyMenuRepository::delete);

        List<MenuItem> items = menuItemRepository.findAllById(request.getMenuItemIds());

        DailyMenu menu = DailyMenu.builder()
                .menuDate(date)
                .items(items)
                .build();

        return toResponse(dailyMenuRepository.save(menu));
    }

    private DailyMenuResponse toResponse(DailyMenu menu) {
        return DailyMenuResponse.builder()
                .id(menu.getId())
                .menuDate(menu.getMenuDate())
                .items(menu.getItems().stream()
                        .map(item -> MenuItemResponse.builder()
                                .id(item.getId())
                                .name(item.getName())
                                .description(item.getDescription())
                                .available(item.isAvailable())
                                .createdAt(item.getCreatedAt())
                                .build())
                        .toList())
                .createdAt(menu.getCreatedAt())
                .build();
    }
}
