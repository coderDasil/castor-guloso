package com.castor.guloso.menu.domain;

import com.castor.guloso.core.domain.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    Optional<MenuItem> findByNameIgnoreCase(String name);

    List<MenuItem> findByAvailableTrue();
}
