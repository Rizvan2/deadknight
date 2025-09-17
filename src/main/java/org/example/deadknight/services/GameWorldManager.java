package org.example.deadknight.services;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.gameplay.actors.player.controllers.KnightController;
import org.example.deadknight.gameplay.actors.player.controllers.MovementController;
import org.example.deadknight.gameplay.actors.player.controllers.PantherController;
import org.example.deadknight.gameplay.actors.player.services.HasSpeed;
import org.example.deadknight.gameplay.actors.player.services.ui.PlayerUIService;
import org.example.deadknight.gameplay.actors.player.services.ui.UIService;
import org.example.deadknight.gameplay.actors.player.systems.CollisionSystem;
import org.example.deadknight.gameplay.components.SpeedComponent;
import org.example.deadknight.infrastructure.dto.GameWorldData;
import org.example.deadknight.infrastructure.render.services.MapChunkService;

import java.util.function.Supplier;

/**
 * Управляет игровым миром: отвечает за инициализацию карты, спавн игрока,
 * настройку камеры, обработку ввода, обновление логики и рестарт после Game Over.
 */
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

        // Инициализация игрового мира
        GameWorldData worldData = initializer.initGameWorld(characterType);
        player = worldData.player();
        mapChunkService = worldData.mapChunkService();

        // UI игрока
        PlayerUIService playerUIService = new PlayerUIService();
        playerUIService.initUI(player);

        // Контроллер движения
        HasSpeed playerData = (HasSpeed) player.getComponent(SpeedComponent.class);
        movementController = new MovementController(playerData, player);
        collisionSystem = new CollisionSystem();

        // Настройка ввода
        Supplier<Entity> entitySupplier = () -> player;
        switch (characterType) {
            case "knight" -> KnightController.initInput(entitySupplier);
            case "panther" -> PantherController.initInput(entitySupplier);
        }

        // Камера
        CameraService cameraService = new CameraService(0, 2.0, 1.05);
        cameraService.bindToEntity(
                player,
                FXGL.getAppWidth(),
                FXGL.getAppHeight(),
                worldData.mapWidth(),
                worldData.mapHeight()
        );
        cameraService.setupZoom();

        // UI и загрузка видимых чанков
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

    /**
     * @return текущий игрок
     */
    public Entity getPlayer() {
        return player;
    }
}
