package de.ait.g_67_shop.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "DTO for saving a new product to the database")
public class ProductSaveDto {

    @Schema(description = "Title of the new product", example = "Apple")
    private String title;

    @Schema(description = "Price of the new product", example = "1.50")
    private BigDecimal price;

    public ProductSaveDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        // Formatted output of the object for logs and console.
        return String.format("Product save DTO: title - %s, price - %.2f", title, price);
    }
}