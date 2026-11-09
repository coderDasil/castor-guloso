package com.castor.guloso.order.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KitchenSummaryItem {

    private Long menuItemId;
    private String menuItemName;
    private long quantity;
    private boolean freeText;
}
