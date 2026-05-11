package de.ait.g_67_shop.controller;

import de.ait.g_67_shop.domain.Customer;
import de.ait.g_67_shop.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {


    // Поле которое содержит объект сервиса - мы можем к нему обращаться
    // Из него мы можем вытаскивать(использовать) методы
    private final CustomerService service;

    // Конструктор нужен, чтобы Spring передал сюда готовый сервис
    public CustomerController(CustomerService service) {
        // Сохраняем переданный сервис в поле класса
        this.service = service;
    }


    // * Сохранить покупателя в базе данных.
    //   POST -> http://10.20.30.40/customers -> ожидаем имя в теле запроса
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer save(@RequestBody Customer customer) {
        // Здесь будет обращение к сервису
        return service.save(customer);
    }

    // * Вернуть всех покупателей из базы данных.
    //   GET -> http://localhost:8081/customers
    @GetMapping
    public List<Customer> getAll() {
        // Здесь будет обращение к сервису
        return service.getAllActiveCustomers();
    }

    // * Вернуть одного покупателя из базы данных по его идентификатору.
    //   GET -> http://localhost:8081/customers/2
    @GetMapping("/{id}")
    public Customer getById(@PathVariable Long id) {
        return service.getActiveCustomerById(id);
    }

    // * Изменить одного покупателя в базе данных по его идентификатору.
    //   PUT -> http://localhost:8081/customers/2 - переменная пути
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long id, @RequestBody Customer customer) {
        service.update(id, customer);
    }

    // * Удалить покупателя из базы данных по его идентификатору
    //   DELETE -> http://localhost:8081/customers/2 - переменная пути
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

    // * Восстановить удалённого покупателя в базе данных по его идентификатору.
    //   PUT -> http://localhost:8081/customers/2/restore - переменная пути
    @PutMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreById(@PathVariable Long id) {
        service.restoreById(id);
    }

    // * Вернуть общее количество покупателей в базе данных.
    //   GET http://localhost:8081/customers/count
    @GetMapping("/count")
    public long getCustomerCount() {
        return service.getAllActiveCustomerCount();
    }

    // * Вернуть стоимость корзины покупателя по его идентификатору.
    //   GET -> http://localhost:8081/customers/2/total-cost
    @GetMapping("/{id}/total-cost")
    public BigDecimal getCartTotalCost(@PathVariable Long id) {
        return service.getActiveCustomerCartTotalCostById(id);
    }

    // * Вернуть среднюю стоимость продукта в корзине покупателя по его идентификатору.
    //   GET -> http://localhost:8081/customers/2/avg-cart-price
    @GetMapping("/{id}/avg-cart-price")
    public BigDecimal getProductsCartAveragePriceByCustomerId(@PathVariable Long id) {
        return service.getAverageProductPriceInCartByCustomerId(id);
    }

    // * Добавить товар в корзину покупателя по их идентификаторам.
    //   Количество добавляемого продукта можно передавать в теле запроса либо параметром.
    //   POST -> http://localhost:8081/customers/2/cart/products/5?quantity=3
    @PostMapping("/{customerId}/cart/products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addProductToCart(@PathVariable Long customerId,
                                 @PathVariable Long productId,
                                 @RequestParam Integer quantity) {
        service.addProductToCart(customerId, productId, quantity);
    }

    // * Удалить товар из корзины покупателя по их идентификаторам.
    //   Количество удаляемого продукта можно передавать в теле запроса либо параметром.
    //   DELETE -> http://localhost:8081/customers/2/cart/products/5?quantity=3
    @DeleteMapping("/{customerId}/cart/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductFromCart(@PathVariable Long customerId,
                                      @PathVariable Long productId,
                                      @RequestParam Integer quantity) {
        service.removeProductFromCartById(customerId, productId, quantity);
    }

    // * Полностью очистить корзину покупателя по его идентификатору.
    //   DELETE -> http://localhost:8081/customers/2/cart
    @DeleteMapping("/{customerId}/cart")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCartByCustomerId(@PathVariable Long customerId) {
        service.clearCustomerCartById(customerId);
    }
}
