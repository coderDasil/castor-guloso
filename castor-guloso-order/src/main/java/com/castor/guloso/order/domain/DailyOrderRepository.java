package com.castor.guloso.order.domain;

import com.castor.guloso.core.domain.DailyOrder;
import com.castor.guloso.core.domain.DailySession;
import com.castor.guloso.core.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DailyOrderRepository extends JpaRepository<DailyOrder, Long> {

    List<DailyOrder> findBySession(DailySession session);

    List<DailyOrder> findBySessionAndStatus(DailySession session, OrderStatus status);

    Optional<DailyOrder> findBySessionAndCustomerId(DailySession session, Long customerId);

    Optional<DailyOrder> findBySessionAndCustomerPhone(DailySession session, String phone);

    @Query("""
            SELECT o.menuItem.id, COUNT(o)
            FROM DailyOrder o
            WHERE o.session = :session AND o.status = 'CONFIRMED'
            GROUP BY o.menuItem.id
            """)
    List<Object[]> countConfirmedItemsBySession(@Param("session") DailySession session);

    @Query("""
            SELECT o.menuItem.id, COUNT(o) as cnt
            FROM DailyOrder o
            WHERE o.customer.id = :customerId AND o.status = 'CONFIRMED'
            GROUP BY o.menuItem.id
            ORDER BY cnt DESC
            """)
    List<Object[]> findCustomerFavoriteItemIds(@Param("customerId") Long customerId);
}
