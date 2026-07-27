package com.castor.guloso.menu.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyMenuRequest {

    private LocalDate menuDate;

    private java.util.Set<Long> menuItemIds;
}
