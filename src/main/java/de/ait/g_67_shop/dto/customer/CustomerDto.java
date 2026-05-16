package de.ait.g_67_shop.dto.customer;

import de.ait.g_67_shop.dto.cart.CartDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Customer Response DTO for sending to Client")
public class CustomerDto {

    @Schema(description = "Customer unique identifier", example = "555")
    private Long id;

    @Schema(description = "Customer name", example = "John Johnson")
    private String name;

    @Schema(description = "Current active shopping cart of the customer")
    private CartDto cart;

    public CustomerDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CartDto getCart() {
        return cart;
    }

    public void setCart(CartDto cart) {
        this.cart = cart;
    }

    @Override
    public String toString() {
        return String.format("Customer: id - %d, name - %s, cart - %s", id, name, cart);
    }
}