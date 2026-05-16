package de.ait.g_67_shop.dto.position;

import de.ait.g_67_shop.dto.product.ProductDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Position DTO representing an item in the shopping cart")
public class PositionDto {

    @Schema(description = "Position unique identifier", example = "10")
    private Long id;

    @Schema(description = "Quantity of the selected product", example = "2")
    private int quantity;

    @Schema(description = "Product details included in this position")
    private ProductDto product;

    public PositionDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    @Override
    public String toString() {
        // Formatted output of the object for logs and console.
        // When using %s for the product field, the toString() method of the ProductDto class will be called automatically.
        return String.format("Position: id - %d, product - %s, quantity - %d", id, product, quantity);
    }
}