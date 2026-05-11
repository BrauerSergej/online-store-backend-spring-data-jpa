package de.ait.g_67_shop.domain;

import jakarta.persistence.*;

import java.util.Objects;

//Называется Entity, или сущность. Так вот, чтобы сказать фреймворку о том,
//что наш продукт является Entity, то есть о том, что он хранится в таблице
//соответствующей, мы здесь вешаем аннотацию Entity
@Entity
//Чтобы сказать фреймвоку в какой имеено таблице хранятся наши клиенты применяем анотацию table
@Table(name = "customer")
public class Customer {
    //    То есть благодаря этой аннотации фреймворк наш вообще понимает, что данное поле
//    является уникальным идентификатором и первичным ключом в базе данных. Но база
//    данных, она генерирует айдишники сама, и об этом должен знать фреймворк,
//    чтобы он не вмешивался в этот процесс и не происходило никаких конфликтов.
//    Для того, чтобы фреймворку сообщить о том, что у нас айдишники генерируются базой,
//    для этого есть аннотация, которая называется GeneratedValue, и в ней мы применяем
//    атрибут strategy для которого указываем значение GenerationType.IDENTITY. Именно вот
//    эта аннотация с таким значением атрибута и сообщает фреймворку о том, что айдишники
//    генерируются базой. Ну и на каждое из полей мы навешиваем аннотацию column, которая сообщает
//    фреймворку о том, а в какой именно колонке хранятся значения этого поля.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "active", nullable = false)
    private boolean active;

    /* @OneToOne — связь "Один к Одному". У одного клиента может быть только одна корзина.
     * cascade = CascadeType.ALL — означает, что если мы сохраним или удалим клиента,
     * то же самое действие автоматически произойдет и с его корзиной.
     * mappedBy = "customer" — указывает, что "главным" в этой связи является поле customer
     * в классе Cart. Это избавляет от создания лишней колонки в таблице customer. */
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "customer")
    private Cart cart;

    // Пустой конструктор необходим JPA для создания объекта при чтении из базы
    public Customer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
            return true;
        }

        if (!(o instanceof Customer customer)) {
            return false;
        }

        return id != null && Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Customer: id - %d, name - %s, active - %s, cart - %s",
                id,
                name,
                active ? "yes" : "no",
                cart == null ? "none" : cart.getId()
        );
    }
}