package com.castor.guloso.core.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A price point for a menu item with effective date range.
 * <p>
 * When a price changes, the old record gets {@code effectiveTo} set
 * and a new record is inserted. This gives full pricing history
 * without an separate audit table.
 * <p>
 * A record with {@code effectiveTo = null} is the current active price.
 */
@Entity
@Table(name = "menu_prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MenuPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull
    @Column(nullable = false)
    private LocalDate effectiveFrom;

    /**
     * If null, this price is currently active.
     * If set, this price was superseded on this date.
     */
    private LocalDate effectiveTo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
