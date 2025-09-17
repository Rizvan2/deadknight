package org.example.deadknight;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.input.KeyCode;
import org.example.deadknight.config.GameConfig;
import org.example.deadknight.gameplay.actors.essences.systems.EssenceCollisionInitializer;
import org.example.deadknight.gameplay.actors.essences.systems.UpgradeEssenceCollisionInitializer;
import org.example.deadknight.gameplay.actors.essences.factory.EssenceFactory;
import org.example.deadknight.gameplay.actors.mobs.factories.GoblinFactory;
import org.example.deadknight.gameplay.actors.player.services.ui.UIService;
import org.example.deadknight.services.GameFlowService;
import org.example.deadknight.services.GameInitializerService;
import org.example.deadknight.services.GameWorldManager;
import org.example.deadknight.services.debug.DebugOverlayService;
import org.example.deadknight.services.init.SettingsInitializer;

public class DeadKnightApp extends GameApplication {

    private GameWorldManager worldManager;
    private String currentCharacterType;

    @Override
    protected void initSettings(GameSettings settings) {
        SettingsInitializer.initSettings(settings);
    }

    @Override
    protected void initGame() {
        // Подключаем необходимые фабрики сущностей
        FXGL.getGameWorld().addEntityFactory(new EssenceFactory());

        GameInitializerService gameInitService = new GameInitializerService();
        FXGL.getGameWorld().addEntityFactory(new GoblinFactory(gameInitService.getLootService()));

        UIService uiService = new UIService();

        worldManager = new GameWorldManager(gameInitService, uiService);

        // Выбор персонажа
        GameFlowService gameFlowService = new GameFlowService();

        gameFlowService.startCharacterSelection(characterType -> {
            currentCharacterType = characterType;
            worldManager.startGame(characterType);
        });

        // Отладка
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

    @Override
    protected void onUpdate(double tpf) {
        worldManager.update(tpf);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
