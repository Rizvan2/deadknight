package org.example.deadknight.controllers;

import com.almasb.fxgl.entity.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Контроллер, управляющий движением сущности игрока.
 * <p>
 * Делегирует обработку нажатий клавиш WASD через {@link WASDController}
 * и обновляет текущее время между кадрами (tpf).
 */
@Getter
@Setter
public class MovementController {

    /** Сущность, которой управляет контроллер */
    private final Entity entity;

    /**
     * Создаёт новый контроллер для указанной сущности.
     *
     * @param entity сущность для управления
     */
    public MovementController(Entity entity) {
        this.entity = entity;
    }

    /**
     * Обновляет состояние контроллера.
     * <p>
     * Вызывается каждый кадр и передаёт {@code tpf} в {@link WASDController}.
     *
     * @param tpf время между кадрами (time per frame)
     */
    public void update(double tpf) {
        WASDController.setCurrentTpf(tpf);
    }
}
