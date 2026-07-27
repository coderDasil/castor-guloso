package com.castor.guloso.menu.api;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemRequest {

    @NotBlank
    private String name;

    private String description;

    @Builder.Default
    private boolean available = true;
}
