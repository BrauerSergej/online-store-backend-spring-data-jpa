package de.ait.g_67_shop.dto.position;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for adding or removing a product position in the cart")
public class PositionUpdateDto {

    @Schema(description = "Product unique identifier", example = "5")
    private Long productId;

    @Schema(description = "Quantity of the product to add or remove", example = "2")
    private Integer quantity;

    public PositionUpdateDto() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("Position update DTO: productId - %d, quantity - %d", productId, quantity);
    }
}
