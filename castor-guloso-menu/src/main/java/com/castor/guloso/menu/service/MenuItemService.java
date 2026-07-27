package com.castor.guloso.menu.service;

import com.castor.guloso.core.domain.MenuItem;
import com.castor.guloso.menu.api.MenuItemRequest;
import com.castor.guloso.menu.api.MenuItemResponse;
import com.castor.guloso.menu.domain.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository repository;

    public List<MenuItemResponse> findAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<MenuItemResponse> findAvailable() {
        return repository.findByAvailableTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<MenuItemResponse> findById(Long id) {
        return repository.findById(id).map(this::toResponse);
    }

    @Transactional
    public MenuItemResponse create(MenuItemRequest request) {
        MenuItem item = MenuItem.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .available(request.isAvailable())
                .build();
        return toResponse(repository.save(item));
    }

    @Transactional
    public Optional<MenuItemResponse> update(Long id, MenuItemRequest request) {
        return repository.findById(id).map(item -> {
            item.setName(request.getName().trim());
            item.setDescription(request.getDescription());
            item.setAvailable(request.isAvailable());
            return toResponse(repository.save(item));
        });
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private MenuItemResponse toResponse(MenuItem item) {
        return MenuItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
