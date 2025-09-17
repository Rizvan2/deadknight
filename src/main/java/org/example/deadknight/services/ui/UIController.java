package org.example.deadknight.services.ui;

import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.gameplay.actors.player.services.ui.HealthBar;

/**
 * Контроллер UI, управляющий элементами интерфейса игрока.
 * <p>
 * В текущей реализации отвечает за обновление полоски здоровья персонажа.
 */
public class UIController {

    private final HealthBar healthBar;

    /**
     * Создаёт контроллер UI для указанного персонажа.
     *
     * @param knight сущность персонажа, здоровье которого будет отображаться
     */
    public UIController(Entity knight) {
        this.healthBar = new HealthBar(knight);
    }

    /**
     * Обновляет UI элементы, связанные с персонажем.
     * <p>
     * В частности, обновляет полоску здоровья.
     */
    public void update() {
        healthBar.update();
    }
}
