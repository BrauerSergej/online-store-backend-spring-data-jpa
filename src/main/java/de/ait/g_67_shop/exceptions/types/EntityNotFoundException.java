package de.ait.g_67_shop.exceptions.types;

// Не проверяемый exception - RuntimeException
// В Spring нужно применять не проверяемые exception - рекомендую применять непроверяемые эксепшены.
// Почему? Потому что когда мы работаем в Spring, мы логику обработки эксепшенов хотим вынести в
// отдельный класс. То есть чтобы у нас логика была в одном месте, а обработка ошибок была в
// другом месте. Но если вы используете проверяемый эксепшен, то идея и компилятор вас будут
// постоянно заставлять обработать эксепшены здесь и сейчас, то есть, по сути, написать
// блок try-catch. А мы от этого как раз и хотим уйти. Мы хотим уйти от блока try-catch
// в пользу глобальной обработки ошибок в отдельном классе.
public class EntityNotFoundException extends RuntimeException {
    // Конструктор                          тип сущности | идентификатор
    public EntityNotFoundException(Class<?> entityType, Long id) {
        // getSimpleName() - возвращает простое название типа - будет просто подставлено слово Product
        // вместо всего пути
        super(String.format("%s with ID %d not found", entityType.getSimpleName(), id));
    }
}
