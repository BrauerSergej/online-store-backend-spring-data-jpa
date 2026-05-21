package de.ait.g_67_shop.controller;

import de.ait.g_67_shop.domain.Product;
import de.ait.g_67_shop.dto.product.ProductDto;
import de.ait.g_67_shop.dto.product.ProductSaveDto;
import de.ait.g_67_shop.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTestIT {

    @Autowired
    // Умеет отправлять http запросы
    private TestRestTemplate httpClient;

    @Autowired
    private ProductRepository repository;

    private static final String PRODUCTS_RESOURCE = "/products";

    @Test
    public void shouldSaveProduct() {
        // Создаем тело запроса (в данном случае им является ДТО для сохранения)
        ProductSaveDto saveDto = new ProductSaveDto();
        saveDto.setTitle("Test product");
        saveDto.setPrice(new BigDecimal("777.00"));

        // Создаем объект http запроса
        HttpEntity<ProductSaveDto> request = new HttpEntity<>(saveDto);
        // Отправляем запрос и получаем ответ (response)
        // Вариант 1
//        ResponseEntity<ProductDto> response = httpClient.exchange(
//                // url | Post-запрос | Объект запроса | тип данных
//                "/products", HttpMethod.POST, request, ProductDto.class
//        );

        // Вариант 2
        ResponseEntity<ProductDto> response = httpClient.postForEntity(
                PRODUCTS_RESOURCE, request, ProductDto.class
        );

        // Проверяем что нам действительно пришел ожидаемый статус
        // response.getStatusCode() достает из ответа HTTP-статус, который вернул контроллер.
        // HttpStatus.CREATED — это статус 201 Created. В REST-архитектуре принято возвращать именно его,
        // если запрос успешно создал новую сущность в системе.
        // Третий параметр (строка) — это сообщение об ошибке. Оно сработает только если тест упадет.
        // Если контроллер вернет, например, 500 Internal Server Error, тест покажет красивую ошибку:
        // "Response has unexpected status: expected 201 but was 500".
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response has unexpected status");

        // Проверяем корректность того что пришло в теле response
        // Здесь мы проверяем, что сервер не просто сказал «успешно», но и вернул нам обратно правильный
        // объект (уже созданный продукт).
        ProductDto dto = response.getBody();
        // Вытаскиваем тело ответа (body). Первая проверка assertNotNull страхует от NullPointerException.
        // Если сервер прислал пустоту, тест упадет здесь с понятным сообщением, а не пойдет дальше.
        assertNotNull(dto, "Response does not have a body");
        assertNotNull(dto.getId(), "Returned product doesn't have an ID");
        assertEquals(saveDto.getTitle(), dto.getTitle(), "Returned product has incorrect a title");
        assertEquals(saveDto.getPrice(), dto.getPrice(), "Returned product has incorrect a price");

        // Проверяем, что продукт действительно корректно сохранился в базу данных
        Product savedProduct = repository.findByIdAndActiveTrue(dto.getId()).orElse(null);
        assertNotNull(savedProduct, "Product was´nt properly saved to the database");
        assertEquals(saveDto.getTitle(), savedProduct.getTitle(), "Saved product has incorrect title");
        assertEquals(saveDto.getPrice(), savedProduct.getPrice(), "Saved product has incorrect price");
    }

    @Test
    public void shouldReturn400WhenTitleIsEmpty() {
        ProductSaveDto saveDto = new ProductSaveDto();
        saveDto.setTitle("");
        saveDto.setPrice(new BigDecimal("777.00"));

        HttpEntity<ProductSaveDto> request = new HttpEntity<>(saveDto);

        ResponseEntity<String> response = httpClient.postForEntity(
                PRODUCTS_RESOURCE, request, String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response has unexpected status");

        String body = response.getBody();
        assertNotNull(body, "Response doesn't have an error message");
        assertTrue(body.contains("title"), "Response doesn't contain an expected error message");
    }
    // Метод для заполнения базы данных тестовыми объектами перед каждым тестом
    @BeforeEach
    public void startUp() {
        Product activeProduct = new Product();
        activeProduct.setTitle("Test active product");
        activeProduct.setPrice(new BigDecimal("111.00"));
        activeProduct.setActive(true);

        Product inactiveProduct = new Product();
        inactiveProduct.setTitle("Test inactive product");
        inactiveProduct.setPrice(new BigDecimal("222.00"));
        inactiveProduct.setActive(false);

        repository.saveAll(List.of(activeProduct, inactiveProduct));
    }

    // Метод для очистки базы данных после каждого теста
    @AfterEach
    public void cleanDatabase() {
        repository.deleteAll();
    }
}