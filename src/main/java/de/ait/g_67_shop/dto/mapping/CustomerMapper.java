package de.ait.g_67_shop.dto.mapping;

import de.ait.g_67_shop.domain.Customer;
import de.ait.g_67_shop.dto.customer.CustomerDto;
import de.ait.g_67_shop.dto.customer.CustomerSaveDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = CartMapper.class)
public interface CustomerMapper {

     CustomerDto mapEntityToDto(Customer entity);
     Customer mapDtoToEntity(CustomerSaveDto dto);
     List<CustomerDto> mapEntitiesToDto(List<Customer> entities);
}
