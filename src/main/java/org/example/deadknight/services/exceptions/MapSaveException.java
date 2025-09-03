package org.example.deadknight.services.exceptions;

/**
 * Исключение, выбрасываемое при ошибке сохранения карты в файл.
 * <p>
 * Используется, когда операция записи сгенерированного игрового поля
 * в указанный файл не может быть выполнена (например, из-за проблем с диском
 * или отсутствия прав на запись).
 */
public class MapSaveException extends RuntimeException {

    /**
     * Создаёт новое исключение с сообщением и причиной ошибки.
     *
     * @param message подробное сообщение об ошибке
     * @param cause   исходная причина ошибки (обычно {@link java.io.IOException})
     */
    public MapSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
