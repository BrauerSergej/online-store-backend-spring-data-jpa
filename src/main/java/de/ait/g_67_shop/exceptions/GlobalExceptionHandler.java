package de.ait.g_67_shop.exceptions;

import de.ait.g_67_shop.exceptions.types.EntityNotFoundException;
import de.ait.g_67_shop.exceptions.types.EntityUpdateException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice // Глобальный обработчик ошибок для REST-контроллеров
public class GlobalExceptionHandler {

    // Logger нужен для записи сообщений об ошибках в консоль или файл
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Метод сработает только при ошибке EntityNotFoundException
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleException(EntityNotFoundException e) {

        // Получаем текст ошибки, например: "Product not found"
        String message = e.getMessage();

        // Записываем предупреждение в лог
        logger.warn(message);

        // Возвращаем клиенту текст ошибки и HTTP-статус 404 NOT_FOUND
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    // =========================================================================
    // 1. Обработка ошибок валидации (когда клиент прислал неверные данные)
    // =========================================================================

    // @ExceptionHandler указывает, что этот метод сработает только тогда,
    // когда вылетит ошибка типа ConstraintViolationException.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<String>> handleException(ConstraintViolationException e) {

        // Собираем список всех ошибок валидации с помощью Stream API (конвейера)
        List<String> messages = e
                .getConstraintViolations() // 1. Достаем "мешок" всех нарушений правил
                .stream()                  // 2. Высыпаем их на конвейерную ленту
                .map(ConstraintViolation::getMessage) // 3. Отбрасываем лишнее, оставляем только текст ошибки (String)
                .peek(logger::warn)        // 4. "Шпион": проходя мимо, записываем ошибку в лог сервера (желтым цветом - WARN)
                .toList();                 // 5. Упаковываем все тексты в готовый список

        // Возвращаем клиенту собранный список его ошибок.
        // Статус BAD_REQUEST (400) означает "Сервер работает, но ты прислал кривые данные".
        return new ResponseEntity<>(messages, HttpStatus.BAD_REQUEST);
    }


    // =========================================================================
    // 2. Обработка критических ошибок в коде (например, наткнулись на null)
    // =========================================================================

    // Этот метод ловит самую частую ошибку программистов - NullPointerException.
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleException(NullPointerException e) {

        // Достаем технический текст ошибки из кода
        String message = e.getMessage();

        // ВАЖНО: Записываем в лог как ERROR (красным цветом) и обязательно передаем саму ошибку 'e'.
        // Это распечатает в консоли весь маршрут поломки (стектрейс), чтобы программист смог всё починить.
        logger.error(message, e);

        // Формируем безопасный ответ клиенту.
        // Мы СПЕЦИАЛЬНО не отдаем клиенту переменную 'message', чтобы не слить секретные данные сервера (безопасность!).
        return new ResponseEntity<>(
                // Отдаем стандартную безликую фразу "Internal Server Error"
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                // Отдаем статус 500: "Ты всё сделал правильно, но сервер сломался. Виноваты мы".
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(EntityUpdateException.class)
    public ResponseEntity<String> handleException(EntityUpdateException e) {
        String message = e.getMessage();
        logger.warn(message);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
