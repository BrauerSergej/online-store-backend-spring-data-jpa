package de.ait.g_67_shop.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "DTO for updating an existing product in the database")
public class ProductUpdateDto {

    @Schema(description = "New price for the product", example = "2.99")
    private BigDecimal newPrice;

    public ProductUpdateDto() {
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    @Override
    public String toString() {
        // Formatted output of the object for logs and console.
        return String.format("Product update DTO: newPrice - %.2f", newPrice);
    }
}
