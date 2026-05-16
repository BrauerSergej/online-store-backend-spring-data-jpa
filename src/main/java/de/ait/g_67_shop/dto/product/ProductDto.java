package de.ait.g_67_shop.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Product DTO for sending to Client")
public class ProductDto {

    @Schema(description = "Product unique identifier", example = "777")
    private Long id;
    @Schema(description = "Product title", example = "Banana")
    private String title;
    @Schema(description = "Product price", example = "1.2")
    private BigDecimal price;

    public ProductDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return String.format("Product DTO: title - %s, price - %.2f", id, title, price);
    }
}
