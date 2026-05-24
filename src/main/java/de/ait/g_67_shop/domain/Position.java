package de.ait.g_67_shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Objects;

@Entity // Помечает класс как сущность, которая будет храниться в БД
@Table(name = "position") // Привязывает этот класс к конкретной таблице "position"
public class Position {

    @Id // Помечает поле как Primary Key (первичный ключ)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Указывает, что ID генерируется базой данных (автоинкремент)
    @Column(name = "id") // Имя колонки в таблице
    private Long id;

    @NotNull(message = "Product cannot be null")
    // @ManyToOne — связь "Многие к Одному": много позиций могут ссылаться на один товар.
    // fetch = FetchType.EAGER — "жадная" загрузка: товар подгружается из базы сразу вместе с позицией.
    @ManyToOne(fetch = FetchType.EAGER)
    // @JoinColumn — указывает на колонку во внешней таблице (Foreign Key), через которую идет связь.
    @JoinColumn(name = "product_id", nullable = false) // Поле product_id не может быть пустым
    private Product product;

    // @Positive проверяет, что значение больше 0
    @Positive(message = "The quantity must be greater than zero")
    @Column(name = "quantity", nullable = false) // Колонка для хранения количества товара
    private int quantity;

    // @NotNull(message = "Cart cannot be null")
    // @ManyToOne — связь "Многие к Одному": много позиций могут принадлежать одной корзине (Cart).
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false) // Связь с таблицей корзин через колонку cart_id
    private Cart cart;

    // Пустой конструктор: обязательное требование JPA/Hibernate для создания объекта из базы
    public Position() {
    }

    // Геттеры и сеттеры: стандартные методы для доступа к приватным полям
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true; // Если это один и тот же объект в памяти — они равны
        }

        if (!(o instanceof Position position)) {
            return false; // Если объект другого типа — они не равны
        }

        // Сравнение сущностей базы данных правильно делать только по уникальному ID
        return id != null && Objects.equals(id, position.id);
    }

    @Override
    public int hashCode() {
        // Возвращаем хэш-код класса, чтобы объект вел себя стабильно в коллекциях (Set, Map),
        // даже если ID еще не был присвоен базой данных.
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        // Форматированный вывод объекта для логов и консоли.
        // При вызове %s для поля product, будет автоматически вызван toString() класса Product.
        return String.format("Position: id - %d, product - %s, quantity - %d", id, product, quantity);
    }
}