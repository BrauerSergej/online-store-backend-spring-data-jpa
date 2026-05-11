package de.ait.g_67_shop.service;

import de.ait.g_67_shop.domain.Product;
import de.ait.g_67_shop.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

// @Service говорит Spring:
// "Этот класс относится к сервисному слою.
// Создай объект этого класса сам
// и сохрани его в Spring Context как Spring Bean".
@Service
// Класс имплементирует интерфейс, то есть берёт на себя обязанность
// реализовать все методы, которые объявлены в этом интерфейсе.
// Это связь с интерфейсом
public class ProductServiceImpl implements ProductService {

    // Поле которое содержит объект репозитория - мы можем к нему обращаться
    // Это связь с репозиторием
    private final ProductRepository repository;

    // ProductServiceImpl обращается к ProductRepository
    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    // Этот метод будет вызваться в ProductController
    public Product save(Product product) {
        // Делаем продукт активным
        product.setActive(true);
        // Методы технологии Spring Data JPA (save)
        // Метод save сам сохранит продукт в базу данных и присвоит тот ID
        // который присвоила база данных в результате мы возвращаем продукт
        // с присвоенным идентификатором
        return repository.save(product);
    }


    @Override
    public List<Product> getAllActiveProducts() {
        // Более эффективный способ мы можем сразу из базы запросить только активные продукты
        return repository.findAllByActiveTrue();
    }

    @Override
    public Product getActiveProductById(Long id) {
        return repository.findByIdAndActiveTrue(id).orElseThrow(
                // Временная обработка ошибки до тех пор, пока
                // не изучим соответствующую тему
                () -> new IllegalArgumentException("Product not found")
        );
    }

    @Override
    @Transactional
    public void update(Long id, Product product) {
        BigDecimal newPrice = product.getPrice();
        repository.findById(id).ifPresent(x -> x.setPrice(newPrice));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.findByIdAndActiveTrue(id).ifPresent(x -> x.setActive(false));
    }

    @Override
    @Transactional
    public void restoreById(Long id) {
        repository.findById(id).ifPresent(x -> x.setActive(true));
    }

    @Override
    public int getAllActiveProductsCount() {
        return repository.countByActiveTrue();
    }
    //    • Вернуть суммарную стоимость всех продуктов в базе данных (активных).
    @Override
    public BigDecimal getAllActiveProductsTotalCost() {
        return getAllActiveProducts()
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
