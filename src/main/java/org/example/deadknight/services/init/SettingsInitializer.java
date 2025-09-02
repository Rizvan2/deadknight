package org.example.deadknight.services.init;

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
        // увеличиваем рабочее поле
        settings.setWidth(1920);
        settings.setHeight(1080);
        
        settings.setFullScreenAllowed(true);      // разрешаем fullscreen
        settings.setFullScreenFromStart(true);    // включаем fullscreen при старте
        settings.setTitle("Dead KnightFactory");
    }
}
