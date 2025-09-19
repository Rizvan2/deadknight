package org.example.deadknight.services;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import lombok.Getter;
import lombok.Setter;
import org.example.deadknight.gameplay.actors.player.controllers.MovementController;
import org.example.deadknight.gameplay.actors.player.services.*;
import org.example.deadknight.gameplay.actors.player.services.ui.PlayerUIService;
import org.example.deadknight.gameplay.actors.player.services.ui.UIService;
import org.example.deadknight.gameplay.actors.player.systems.CollisionSystem;
import org.example.deadknight.infrastructure.dto.GameWorldData;
import org.example.deadknight.infrastructure.render.services.MapChunkService;

/**
 * Управляет игровым миром: отвечает за инициализацию карты, спавн игрока,
 * настройку камеры, обработку ввода, обновление логики и рестарт после Game Over.
 */
@Getter
@Setter
public class GameWorldManager {

    private final GameInitializerService initializer;
    private final UIService uiService;
    private String currentCharacterType;
    private Entity player;
    private MapChunkService mapChunkService;
    private MovementController movementController;
    private CollisionSystem collisionSystem;

    /**
     * @param initializer сервис для инициализации игрового мира (карта, игрок, враги)
     * @param uiService   сервис для отображения UI
     */
    public GameWorldManager(GameInitializerService initializer, UIService uiService) {
        this.initializer = initializer;
        this.uiService = uiService;
    }

    /**
     * Запускает игру для выбранного персонажа.
     *
     * @param characterType тип персонажа ("knight" или "panther")
     */
    public void startGame(String characterType) {
        this.currentCharacterType = characterType;

        clearScene();
        GameWorldData worldData = initializer.initGameWorld(characterType);
        PlayerWorld pw = WorldFactory.createPlayer(worldData);

        this.player = pw.player();
        this.mapChunkService = pw.mapChunkService();
        this.movementController = pw.movementController();
        collisionSystem = new CollisionSystem();

        CameraManager cameraManager = new CameraManager();

        PlayerInputService.initInput(characterType, () -> player);
        cameraManager.bindToPlayer(player, worldData);
        uiService.initUI(player);
    }

    /**
     * Очищает текущую игровую сцену (игрок, UI, ввод, сущности).
     */
    private void clearScene() {
        if (mapChunkService != null) mapChunkService.clearChunks();
        FXGL.getGameWorld().removeEntities(FXGL.getGameWorld().getEntitiesCopy());
        FXGL.getGameScene().clearUINodes();
        FXGL.getInput().clearAll();
    }

    /**
     * Обновляет игровую логику каждый кадр.
     *
     * @param tpf время, прошедшее с прошлого кадра (time per frame)
     */
    public void update(double tpf) {
        if (player == null) return;

        movementController.update(tpf);
        collisionSystem.update(player, tpf);
        uiService.update();
        uiService.checkGameOver(player, () -> startGame(currentCharacterType));
        mapChunkService.updateVisibleChunks(player.getX(), player.getY());
    }
}
