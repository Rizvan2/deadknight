package org.example.deadknight;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.player.components.SpeedComponent;
import org.example.deadknight.player.controllers.MovementController;
import org.example.deadknight.player.controllers.KnightController;
import org.example.deadknight.player.controllers.PantherController;
import org.example.deadknight.services.init.LoadingScreenSubScene;
import org.example.deadknight.services.init.SettingsInitializer;
import org.example.deadknight.services.GameInitializerService;
import org.example.deadknight.player.services.HasSpeed;
import org.example.deadknight.services.UIService;
import org.example.deadknight.player.systems.CollisionSystem;
import org.example.deadknight.services.ui.CharacterSelectScreen;

import java.util.List;
import java.util.function.Supplier;

/**
 * Главный класс приложения DeadKnight.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Инициализацию настроек игры</li>
 *     <li>Выбор персонажа и показ экрана загрузки</li>
 *     <li>Инициализацию игрока и врагов через {@link GameInitializerService}</li>
 *     <li>Инициализацию UI через {@link UIService}</li>
 *     <li>Обновление состояния игрока, UI и системы столкновений</li>
 *     <li>Обработку конца игры и перезапуск</li>
 * </ul>
 */
public class DeadKnightApp extends GameApplication {

    /** Сущность игрока, которой управляет пользователь */
    private Entity player;

    /** Контроллер движения игрока */
    private MovementController movementController;

    /** Система обработки столкновений и урона */
    private CollisionSystem collisionSystem;

    /** Сервис для управления интерфейсом игрока */
    private UIService uiService;

    /** Сервис инициализации игрока и врагов */
    private GameInitializerService gameInitService;

    /** Тип текущего выбранного персонажа ("knight"/"panther") */
    private String currentCharacterType;

    @Override
    protected void initSettings(GameSettings settings) {
        SettingsInitializer.initSettings(settings);
    }

    @Override
    protected void initGame() {
        gameInitService = new GameInitializerService();
        uiService = new UIService();
        collisionSystem = new CollisionSystem();

        CharacterSelectScreen.show(characterType -> {
            currentCharacterType = characterType;
            FXGL.getGameScene().clearUINodes();

            LoadingScreenSubScene loadingScreen = new LoadingScreenSubScene(
                    FXGL.getAppWidth(),
                    FXGL.getAppHeight()
            );
            FXGL.getGameScene().addUINode(loadingScreen);

            loadingScreen.loadTextures(() -> {
                FXGL.getGameScene().removeUINode(loadingScreen);
                startGame(characterType);
            });
        });
    }

    /**
     * Инициализирует игрока и врагов, настраивает контроллеры и ввод.
     *
     * @param characterType выбранный тип персонажа
     */
    private void startGame(String characterType) {
        FXGL.getGameWorld().removeEntities(FXGL.getGameWorld().getEntitiesCopy());
        FXGL.getGameScene().clearUINodes();
        FXGL.getInput().clearAll();

        player = gameInitService.initPlayer(characterType);
        gameInitService.spawnEnemies(List.of(
                new double[]{100, 100},
                new double[]{200, 100},
                new double[]{300, 100},
                new double[]{250, 100},
                new double[]{250, 100},
                new double[]{350, 100}

                ));

        HasSpeed playerData = (HasSpeed) player.getComponent(SpeedComponent.class); // SpeedComponent реализует HasSpeed
        movementController = new MovementController(playerData, player);

        uiService.initUI(player);

        Supplier<Entity> entitySupplier = () -> player;
        switch (characterType) {
            case "knight" -> KnightController.initInput(entitySupplier);
            case "panther" -> PantherController.initInput(entitySupplier);
        }
    }

    @Override
    protected void onUpdate(double tpf) {
        if (player == null) return;

        movementController.update(tpf);
        collisionSystem.update(player, tpf);
        uiService.update();

        uiService.checkGameOver(player, () -> startGame(currentCharacterType));
    }

    /**
     * Точка входа в игру.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        launch(args);
    }
}
