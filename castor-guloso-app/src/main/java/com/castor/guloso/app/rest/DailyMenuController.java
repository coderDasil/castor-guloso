package com.castor.guloso.app.rest;

import com.castor.guloso.menu.api.DailyMenuRequest;
import com.castor.guloso.menu.api.DailyMenuResponse;
import com.castor.guloso.menu.service.DailyMenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/daily-menus")
@RequiredArgsConstructor
public class DailyMenuController {

    private final DailyMenuService dailyMenuService;

    @GetMapping("/today")
    public ResponseEntity<DailyMenuResponse> getToday() {
        return dailyMenuService.findToday()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{date}")
    public ResponseEntity<DailyMenuResponse> getByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        return dailyMenuService.findByDate(localDate)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<DailyMenuResponse> listAll() {
        return dailyMenuService.findAll();
    }

    @PostMapping
    public ResponseEntity<DailyMenuResponse> setMenu(@Valid @RequestBody DailyMenuRequest request) {
        DailyMenuResponse created = dailyMenuService.setDailyMenu(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
