package com.castor.guloso.app.rest;

import com.castor.guloso.menu.api.MenuPriceRequest;
import com.castor.guloso.menu.api.MenuPriceResponse;
import com.castor.guloso.menu.api.PriceHistoryResponse;
import com.castor.guloso.menu.service.MenuPriceService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
public class MenuPriceController {

    private final MenuPriceService menuPriceService;

    @GetMapping("/{id}/price")
    public ResponseEntity<MenuPriceResponse> getCurrentPrice(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(menuPriceService.getCurrentPrice(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/price/history")
    public List<PriceHistoryResponse> getPriceHistory(@PathVariable Long id) {
        return menuPriceService.getPriceHistory(id);
    }

    @GetMapping("/prices")
    public Map<Long, BigDecimal> getAllCurrentPrices() {
        return menuPriceService.getCurrentPrices();
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<MenuPriceResponse> setPrice(@PathVariable Long id,
                                                       @Valid @RequestBody MenuPriceRequest request) {
        try {
            MenuPriceResponse response = menuPriceService.setPrice(id, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
