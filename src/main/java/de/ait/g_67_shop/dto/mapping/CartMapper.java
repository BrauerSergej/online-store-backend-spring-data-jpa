package de.ait.g_67_shop.dto.mapping;

import de.ait.g_67_shop.domain.Cart;
import de.ait.g_67_shop.dto.cart.CartDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = PositionMapper.class)
public interface CartMapper {
    CartDto mapEntityToDto(Cart entity);
}
