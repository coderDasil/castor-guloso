package com.castor.guloso.menu.domain;

import com.castor.guloso.core.domain.MenuPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuPriceRepository extends JpaRepository<MenuPrice, Long> {

    /**
     * Find the current active price for a menu item.
     */
    @Query("""
            SELECT mp FROM MenuPrice mp
            WHERE mp.menuItem.id = :itemId
              AND mp.effectiveFrom <= :today
              AND (mp.effectiveTo IS NULL OR mp.effectiveTo >= :today)
            ORDER BY mp.effectiveFrom DESC
            """)
    List<MenuPrice> findCurrentPrice(@Param("itemId") Long itemId, @Param("today") LocalDate today);

    /**
     * Full price history for a menu item, newest first.
     */
    @Query("""
            SELECT mp FROM MenuPrice mp
            WHERE mp.menuItem.id = :itemId
            ORDER BY mp.effectiveFrom DESC
            """)
    List<MenuPrice> findHistoryByItemId(@Param("itemId") Long itemId);

    /**
     * Find the last active price before a given date (for rollover).
     */
    @Query("""
            SELECT mp FROM MenuPrice mp
            WHERE mp.menuItem.id = :itemId
              AND (mp.effectiveTo IS NULL OR mp.effectiveTo = :beforeDate)
            ORDER BY mp.effectiveFrom DESC
            """)
    Optional<MenuPrice> findLastActiveBefore(@Param("itemId") Long itemId,
                                              @Param("beforeDate") LocalDate beforeDate);
}
