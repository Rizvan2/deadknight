package org.example.deadknight.gameplay.actors.player.services.ui;

import com.almasb.fxgl.entity.Entity;
import lombok.Getter;
import lombok.Setter;
import org.example.deadknight.gameplay.components.HealthComponent;

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
@Getter
@Setter
public class UIService {

    private PlayerUIService playerUIService; // отвечает только за "essence" UI
    private UIController uiController;
    private boolean isGameOver = false;

    public void initUI(Entity player) {
        uiController = new UIController(player);       // HealthBar, другие элементы
        playerUIService = new PlayerUIService();
        playerUIService.initUI(player);               // инициализация осколков
    }

    public void update() {
        if (uiController != null) uiController.update();
        if (playerUIService != null) playerUIService.update(); // нужно добавить метод update()
    }

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
