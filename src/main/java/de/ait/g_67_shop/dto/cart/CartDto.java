package de.ait.g_67_shop.dto.cart;

import de.ait.g_67_shop.dto.position.PositionDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "Cart DTO for sending to Client (contains a list of items)")
public class CartDto {

    @Schema(description = "Set of positions (items) in the customer's cart")
    private Set<PositionDto> positions;

    public CartDto() {
    }

    public Set<PositionDto> getPositions() {
        return positions;
    }

    public void setPositions(Set<PositionDto> positions) {
        this.positions = positions;
    }

    @Override
    public String toString() {
        // %s вызовет toString() у коллекции Set, которая в свою очередь
        // вызовет toString() у каждой PositionDto внутри неё.
        return String.format("Cart DTO: positions - %s", positions);
    }
}
