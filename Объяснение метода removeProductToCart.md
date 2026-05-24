Этот метод делает вот что:

> У покупателя из корзины удаляется определённое количество конкретного товара.
> Если после удаления количество стало больше 0 — просто уменьшаем `quantity`.
> Если стало 0 или меньше — полностью удаляем `Position` из корзины.

---

# 1. Метод принимает 3 параметра

```java
public void removeProductFromCartById(Long customerId, Long productId, PositionUpdateDto dto)
```

Сюда приходят:

```text
customerId → у какого покупателя удаляем товар
productId  → какой товар удаляем
dto        → сколько штук удалить
```

Например запрос:

```http
DELETE /customers/1/cart/items/2
```

Body:

```json
{
  "quantity": 1
}
```

означает:

```text
У покупателя id 1 удалить из корзины товар id 2 в количестве 1 штука.
```

---

# 2. Проверка, что параметры не null

```java
Objects.requireNonNull(customerId, "Customer id cannot be null");
Objects.requireNonNull(productId, "Product id cannot be null");
Objects.requireNonNull(dto, "PositionUpdateDto cannot be null");
```

Это защита от ситуации, когда в метод передали `null`.

Например:

```java
removeProductFromCartById(null, 2L, dto);
```

или:

```java
removeProductFromCartById(1L, 2L, null);
```

Если такое случится, метод сразу остановится.

---

# 3. Проверка количества

```java
if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
    throw new EntityUpdateException("Quantity must be positive");
}
```

Здесь проверяется бизнес-правило:

```text
Количество для удаления должно быть больше 0.
```

Нельзя удалить:

```json
{
  "quantity": 0
}
```

или:

```json
{
  "quantity": -3
}
```

или:

```json
{
  "quantity": null
}
```

Если quantity неправильный, выбрасывается:

```java
EntityUpdateException
```

И `GlobalExceptionHandler` вернёт клиенту ошибку, лучше всего `400 BAD_REQUEST`.

---

# 4. Достаём quantity из DTO

```java
int quantity = dto.getQuantity();
```

Теперь количество сохраняется в обычную переменную.

Например:

```text
quantity = 1
```

---

# 5. Ищем активного покупателя

```java
Customer customer = getActiveEntityById(customerId);
```

Этот метод ищет активного покупателя по `id`.

Если покупатель найден — вернётся объект `Customer`.

Если не найден — будет:

```java
EntityNotFoundException
```

Например:

```text
Customer with ID 1 not found
```

---

# 6. Берём корзину покупателя

```java
Cart cart = customer.getCart();
```

У найденного покупателя берём его корзину.

Логика такая:

```text
Customer → Cart
```

---

# 7. Проверяем, что корзина существует

```java
if (cart == null) {
    throw new EntityUpdateException("Customer cart does not exist");
}
```

Если у покупателя по какой-то причине нет корзины, метод не может удалить товар.

Поэтому выбрасывается ошибка:

```text
Customer cart does not exist
```

---

# 8. Берём все позиции из корзины

```java
Set<Position> positions = cart.getPositions();
```

`positions` — это все товары в корзине.

Например корзина:

```json
"positions": [
  {
    "id": 1,
    "quantity": 2,
    "product": {
      "id": 2,
      "title": "Bread"
    }
  }
]
```

В Java это:

```java
Set<Position> positions
```

---

# 9. Ищем нужный товар в корзине

```java
Position existingPosition = positions.stream()
        .filter(p -> p.getProduct().getId().equals(productId))
        .findFirst()
        .orElseThrow(() -> new EntityUpdateException("Product is not in customer's cart"));
```

Вот это самый важный кусок.

Он проходит по всем позициям в корзине и ищет такую позицию, где:

```java
p.getProduct().getId().equals(productId)
```

То есть:

```text
Найди Position, у которой product.id совпадает с productId из запроса.
```

Например в корзине есть:

```json
{
  "id": 1,
  "quantity": 2,
  "product": {
    "id": 2,
    "title": "Bread"
  }
}
```

И запрос:

```http
DELETE /customers/1/cart/items/2
```

Значит:

```text
productId = 2
```

Метод найдёт эту позицию.

Если такого товара в корзине нет, будет ошибка:

```java
EntityUpdateException("Product is not in customer's cart")
```

---

# 10. Считаем новое количество

```java
int newQuantity = existingPosition.getQuantity() - quantity;
```

Например в корзине было:

```text
Bread quantity = 2
```

Клиент хочет удалить:

```text
quantity = 1
```

Тогда:

```text
newQuantity = 2 - 1 = 1
```

---

# 11. Если после удаления количество ещё больше 0

```java
if (newQuantity > 0) {
    existingPosition.setQuantity(newQuantity);

    logger.info(
            "Business Event: Customer ID {} reduced quantity of Product ID {} in cart by {}. Remaining: {}",
            customerId, productId, quantity, newQuantity
    );
}
```

Если после вычитания товар ещё остаётся в корзине, мы просто обновляем количество.

Было:

```text
Bread quantity = 2
```

Удаляем:

```text
1
```

Станет:

```text
Bread quantity = 1
```

Позиция остаётся в корзине, меняется только `quantity`.

---

# 12. Если количество стало 0 или меньше

```java
else {
    positions.remove(existingPosition);

    logger.info(
            "Business Event: Customer ID {} completely removed Product ID {} from cart",
            customerId, productId
    );
}
```

Если после вычитания количество стало `0` или меньше, позиция полностью удаляется из корзины.

Например было:

```text
Bread quantity = 2
```

Удаляем:

```text
2
```

Станет:

```text
newQuantity = 0
```

Значит позицию удаляем полностью:

```java
positions.remove(existingPosition);
```

После этого в JSON корзины товара уже не будет.

---

# Почему изменение сохраняется в базе

Метод помечен:

```java
@Transactional
```

Это значит:

> Пока метод работает, Hibernate следит за объектами из базы.

Если ты делаешь:

```java
existingPosition.setQuantity(newQuantity);
```

Hibernate обновит `quantity` в таблице `position`.

Если ты делаешь:

```java
positions.remove(existingPosition);
```

и в `Cart` стоит:

```java
orphanRemoval = true
```

то Hibernate удалит эту строку из таблицы `position`.

---

# Пример полностью

До удаления:

```json
{
  "id": 1,
  "name": "Lars Chekoski",
  "cart": {
    "positions": [
      {
        "id": 1,
        "quantity": 2,
        "product": {
          "id": 2,
          "title": "Bread",
          "price": 1.49
        }
      }
    ]
  }
}
```

Запрос:

```http
DELETE /customers/1/cart/items/2
```

Body:

```json
{
  "quantity": 1
}
```

После удаления:

```json
{
  "id": 1,
  "name": "Lars Chekoski",
  "cart": {
    "positions": [
      {
        "id": 1,
        "quantity": 1,
        "product": {
          "id": 2,
          "title": "Bread",
          "price": 1.49
        }
      }
    ]
  }
}
```

Если ещё раз отправить:

```json
{
  "quantity": 1
}
```

то позиция полностью исчезнет:

```json
{
  "id": 1,
  "name": "Lars Chekoski",
  "cart": {
    "positions": []
  }
}
```

---

## Коротко

Метод делает 5 главных вещей:

```text
1. Проверяет customerId, productId и dto.
2. Проверяет quantity.
3. Находит активного Customer.
4. Находит нужную Position в корзине.
5. Уменьшает quantity или полностью удаляет Position.
```

Главная логика метода вот здесь:

```java
int newQuantity = existingPosition.getQuantity() - quantity;

if (newQuantity > 0) {
    existingPosition.setQuantity(newQuantity);
} else {
    positions.remove(existingPosition);
}
```

Это и есть суть удаления товара из корзины.
