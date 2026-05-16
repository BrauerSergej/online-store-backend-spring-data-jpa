package de.ait.g_67_shop.service;

import de.ait.g_67_shop.domain.Customer;
import de.ait.g_67_shop.dto.customer.CustomerDto;
import de.ait.g_67_shop.dto.customer.CustomerSaveDto;
import de.ait.g_67_shop.dto.customer.CustomerUpdateDto;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerService {

    // * Сохранить покупателя в базе данных (при сохранении покупатель автоматически считается активным).
    CustomerDto save(CustomerSaveDto saveDto);

    // * Вернуть всех покупателей из базы данных (активных).
    List<CustomerDto> getAllActiveCustomers();

    // * Вернуть одного покупателя из базы данных по его идентификатору (если он активен).
    CustomerDto getActiveCustomerById(Long id);
    Customer getActiveEntityById(Long id);

    // * Изменить одного покупателя в базе данных по его идентификатору.
    void update(Long id, CustomerUpdateDto updateDto);

    // * Удалить покупателя из базы данных по его идентификатору.
    void deleteById(Long id);

    // * Восстановить удалённого покупателя в базе данных по его идентификатору.
    void restoreById(Long id);

    // * Вернуть общее количество покупателей в базе данных (активных).
    int getAllActiveCustomerCount();

    // * Вернуть стоимость корзины покупателя по его идентификатору (если он активен).
    BigDecimal getActiveCustomerCartTotalCostById(Long id);

    // * Вернуть среднюю стоимость продукта в корзине покупателя по его идентификатору (если активен).
    BigDecimal getAverageProductPriceInCartByCustomerId(Long id);

    // * Добавить товар в корзину покупателя по их идентификаторам (если оба активны).
    //   Количество можно передавать контроллеру в теле запроса либо параметром.
    void addProductToCart(Long customerId, Long productId, int quantity);

    // * Удалить товар из корзины покупателя по их идентификаторам.
    //   Количество можно передавать контроллеру в теле запроса либо параметром.
    void removeProductFromCartById(Long customerId, Long productId, int quantity);

    // * Полностью очистить корзину покупателя по его идентификатору (если он активен).
    void clearCustomerCartById(Long id);
}
