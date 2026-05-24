package de.ait.g_67_shop.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for updating an existing customer in the database")
public class CustomerUpdateDto {

    @Schema(description = "New name for the customer", example = "Jane Doe")
    private String name;

    public CustomerUpdateDto() {
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
        return String.format("Customer update DTO: name - %s", name);
    }
}