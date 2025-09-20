package org.example.deadknight.gameplay.actors.player.services;

import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.gameplay.actors.player.controllers.MovementController;
import org.example.deadknight.gameplay.actors.player.services.ui.PlayerUIService;

/**
 * Сервис для управления игроком в игре.
 * <p>
 * Отвечает за:
 * <ul>
 *   <li>Обновление логики движения игрока.</li>
 *   <li>Взаимодействие с пользовательским интерфейсом игрока (например, отображение апгрейдов/осколков).</li>
 * </ul>
 */
public class PlayerService {

    /**
     * Сущность игрока, зарегистрированная в игровом мире.
     */
    private final Entity player;

    /**
     * Контроллер движения игрока (перемещение, управление скоростью и направлением).
     */
    private final MovementController movementController;

    /**
     * Сервис пользовательского интерфейса игрока.
     * Может быть {@code null}, если UI для игрока не используется.
     */
    private final PlayerUIService uiService;

    /**
     * Создаёт сервис управления игроком.
     *
     * @param player             сущность игрока
     * @param movementController контроллер движения игрока
     * @param uiService          сервис UI игрока (может быть {@code null}, если интерфейс не используется)
     */
    public PlayerService(Entity player, MovementController movementController, PlayerUIService uiService) {
        this.player = player;
        this.movementController = movementController;
        this.uiService = uiService; // теперь просто ссылка, не вызываем initUI
    }

    /**
     * Обновляет состояние игрока.
     *
     * @param tpf время, прошедшее за кадр (time per frame), используется для расчётов движения
     */
    public void update(double tpf) {
        movementController.update(tpf); // движение
        if (uiService != null)
            uiService.update();          // апгрейды/осколки
    }

    /**
     * Возвращает сущность игрока.
     *
     * @return {@link Entity} игрока
     */
    public Entity getPlayer() {
        return player;
    }
}
