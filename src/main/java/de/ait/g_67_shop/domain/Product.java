package de.ait.g_67_shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity // Говорит Spring/JPA, что этот класс — сущность, которую нужно сохранять в БД
@Table(name = "product") // Указывает конкретное имя таблицы в базе (как в твоем XML)
public class Product {
    // Не нужно валидировать он назначается базой - клиент нам его не присылает
    @Id // Помечает поле как уникальный идентификатор (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Указывает, что ID база создает сама (autoIncrement="true")
    @Column(name = "id") // Явное имя колонки в БД
    private Long id;

    // Значение title не должно быть null - валидация
    // null - это полное отсутствие объекта строки
    // пустая строка это когда объект строки существует, но строка состоит из нуля символов -
    // также пустую строку можно считать которая только состоит из пробелов
    /*
    Требования к полю:
    1. Длина должна быть не менее 3 букв (максимум - 100).
    2. Первая буква должна быть в верхнем регистре
    3. Вторая и последующая буквы должны быть в нижнем регистре.
    4. Не допускаются цифры, специальные символы и кириллица (можно пробелы).
    */
    // message - будет отправляться клиенту в случае если поле оказалось null
    @NotNull(message = "Product title cannot be null")
    @NotBlank(message = "Product title cannot be empty")
    @Pattern(
            // Регулярное выражение
            // первая буква | последующие буквы | квантификатор - относится к предыдущему блоку от 3 - 100 символов
            regexp = "[A-Z][a-z ]{2,99}",
            message = "Product title should be at least three characters length and starts with capital letter"
    )
    @Column(name = "title")
    private String title; // Соответствует varchar(100)


    @NotNull(message = "Product price cannot be null")
    @DecimalMin(
            value = "0.00",
            // Если цена окажется меньше нуля - то валидатор выбросит exception и заложит это наше сообщение
            message = "Product price shouldn't be negative"
    )
    @DecimalMax(
            value = "1000.00",
            // Если false - то 1000 будут браться не включительно - по умолчанию значение = true
            inclusive = false,
            message = "Product price should be las than 1000"
    )

    @Column(name = "price")
    private BigDecimal price; // Соответствует decimal(7,2). BigDecimal используется для денег, так как он точный.

    // Не нужно валидировать - его нам сервис выставляет в значение true
    @Column(name = "active")
    private boolean active; // Соответствует типу boolean

    public Product() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Product product)) {
            return false;
        }

        return id != null && Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Product: id - %d, title - %s, price - %.2f, active - %s",
                id, title, price, active ? "yes" : "no");
    }
}
