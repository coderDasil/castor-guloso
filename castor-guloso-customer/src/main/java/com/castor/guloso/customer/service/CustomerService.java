package com.castor.guloso.customer.service;

import com.castor.guloso.core.domain.Customer;
import com.castor.guloso.core.domain.CustomerTag;
import com.castor.guloso.core.domain.Tag;
import com.castor.guloso.customer.api.CustomerRequest;
import com.castor.guloso.customer.api.CustomerResponse;
import com.castor.guloso.customer.domain.CustomerRepository;
import com.castor.guloso.customer.domain.CustomerTagRepository;
import com.castor.guloso.customer.domain.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerTagRepository customerTagRepository;
    private final TagRepository tagRepository;

    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CustomerResponse> findActive() {
        return customerRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<CustomerResponse> findById(Long id) {
        return customerRepository.findById(id).map(this::toResponse);
    }

    public Optional<CustomerResponse> findByPhone(String phone) {
        return customerRepository.findByPhone(phone).map(this::toResponse);
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        String phone = normalizePhone(request.getPhone());
        if (customerRepository.findByPhone(phone).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A customer with this phone already exists");
        }

        Customer customer = Customer.builder()
                .name(request.getName().trim())
                .phone(phone)
                .active(request.isActive())
                .notes(blankToNull(request.getNotes()))
                .build();

        return toResponse(customerRepository.save(customer));
    }

    @Transactional
    public Optional<CustomerResponse> update(Long id, CustomerRequest request) {
        return customerRepository.findById(id).map(customer -> {
            String phone = normalizePhone(request.getPhone());
            customerRepository.findByPhone(phone).ifPresent(other -> {
                if (!other.getId().equals(id)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "A customer with this phone already exists");
                }
            });

            customer.setName(request.getName().trim());
            customer.setPhone(phone);
            customer.setActive(request.isActive());
            customer.setNotes(blankToNull(request.getNotes()));
            return toResponse(customerRepository.save(customer));
        });
    }

    @Transactional
    public void deactivate(Long id) {
        customerRepository.findById(id).ifPresent(c -> {
            c.setActive(false);
            customerRepository.save(c);
        });
    }

    @Transactional
    public void delete(Long id) {
        // Soft approach: deactivate. Hard delete only if no orders (future safety).
        deactivate(id);
    }

    @Transactional
    public CustomerResponse addTag(Long customerId, Long tagId, String notes) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));

        boolean already = customerTagRepository.findByCustomerId(customerId).stream()
                .anyMatch(ct -> ct.getTag().getId().equals(tagId));
        if (!already) {
            CustomerTag ct = CustomerTag.builder()
                    .customer(customer)
                    .tag(tag)
                    .notes(blankToNull(notes))
                    .build();
            customerTagRepository.save(ct);
        }
        return toResponse(customer);
    }

    @Transactional
    public CustomerResponse removeTag(Long customerId, Long tagId) {
        customerTagRepository.deleteByCustomerIdAndTagId(customerId, tagId);
        return customerRepository.findById(customerId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    public List<Tag> listActiveTags() {
        return tagRepository.findByActiveTrue();
    }

    private CustomerResponse toResponse(Customer customer) {
        List<CustomerResponse.TagSummary> tags = customerTagRepository.findByCustomerId(customer.getId()).stream()
                .map(ct -> CustomerResponse.TagSummary.builder()
                        .id(ct.getTag().getId())
                        .name(ct.getTag().getName())
                        .type(ct.getTag().getType().name())
                        .color(ct.getTag().getColor())
                        .notes(ct.getNotes())
                        .build())
                .toList();

        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .active(customer.isActive())
                .notes(customer.getNotes())
                .createdAt(customer.getCreatedAt())
                .tags(tags)
                .build();
    }

    private static String normalizePhone(String phone) {
        if (phone == null) return "";
        return phone.replaceAll("[^0-9+]", "").trim();
    }

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
