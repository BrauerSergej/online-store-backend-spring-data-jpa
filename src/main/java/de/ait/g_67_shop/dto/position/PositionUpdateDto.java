package de.ait.g_67_shop.dto.position;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for adding or removing a product position in the cart")
public class PositionUpdateDto {

    @Schema(description = "Quantity of the product to add or remove", example = "2")
    private Integer quantity;

    public PositionUpdateDto() {
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("Position update DTO: quantity - %d", quantity);
    }
}
