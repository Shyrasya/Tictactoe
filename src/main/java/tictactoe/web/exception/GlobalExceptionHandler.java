package tictactoe.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tictactoe.web.model.ErrorMessage;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Печать сообщения при исключении, выбрасиваемого при общих ошибках
     *
     * @param e Пойманное исключение
     * @return Ответ клиенту о 500 ошибке с сообщением причины возникновения
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage("Неизвестная ошибка: " + e.getMessage()));
    }

    /**
     * Обрабатывает исключение {@link GameNotFoundException}, выбрасываемое при отсутствии искомой игры.
     *
     * @param e Пойманное исключение
     * @return {@code ResponseEntity<Map<String, String>>} с сообщением об ошибке:
     * <ul>
     *     <li>HTTP 404 (Not Found) - Если игра не найдена, сообщение об ошибке под ключом {@code "error"}.</li>
     * </ul>
     */
    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleInvalidGameStateException(GameNotFoundException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
