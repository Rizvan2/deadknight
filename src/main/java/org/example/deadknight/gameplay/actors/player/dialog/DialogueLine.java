package org.example.deadknight.gameplay.actors.player.dialog;

/**
 * Представляет одну строку диалога для персонажа.
 * <p>
 * Хранит текст сообщения и длительность его отображения в секундах.
 * Используется менеджером диалогов для показа сообщений над сущностями.
 * </p>
 */
public class DialogueLine {

    /** Текст сообщения диалога. */
    private final String message;

    /** Время отображения сообщения в секундах. */
    private final double durationSeconds;

    /**
     * Создаёт новый объект диалога.
     *
     * @param message текст сообщения
     * @param durationSeconds длительность отображения сообщения в секундах
     */
    public DialogueLine(String message, double durationSeconds) {
        this.message = message;
        this.durationSeconds = durationSeconds;
    }

    /**
     * Возвращает текст сообщения диалога.
     *
     * @return текст сообщения
     */
    public String getMessage() {
        return message;
    }

    /**
     * Возвращает длительность отображения сообщения в секундах.
     *
     * @return длительность отображения
     */
    public double getDurationSeconds() {
        return durationSeconds;
    }
}
