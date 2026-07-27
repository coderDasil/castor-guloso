package com.castor.guloso.customer.domain;

import com.castor.guloso.core.domain.CustomerTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerTagRepository extends JpaRepository<CustomerTag, Long> {

    List<CustomerTag> findByCustomerId(Long customerId);

    List<CustomerTag> findByTagId(Long tagId);

    void deleteByCustomerIdAndTagId(Long customerId, Long tagId);
}
