package de.ait.g_67_shop.service;

import de.ait.g_67_shop.domain.Product;
import de.ait.g_67_shop.dto.mapping.ProductMapper;
import de.ait.g_67_shop.dto.product.ProductDto;
import de.ait.g_67_shop.dto.product.ProductSaveDto;
import de.ait.g_67_shop.dto.product.ProductUpdateDto;
import de.ait.g_67_shop.exceptions.types.EntityNotFoundException;
import de.ait.g_67_shop.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

// @Service говорит Spring:

// "Этот класс относится к сервисному слою.

// Создай объект этого класса сам
// и сохрани его в Spring Context как Spring Bean".
@Service
// Класс имплементирует интерфейс, то есть берёт на себя обязанность
// реализовать все методы, которые объявлены в этом интерфейсе.
// Это связь с интерфейсом
public class ProductServiceImpl implements ProductService {

    // Содержит объект logger
    private final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    // Поле которое содержит объект репозитория - мы можем к нему обращаться
    // Это связь с репозиторием
    private final ProductRepository repository;

    private final ProductMapper mapper;

    // ProductServiceImpl обращается к ProductRepository
    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    // Этот метод будет вызваться в ProductController
    public ProductDto save(ProductSaveDto saveDto) {
        Objects.requireNonNull(saveDto, "ProductSaveDto cannot be null");
        Product entity = mapper.mapDtoToEntity(saveDto);
        entity.setActive(true);
        repository.save(entity); // Здесь может возникать ошибка если валидация не проходит
        /*
        Не всегда стоит целиком логировать весь объект, так как
        он может быть очень большим.
        Или этот объект может содержать секреты.
        Иногда стоит логировать только определенные параметры объекта
        либо вообще только идентификатор.
        */
        logger.info("Product saved to the database: {}", entity);
        return mapper.mapEntityToDto(entity);
    }


    @Override
    public List<ProductDto> getAllActiveProducts() {
        List<Product> entities = repository.findAllByActiveTrue();
        return mapper.mapEntitiesToDto(entities);
        // Решение номер 1
//        return repository.findAllByActiveTrue()
//                .stream()
//                .map(mapper::mapEntityToDto)
//                .toList();
    }

    @Override
    public ProductDto getActiveProductById(Long id) {
        Product product = getActiveEntityById(id);
        return mapper.mapEntityToDto(product);
    }

    @Override
    public Product getActiveEntityById(Long id) {
        Objects.requireNonNull(id, "Product id cannot be null");
        return repository.findByIdAndActiveTrue(id).orElseThrow(
                // Передача самого типа и id не найденного продукта
                () -> new EntityNotFoundException(Product.class, id) // Customer может не существовать в базе
        );
    }

    @Override
    @Transactional
    public void update(Long id, ProductUpdateDto updateDto) {
        Objects.requireNonNull(id, "Product id cannot be null");
        Objects.requireNonNull(updateDto, "ProductUpdateDto cannot be null");

        BigDecimal newPrice = updateDto.getNewPrice();
        repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Product.class, id))
                .setPrice(newPrice);

        logger.info("Product id {} updated, new price: {}", id, newPrice);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        getActiveEntityById(id).setActive(false);
        logger.info("Product id {} marked as inactive", id);
    }

    @Override
    @Transactional
    public void restoreById(Long id) {
        Objects.requireNonNull(id, "Product id cannot be null");
        repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Product.class, id))
                .setActive(true);

        logger.info("Product id {} marked as active", id);
    }

    @Override
    public int getAllActiveProductsCount() {
        return repository.countByActiveTrue();
    }

    //    • Вернуть суммарную стоимость всех продуктов в базе данных (активных).
    @Override
    public BigDecimal getAllActiveProductsTotalCost() {
        return repository.findAllByActiveTrue()
                .stream()
                .map(Product::getPrice)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getAllActiveProductsAveragePrice() {
        long productsCount = getAllActiveProductsCount();

        if (productsCount == 0) {
            return BigDecimal.ZERO;
        }

        return getAllActiveProductsTotalCost().divide(
                BigDecimal.valueOf(productsCount),
                2,
                RoundingMode.HALF_UP
        );
    }
}

//        Метод принимает параметр?
//        → проверь через Objects.requireNonNull, если null недопустим.
//
//        Метод ищет объект в базе?
//        → используй orElseThrow с EntityNotFoundException.
//
//        Метод вызывает другой метод, который уже ищет и проверяет?
//        → повторно не надо.

//
//        EntityUpdateException нужен для таких случаев:
//
//        Customer найден, но обновить нельзя.
//        Product найден, но добавить нельзя.
//        Quantity неправильный.
//        Товара нет в корзине, а клиент хочет его удалить.
//        Клиент хочет сделать запрещённое изменение.