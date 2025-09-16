package org.example.deadknight;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import org.example.deadknight.config.GameConfig;
import org.example.deadknight.gameplay.actors.essences.systems.EssenceCollisionInitializer;
import org.example.deadknight.gameplay.actors.essences.systems.UpgradeEssenceCollisionInitializer;
import org.example.deadknight.gameplay.actors.essences.factory.EssenceFactory;
import org.example.deadknight.gameplay.actors.mobs.factories.GoblinFactory;
import org.example.deadknight.gameplay.actors.player.controllers.KnightController;
import org.example.deadknight.gameplay.actors.player.controllers.MovementController;
import org.example.deadknight.gameplay.actors.player.controllers.PantherController;
import org.example.deadknight.gameplay.actors.player.services.HasSpeed;
import org.example.deadknight.gameplay.actors.player.services.ui.PlayerUIService;
import org.example.deadknight.gameplay.components.SpeedComponent;
import org.example.deadknight.infrastructure.dto.GameWorldData;
import org.example.deadknight.infrastructure.render.services.MapChunkService;
import org.example.deadknight.services.CameraService;
import org.example.deadknight.services.GameFlowService;
import org.example.deadknight.services.GameInitializerService;
import org.example.deadknight.services.UIService;
import org.example.deadknight.gameplay.actors.player.systems.CollisionSystem;
import org.example.deadknight.services.debug.DebugOverlayService;
import org.example.deadknight.services.init.SettingsInitializer;

import java.util.Set;
import java.util.function.Supplier;

public class DeadKnightApp extends GameApplication {

    private Entity player;
    private MovementController movementController;
    private CollisionSystem collisionSystem;
    private UIService uiService;
    private GameInitializerService gameInitService;
    private MapChunkService mapChunkService;
    private String currentCharacterType;

    @Override
    protected void initSettings(GameSettings settings) {
        SettingsInitializer.initSettings(settings);
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new EssenceFactory());

        gameInitService = new GameInitializerService();
        FXGL.getGameWorld().addEntityFactory(new GoblinFactory(gameInitService.getLootService())); // ✅ только здесь

        uiService = new UIService();
        collisionSystem = new CollisionSystem();

        GameFlowService gameFlowService = new GameFlowService();

        gameFlowService.startCharacterSelection(characterType -> {
            currentCharacterType = characterType;
            startGame(characterType);
        });

        DebugOverlayService debugService = new DebugOverlayService();
        setupDebugKeys(debugService);
        debugService.init();
    }


    private void setupDebugKeys(DebugOverlayService debugService) {
        FXGL.onKeyDown(KeyCode.F3, () -> {
            GameConfig.DEBUG_HITBOXES = !GameConfig.DEBUG_HITBOXES;
            if (!GameConfig.DEBUG_HITBOXES) debugService.clear();
        });
    }

    @Override
    protected void initPhysics() {
        new EssenceCollisionInitializer().init();
        new UpgradeEssenceCollisionInitializer().init();
    }

    private void startGame(String characterType) {
        clearScene();

        // Инициализация игрового мира через сервис
        GameWorldData worldData = gameInitService.initGameWorld(characterType);
        player = worldData.player();
        mapChunkService = worldData.mapChunkService();

        PlayerUIService playerUIService = new PlayerUIService();
        playerUIService.initUI(player);
        // Контроллер движения
        HasSpeed playerData = (HasSpeed) player.getComponent(SpeedComponent.class);
        movementController = new MovementController(playerData, player);

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

        // UI и враги
        uiService.initUI(player);
        Point2D playerChunk = mapChunkService.worldToChunk(player.getPosition());
        Set<Point2D> visibleChunks = mapChunkService.calculateVisibleChunks(playerChunk);
    }


    private void clearScene() {
        if (mapChunkService != null) mapChunkService.clearChunks();
        FXGL.getGameWorld().removeEntities(FXGL.getGameWorld().getEntitiesCopy());
        FXGL.getGameScene().clearUINodes();
        FXGL.getInput().clearAll();
    }

    @Override
    protected void onUpdate(double tpf) {
        if (player == null) return;

        movementController.update(tpf);
        collisionSystem.update(player, tpf);
        uiService.update();
        uiService.checkGameOver(player, () -> startGame(currentCharacterType));
        mapChunkService.updateVisibleChunks(player.getX(), player.getY());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
