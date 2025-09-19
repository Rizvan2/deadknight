package org.example.deadknight.gameplay.actors.player.services;

import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.gameplay.actors.player.controllers.MovementController;
import org.example.deadknight.gameplay.actors.player.services.ui.PlayerUIService;

/**
 * Сервис для управления игроком: движение и апгрейды (осколки).
 */
public class PlayerService {

    private final Entity player;
    private final MovementController movementController;
    private final PlayerUIService uiService;

    public PlayerService(Entity player, MovementController movementController, PlayerUIService uiService) {
        this.player = player;
        this.movementController = movementController;
        this.uiService = uiService; // теперь просто ссылка, не вызываем initUI
    }

    /** Обновление игрока каждый кадр */
    public void update(double tpf) {
        movementController.update(tpf); // движение
        if (uiService != null)
            uiService.update();          // апгрейды/осколки
    }

    public Entity getPlayer() {
        return player;
    }
}
