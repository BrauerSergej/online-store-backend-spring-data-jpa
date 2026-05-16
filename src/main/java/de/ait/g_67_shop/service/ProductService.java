package de.ait.g_67_shop.service;

import de.ait.g_67_shop.domain.Product;
import de.ait.g_67_shop.dto.product.ProductDto;
import de.ait.g_67_shop.dto.product.ProductSaveDto;
import de.ait.g_67_shop.dto.product.ProductUpdateDto;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    //    • Сохранить продукт в базе данных (при сохранении продукт автоматически считается активным).
    ProductDto save(ProductSaveDto saveDto);

    //    • Вернуть все продукты из базы данных (активные).
    List<ProductDto> getAllActiveProducts();

    //    • Вернуть один продукт из базы данных по его идентификатору (если он активен).
    ProductDto getActiveProductById(Long id);
    Product getActiveEntityById(Long id);

    //    • Изменить один продукт в базе данных по его идентификатору.
    void update(Long id, ProductUpdateDto updateDto);

    //    • Удалить продукт из базы данных по его идентификатору.
    void deleteById(Long id);

    //    • Восстановить удалённый продукт в базе данных по его идентификатору.
    void restoreById(Long id);

    //    • Вернуть общее количество продуктов в базе данных (активных).
    int getAllActiveProductsCount();

    //    • Вернуть суммарную стоимость всех продуктов в базе данных (активных).
    BigDecimal getAllActiveProductsTotalCost();

    //    • Вернуть среднюю стоимость продукта в базе данных (из активных).
    BigDecimal getAllActiveProductsAveragePrice();
}
