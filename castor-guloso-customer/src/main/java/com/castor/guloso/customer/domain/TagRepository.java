package com.castor.guloso.customer.domain;

import com.castor.guloso.core.domain.Tag;
import com.castor.guloso.core.enums.TagType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByNameIgnoreCase(String name);

    List<Tag> findByType(TagType type);

    List<Tag> findByActiveTrue();
}
