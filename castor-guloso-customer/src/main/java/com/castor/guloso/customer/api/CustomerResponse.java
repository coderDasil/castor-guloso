package com.castor.guloso.customer.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private Long id;
    private String name;
    private String phone;
    private boolean active;
    private String notes;
    private LocalDateTime createdAt;
    private List<TagSummary> tags;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TagSummary {
        private Long id;
        private String name;
        private String type;
        private String color;
        private String notes;
    }
}
