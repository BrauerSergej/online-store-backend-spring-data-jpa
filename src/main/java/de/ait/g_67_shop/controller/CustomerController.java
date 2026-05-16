package de.ait.g_67_shop.controller;

import de.ait.g_67_shop.dto.customer.CustomerDto;
import de.ait.g_67_shop.dto.customer.CustomerSaveDto;
import de.ait.g_67_shop.dto.customer.CustomerUpdateDto;
import de.ait.g_67_shop.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customer Controller", description = "Контроллер для работы с покупателями и их корзинами")
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
    //   POST -> http://.../customers -> ожидаем имя в теле запроса
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Сохранить покупателя", description = "Создает нового покупателя и привязывает к нему пустую корзину")
    public CustomerDto save(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Тело запроса с данными нового покупателя")
            CustomerSaveDto saveDto) {
        // Здесь будет обращение к сервису
        return service.save(saveDto);
    }

    // * Вернуть всех покупателей из базы данных.
    //   GET -> http://localhost:8081/customers
    @GetMapping
    @Operation(summary = "Получить всех покупателей", description = "Возвращает список всех активных покупателей из базы данных")
    public List<CustomerDto> getAll() {
        // Здесь будет обращение к сервису
        return service.getAllActiveCustomers();
    }

    // * Вернуть одного покупателя из базы данных по его идентификатору.
    //   GET -> http://localhost:8081/customers/2
    @GetMapping("/{id}")
    @Operation(summary = "Получить покупателя по ID", description = "Возвращает данные одного покупателя по его уникальному идентификатору")
    public CustomerDto getById(
            @PathVariable
            @Parameter(description = "ID покупателя для поиска")
            Long id
    ) {
        return service.getActiveCustomerById(id);
    }

    // * Изменить одного покупателя в базе данных по его идентификатору.
    //   PUT -> http://localhost:8081/customers/2 - переменная пути
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Обновить имя покупателя", description = "Изменяет имя существующего покупателя по его ID")
    public void update(
            @PathVariable
            @Parameter(description = "ID покупателя для обновления")
            Long id,
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Тело запроса с новым именем покупателя")
            CustomerUpdateDto updateDto) {
        service.update(id, updateDto);
    }

    // * Удалить покупателя из базы данных по его идентификатору
    //   DELETE -> http://localhost:8081/customers/2 - переменная пути
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить покупателя", description = "Мягкое удаление покупателя (переводит статус active в false)")
    public void deleteById(
            @PathVariable
            @Parameter(description = "ID покупателя для удаления")
            Long id) {
        service.deleteById(id);
    }

    // * Восстановить удалённого покупателя в базе данных по его идентификатору.
    //   PUT -> http://localhost:8081/customers/2/restore - переменная пути
    @PutMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Восстановить покупателя", description = "Восстанавливает удаленного покупателя (переводит статус active в true)")
    public void restoreById(
            @PathVariable
            @Parameter(description = "ID покупателя для восстановления")
            Long id) {
        service.restoreById(id);
    }

    // * Вернуть общее количество покупателей в базе данных.
    //   GET http://localhost:8081/customers/count
    @GetMapping("/count")
    @Operation(summary = "Количество покупателей", description = "Возвращает общее число активных покупателей")
    public long getCustomerCount() {
        return service.getAllActiveCustomerCount();
    }

    // * Вернуть стоимость корзины покупателя по его идентификатору.
    //   GET -> http://localhost:8081/customers/2/total-cost
    @GetMapping("/{id}/total-cost")
    @Operation(summary = "Стоимость корзины", description = "Вычисляет и возвращает общую стоимость всех товаров в корзине покупателя")
    public BigDecimal getCartTotalCost(
            @PathVariable
            @Parameter(description = "ID покупателя")
            Long id) {
        return service.getActiveCustomerCartTotalCostById(id);
    }

    // * Вернуть среднюю стоимость продукта в корзине покупателя по его идентификатору.
    //   GET -> http://localhost:8081/customers/2/avg-cart-price
    @GetMapping("/{id}/avg-cart-price")
    @Operation(summary = "Средняя цена товара в корзине", description = "Возвращает среднюю стоимость одного товара в корзине покупателя")
    public BigDecimal getProductsCartAveragePriceByCustomerId(
            @PathVariable
            @Parameter(description = "ID покупателя")
            Long id) {
        return service.getAverageProductPriceInCartByCustomerId(id);
    }

    // * Добавить товар в корзину покупателя по их идентификаторам.
    //   Количество добавляемого продукта можно передавать в теле запроса либо параметром.
    //   POST -> http://localhost:8081/customers/2/cart/products/5?quantity=3
    @PostMapping("/{customerId}/cart/products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить товар в корзину", description = "Добавляет указанное количество выбранного товара в корзину покупателя")
    public void addProductToCart(
            @PathVariable
            @Parameter(description = "ID покупателя")
            Long customerId,
            @PathVariable
            @Parameter(description = "ID продукта для добавления")
            Long productId,
            @RequestParam
            @Parameter(description = "Количество товара", example = "1")
            Integer quantity) {
        service.addProductToCart(customerId, productId, quantity);
    }

    // * Удалить товар из корзины покупателя по их идентификаторам.
    //   Количество удаляемого продукта можно передавать в теле запроса либо параметром.
    //   DELETE -> http://localhost:8081/customers/2/cart/products/5?quantity=3
    @DeleteMapping("/{customerId}/cart/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить товар из корзины", description = "Уменьшает количество или полностью удаляет товар из корзины покупателя")
    public void deleteProductFromCart(
            @PathVariable
            @Parameter(description = "ID покупателя")
            Long customerId,
            @PathVariable
            @Parameter(description = "ID продукта для удаления")
            Long productId,
            @RequestParam
            @Parameter(description = "Количество товара для удаления", example = "1")
            Integer quantity) {
        service.removeProductFromCartById(customerId, productId, quantity);
    }

    // * Полностью очистить корзину покупателя по его идентификатору.
    //   DELETE -> http://localhost:8081/customers/2/cart
    @DeleteMapping("/{customerId}/cart")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Очистить корзину", description = "Полностью удаляет все товары из корзины покупателя")
    public void clearCartByCustomerId(
            @PathVariable
            @Parameter(description = "ID покупателя")
            Long customerId) {
        service.clearCustomerCartById(customerId);
    }
}