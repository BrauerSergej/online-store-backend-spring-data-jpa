package de.ait.g_67_shop.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity // Говорит Spring/JPA, что этот класс — сущность, которую нужно сохранять в БД
@Table(name = "product") // Указывает конкретное имя таблицы в базе (как в твоем XML)
public class Product {

    @Id // Помечает поле как уникальный идентификатор (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Указывает, что ID база создает сама (autoIncrement="true")
    @Column(name = "id") // Явное имя колонки в БД
    private Long id;
    @Column(name = "title")
    private String title; // Соответствует varchar(100)

    @Column(name = "price")
    private BigDecimal price; // Соответствует decimal(7,2). BigDecimal используется для денег, так как он точный.

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
