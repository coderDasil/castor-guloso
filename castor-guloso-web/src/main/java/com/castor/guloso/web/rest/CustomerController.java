package com.castor.guloso.web.rest;

import com.castor.guloso.core.domain.Tag;
import com.castor.guloso.customer.api.CustomerRequest;
import com.castor.guloso.customer.api.CustomerResponse;
import com.castor.guloso.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public List<CustomerResponse> listAll() {
        return customerService.findAll();
    }

    @GetMapping("/active")
    public List<CustomerResponse> listActive() {
        return customerService.findActive();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id) {
        return customerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse created = customerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody CustomerRequest request) {
        return customerService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/tags/{tagId}")
    public ResponseEntity<CustomerResponse> addTag(@PathVariable Long id,
                                                   @PathVariable Long tagId,
                                                   @RequestBody(required = false) Map<String, String> body) {
        String notes = body != null ? body.get("notes") : null;
        return ResponseEntity.ok(customerService.addTag(id, tagId, notes));
    }

    @DeleteMapping("/{id}/tags/{tagId}")
    public ResponseEntity<CustomerResponse> removeTag(@PathVariable Long id,
                                                      @PathVariable Long tagId) {
        return ResponseEntity.ok(customerService.removeTag(id, tagId));
    }

    @GetMapping("/tags/available")
    public List<Tag> listAvailableTags() {
        return customerService.listActiveTags();
    }
}
