package org.example.deadknight.init;

import com.almasb.fxgl.app.GameSettings;

/**
 * Класс инициализации настроек игры.
 * <p>
 * Настраивает основные параметры окна игры, такие как размер и заголовок.
 */
public class SettingsInitializer {

    /**
     * Инициализирует параметры игры.
     *
     * @param settings объект {@link GameSettings}, который необходимо настроить
     */
    public static void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Dead KnightFactory");
    }
}
