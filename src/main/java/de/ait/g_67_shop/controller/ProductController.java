package de.ait.g_67_shop.controller;

import de.ait.g_67_shop.domain.Product;
import de.ait.g_67_shop.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/*
@RequestMapping("/products") - благодаря этой аннотации Спринг понимает,
что все запросы, которые пришли на http://10.20.30.40:8080/products
нужно адресовать именно этому контроллеру
 */
@RestController
@RequestMapping("/products")

public class ProductController {

    // Поле которое содержит объект сервиса - мы можем к нему обращаться
    // Controller обращается к ProductService как к интерфейсу,
    // но реально метод выполняет ProductServiceImpl.
    private final ProductService service;

    // Конструктор нужен, чтобы Spring передал сюда готовый сервис
    public ProductController(ProductService service) {
        // Сохраняем переданный сервис в поле класса
        this.service = service;
    }

    // • Сохранить продукт в базе данных.
    // POST -> http://10.20.30.40:8080/products -> ожидаем название и цену в теле запроса
    // {
    //    "title": "Banana",
    //    "price": 1.4
    // }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product save(@RequestBody Product product) {
        // Здесь будет обращение к сервису
        return service.save(product);
    }

    //    • Вернуть все продукты из базы данных.
    // GET -> http://10.20.30.40:8080/products
    //
    @GetMapping
    public List<Product> getAll() {
        return service.getAllActiveProducts();
    }

    //    • Вернуть один продукт из базы данных по его идентификатору.
    // GET -> http://10.20.30.40:8080/products?id=5
    // GET -> http://10.20.30.40:8080/products/5
    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return service.getActiveProductById(id);
    }

    //    • Изменить один продукт в базе данных по его идентификатору.
    // PUT -> http://10.20.30.40:8080/products/7 -> ожидаем в теле новую цену
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody Product product) {
        service.update(id, product);
    }

    //    • Удалить продукт из базы данных по его идентификатору.
    // DELETE -> http://10.20.30.40:8080/products/7
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

    //    • Восстановить удалённый продукт в базе данных по его идентификатору.
    // PUT -> http://10.20.30.40:8080/products/7/restore
    @PutMapping("/{id}/restore")
    public void restoreById(@PathVariable Long id) {
        service.restoreById(id);
    }

    //    • Вернуть общее количество продуктов в базе данных.
    @GetMapping("/count")
    public int getProductsQuantity() {
        return service.getAllActiveProductsCount();
    }

    //    • Вернуть суммарную стоимость всех продуктов в базе данных.
    @GetMapping("/total-cost")
    public BigDecimal getProductsTotalCost() {
        return service.getAllActiveProductsTotalCost();
    }

    //    • Вернуть среднюю стоимость продукта в базе данных.
    @GetMapping("/avg-price")
    public BigDecimal getProductsAveragePrice() {
        return service.getAllActiveProductsAveragePrice();
    }
}

