package com.castor.guloso.menu.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuPriceRequest {

    @NotNull
    @Positive
    private BigDecimal price;

    /**
     * When this price becomes effective. Defaults to today if null.
     */
    private LocalDate effectiveFrom;
}
