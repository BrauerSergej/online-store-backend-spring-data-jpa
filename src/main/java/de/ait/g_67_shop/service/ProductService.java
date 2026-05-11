package de.ait.g_67_shop.service;

import de.ait.g_67_shop.domain.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    //    • Сохранить продукт в базе данных (при сохранении продукт автоматически считается активным).
    Product save(Product product);

    //    • Вернуть все продукты из базы данных (активные).
    List<Product> getAllActiveProducts();

    //    • Вернуть один продукт из базы данных по его идентификатору (если он активен).
    Product getActiveProductById(Long id);

    //    • Изменить один продукт в базе данных по его идентификатору.
    void update(Long id, Product product);

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
