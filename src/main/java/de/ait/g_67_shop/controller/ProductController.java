package de.ait.g_67_shop.controller;

import de.ait.g_67_shop.dto.product.ProductDto;
import de.ait.g_67_shop.dto.product.ProductSaveDto;
import de.ait.g_67_shop.dto.product.ProductUpdateDto;
import de.ait.g_67_shop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/*
@RequestMapping("/products") - благодаря этой аннотации Спринг понимает,
что все запросы, которые пришли на http://.../products
нужно адресовать именно этому контроллеру
 */
@RestController
@RequestMapping("/products")
@Tag(name = "Product Controller", description = "Контроллер для работы с продуктами")
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
    // POST -> http://.../products -> ожидаем название и цену в теле запроса
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Сохранить продукт", description = "Сохраняет новый продукт в базу данных")
    public ProductDto save(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Тело запроса с параметрами нового продукта")
            ProductSaveDto saveDto) {
        // Здесь будет обращение к сервису
        return service.save(saveDto);
    }

    //    • Вернуть все продукты из базы данных.
    // GET -> http://.../products
    @GetMapping
    @Operation(summary = "Получить все продукты", description = "Возвращает список всех активных продуктов")
    public List<ProductDto> getAll() {
        return service.getAllActiveProducts();
    }

    //    • Вернуть один продукт из базы данных по его идентификатору.
    // GET -> http://.../products/5
    @GetMapping("/{id}")
    @Operation(summary = "Получить продукт по ID", description = "Возвращает один продукт по его уникальному идентификатору")
    public ProductDto getById(
            @PathVariable
            @Parameter(description = "ID продукта для поиска")
            Long id
    ) {
        return service.getActiveProductById(id);
    }

    //    • Изменить один продукт в базе данных по его идентификатору.
    // PUT -> http://.../products/7 -> ожидаем в теле новую цену
    @PutMapping("/{id}")
    @Operation(summary = "Обновить цену продукта", description = "Изменяет цену существующего продукта по его ID")
    public void update(
            @PathVariable
            @Parameter(description = "ID продукта для обновления")
            Long id,
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Тело запроса с новой ценой")
            ProductUpdateDto updateDto) {
        service.update(id, updateDto);
    }

    //    • Удалить продукт из базы данных по его идентификатору.
    // DELETE -> http://.../products/7
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить продукт", description = "Мягкое удаление продукта (меняет статус active на false)")
    public void deleteById(
            @PathVariable
            @Parameter(description = "ID продукта для удаления")
            Long id) {
        service.deleteById(id);
    }

    //    • Восстановить удалённый продукт в базе данных по его идентификатору.
    // PUT -> http://.../products/7/restore
    @PutMapping("/{id}/restore")
    @Operation(summary = "Восстановить продукт", description = "Восстанавливает удаленный продукт (меняет статус active на true)")
    public void restoreById(
            @PathVariable
            @Parameter(description = "ID продукта для восстановления")
            Long id) {
        service.restoreById(id);
    }

    //    • Вернуть общее количество продуктов в базе данных.
    @GetMapping("/count")
    @Operation(summary = "Количество продуктов", description = "Возвращает общее количество активных продуктов")
    public int getProductsQuantity() {
        return service.getAllActiveProductsCount();
    }

    //    • Вернуть суммарную стоимость всех продуктов в базе данных.
    @GetMapping("/total-cost")
    @Operation(summary = "Суммарная стоимость", description = "Возвращает общую стоимость всех активных продуктов")
    public BigDecimal getProductsTotalCost() {
        return service.getAllActiveProductsTotalCost();
    }

    //    • Вернуть среднюю стоимость продукта в базе данных.
    @GetMapping("/avg-price")
    @Operation(summary = "Средняя цена", description = "Возвращает среднюю стоимость активных продуктов")
    public BigDecimal getProductsAveragePrice() {
        return service.getAllActiveProductsAveragePrice();
    }
}
