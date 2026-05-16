package de.ait.g_67_shop.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for saving a new customer to the database")
public class CustomerSaveDto {

    @Schema(description = "Name of the new customer", example = "John Doe")
    private String name;

    public CustomerSaveDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        // Formatted output of the object for logs and console.
        return String.format("Customer Save DTO: name - %s", name);
    }
}