/*
 * ProductMapper преобразует сущность базы данных (Product) в объект передачи данных (ProductDto) для отправки клиентам.
 * Скрывает внутреннюю структуру БД от внешнего API.
 */

package de.ait.g_67_shop.dto.mapping;

import de.ait.g_67_shop.domain.Product;
import de.ait.g_67_shop.dto.product.ProductDto;
import de.ait.g_67_shop.dto.product.ProductSaveDto;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

// 1. Указывает Spring автоматически создать этот объект (бин) для дальнейшего внедрения
// в другие классы без использования "new".
@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto mapEntityToDto(Product entity);
    Product mapDtoToEntity(ProductSaveDto dto);
    List<ProductDto> mapEntitiesToDto(List<Product> entities);
}

// 2. Проверка на null (Null-check): предотвращает ошибку NullPointerException,
// если в метод передана пустая сущность.
//    public ProductDto mapEntityToDto(Product entity){
//        if(entity == null) {
//            return null;
//        }
//
//        ProductDto dto = new ProductDto();

// 3. Выборочное копирование: данные из объекта БД переносятся в DTO.
// Все скрытые или ненужные поля остаются проигнорированными и не попадают в ответ сервера.
//        dto.setId(entity.getId());
//        dto.setId(entity.getId());
//        dto.setTitle(entity.getTitle());
//        dto.setPrice(entity.getPrice());

// 4. Возврат сформированного объекта.
//        return dto;
//    }
//}
