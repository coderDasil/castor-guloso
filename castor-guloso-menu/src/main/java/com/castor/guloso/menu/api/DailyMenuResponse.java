package com.castor.guloso.menu.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyMenuResponse {

    private Long id;
    private LocalDate menuDate;
    private List<MenuItemResponse> items;
    private LocalDateTime createdAt;
}
