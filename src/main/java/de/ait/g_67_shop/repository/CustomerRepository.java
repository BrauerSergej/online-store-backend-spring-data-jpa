package de.ait.g_67_shop.repository;

import de.ait.g_67_shop.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Метод возвращает список всех покупателей, у которых поле active = true
    // Spring Data JPA сам понимает название метода:
    // findAllByActiveTrue -> найти все записи, где active == true
    List<Customer> findAllByActiveTrue();

    // Метод ищет один покупателя по id, но только если active = true
    // Если покупатель найден и он активный, вернётся Optional с продуктом
    // Если покупатель не найден или active = false, вернётся пустой Optional
    Optional<Customer> findByIdAndActiveTrue(Long id);
    int countByActiveTrue();
}
