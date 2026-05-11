package de.ait.g_67_shop.repository;

import de.ait.g_67_shop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


// Интерфейс репозитория для работы с таблицей products в базе данных
// ProductRepository наследуется от JpaRepository,
// поэтому получает готовые стандартные методы:
// save(), findAll(), findById(), deleteById(), count() и другие
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Метод возвращает список всех продуктов, у которых поле active = true
    // Spring Data JPA сам понимает название метода:
    // findAllByActiveTrue -> найти все записи, где active == true
    // SELECT * FROM product WHERE - примерно такой будет запрос на базу данных
    List<Product> findAllByActiveTrue();

    // Метод ищет один продукт по id, но только если active = true
    // Если продукт найден и он активный, вернётся Optional с продуктом
    // Если продукт не найден или active = false, вернётся пустой Optional
    Optional<Product> findByIdAndActiveTrue(Long id);
    int countByActiveTrue();
}
