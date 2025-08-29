package org.example.deadknight.services;

import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.ui.GameOverUI;
import org.example.deadknight.ui.UIController;
import org.example.deadknight.components.HealthComponent;

/**
 * Сервис для управления пользовательским интерфейсом (UI) и проверки конца игры.
 * <p>
 * Основные функции:
 * <ul>
 *     <li>Инициализация UI для игрока</li>
 *     <li>Обновление элементов UI (например, HealthBar)</li>
 *     <li>Проверка состояния здоровья игрока и отображение экрана Game Over</li>
 * </ul>
 */
public class UIService {

    private UIController uiController;
    private boolean isGameOver = false;

    /**
     * Инициализирует UI для переданного игрока.
     *
     * @param player сущность игрока, для которого создается интерфейс
     */
    public void initUI(Entity player) {
        uiController = new UIController(player);
    }

    /**
     * Обновляет элементы интерфейса игрока.
     * <p>
     * Обычно вызывается каждый кадр в методе {@code onUpdate}.
     */
    public void update() {
        if (uiController != null) uiController.update();
    }

    /**
     * Проверяет, жив ли игрок, и при необходимости показывает экран Game Over.
     * <p>
     * Если здоровье игрока равно нулю, экран Game Over отображается и вызывается
     * {@code onRestart} для перезапуска игры. После этого флаг {@code isGameOver} сбрасывается.
     *
     * @param player    сущность игрока, чье здоровье проверяется
     * @param onRestart действие, выполняемое при перезапуске игры после Game Over
     */
    public void checkGameOver(Entity player, Runnable onRestart) {
        if (isGameOver) return;

        HealthComponent health = player.getComponent(HealthComponent.class);
        if (health.isDead()) {
            isGameOver = true;
            GameOverUI.show(() -> {
                onRestart.run();
                isGameOver = false;
            });
        }
    }
}
