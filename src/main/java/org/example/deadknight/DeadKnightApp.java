package org.example.deadknight;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import org.example.deadknight.config.GameConfig;
import org.example.deadknight.gameplay.actors.essences.systems.EssenceCollisionInitializer;
import org.example.deadknight.gameplay.components.types.EntityTypeEssences;

import org.example.deadknight.gameplay.actors.essences.factory.EssenceFactory;
import org.example.deadknight.gameplay.components.SpeedComponent;
import org.example.deadknight.gameplay.actors.player.controllers.KnightController;
import org.example.deadknight.gameplay.actors.player.controllers.MovementController;
import org.example.deadknight.gameplay.actors.player.controllers.PantherController;
import org.example.deadknight.gameplay.actors.player.services.HasSpeed;
import org.example.deadknight.infrastructure.dto.GameWorldData;
import org.example.deadknight.infrastructure.render.services.MapChunkService;
import org.example.deadknight.services.CameraService;
import org.example.deadknight.services.GameFlowService;
import org.example.deadknight.services.GameInitializerService;
import org.example.deadknight.services.UIService;
import org.example.deadknight.gameplay.actors.player.systems.CollisionSystem;
import org.example.deadknight.services.debug.DebugOverlayService;
import org.example.deadknight.services.init.GameInitializer;
import org.example.deadknight.services.init.SettingsInitializer;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Главный класс приложения DeadKnight.
 * <p>
 * Отвечает за полный игровой цикл:
 * <ul>
 *     <li>Инициализация настроек игры</li>
 *     <li>Выбор персонажа и показ экрана загрузки</li>
 *     <li>Создание игрока и врагов через {@link GameInitializerService}</li>
 *     <li>Настройку контроллеров и системы движения</li>
 *     <li>Инициализацию UI через {@link UIService}</li>
 *     <li>Обновление состояния игрока, системы столкновений и UI каждый кадр</li>
 *     <li>Обработку конца игры и возможность перезапуска</li>
 * </ul>
 */
public class DeadKnightApp extends GameApplication {

    /**
     * Сущность игрока, которой управляет пользователь
     */
    private Entity player;

    /**
     * Контроллер движения игрока
     */
    private MovementController movementController;

    /**
     * Система обработки столкновений и урона
     */
    private CollisionSystem collisionSystem;

    /**
     * Сервис для управления интерфейсом игрока
     */
    private UIService uiService;

    /**
     * Сервис инициализации игрока и врагов
     */
    private GameInitializerService gameInitService;

    /**
     * Тип текущего выбранного персонажа ("knight"/"panther")
     */
    private String currentCharacterType;

    private MapChunkService mapChunkService;

    @Override
    protected void initSettings(GameSettings settings) {
        SettingsInitializer.initSettings(settings);
    }

    @Override
    protected void initGame() {

        FXGL.getGameWorld().addEntityFactory(new EssenceFactory());

        gameInitService = new GameInitializerService();
        uiService = new UIService();
        collisionSystem = new CollisionSystem();
        GameFlowService gameFlowService = new GameFlowService();

        // Показываем экран выбора персонажа
        gameFlowService.startCharacterSelection(characterType -> {
            currentCharacterType = characterType;

            // Генерация карты и создание мира через GameInitializer
            GameWorldData worldData = GameInitializer.createGameWorld(characterType);
            player = worldData.player();
            mapChunkService = worldData.mapChunkService();

            // Настройка камеры через CameraService
            CameraService cameraService = new CameraService(0, 2.0, 1.05);
            cameraService.bindToEntity(
                    player,
                    FXGL.getAppWidth(),
                    FXGL.getAppHeight(),
                    worldData.mapWidth(),
                    worldData.mapHeight()
            );
            cameraService.setupZoom();

            startGame(characterType);
        });

        // Настройка отладочных клавиш
        DebugOverlayService debugService = new DebugOverlayService();
        setupDebugKeys(debugService);
        debugService.init();
    }


    /**
     * Настройка клавиш для отладки.
     * <p>
     * F3 переключает видимость хитбоксов через {@link GameConfig#DEBUG_HITBOXES}.
     *
     * @param debugService сервис для отрисовки хитбоксов
     */
    private void setupDebugKeys(DebugOverlayService debugService) {
        FXGL.onKeyDown(KeyCode.F3, () -> {
            GameConfig.DEBUG_HITBOXES = !GameConfig.DEBUG_HITBOXES;
            if (!GameConfig.DEBUG_HITBOXES) {
                debugService.clear();
            }
        });
    }

    /**
     * Инициализация физического мира и обработчиков коллизий.
     * <p>
     * В данном случае добавляется обработчик столкновения рыцаря с сущностью
     * здоровья ({@link EntityTypeEssences#HEALTH_ESSENCE}) и автоматическое
     * восстановление здоровья игрока.
     */
    @Override
    protected void initPhysics() {
        new EssenceCollisionInitializer().init();
    }

    /**
     * Запускает игру после выбора персонажа:
     * <ul>
     *     <li>Очищает сцену и ввод</li>
     *     <li>Создаёт игрока и настраивает контроллер движения</li>
     *     <li>Инициализирует UI и спавнит врагов</li>
     * </ul>
     *
     * @param characterType выбранный тип персонажа ("knight" или "panther")
     */
    private void startGame(String characterType) {
        clearScene();
        initPlayerAndController(characterType);
        initUIAndEnemies(characterType);
    }

    /**
     * Очищает игровую сцену от старых сущностей и UI, сбрасывает ввод.
     */
    private void clearScene() {
        FXGL.getGameWorld().removeEntities(FXGL.getGameWorld().getEntitiesCopy());
        FXGL.getGameScene().clearUINodes();
        FXGL.getInput().clearAll();

        // Очищаем все чанки, чтобы не осталось "белых" канвасов
        mapChunkService.clearChunks();
    }

    private void initPlayerAndController(String characterType) {
        // Создаём игрока
        player = gameInitService.initPlayer(characterType);

        // Добавляем игрока в мир
        FXGL.getGameWorld().addEntity(player);

        // Настройка контроллера движения
        HasSpeed playerData = (HasSpeed) player.getComponent(SpeedComponent.class);
        movementController = new MovementController(playerData, player);

        Supplier<Entity> entitySupplier = () -> player;
        switch (characterType) {
            case "knight" -> KnightController.initInput(entitySupplier);
            case "panther" -> PantherController.initInput(entitySupplier);
        }

        // Привязка камеры к игроку
        FXGL.getGameScene().getViewport().bindToEntity(
                player,
                FXGL.getAppWidth() / 2.0,
                FXGL.getAppHeight() / 2.0
        );
    }

    /**
     * Инициализирует интерфейс игрока и спавнит врагов на сцене.
     *
     * @param characterType выбранный тип персонажа (для совместимости с будущими изменениями)
     */
    private void initUIAndEnemies(String characterType) {
        uiService.initUI(player);

        // определяем чанки, видимые вокруг игрока
        Point2D playerChunk = mapChunkService.worldToChunk(player.getPosition());
        Set<Point2D> visibleChunks = mapChunkService.calculateVisibleChunks(playerChunk);

        // спавним врагов на границах видимости
        gameInitService.spawnEnemiesAroundPlayer(player, 100, 0.5);
    }

    /**
     * Обновление состояния игры каждый кадр:
     * <ul>
     *     <li>Движение игрока</li>
     *     <li>Система столкновений</li>
     *     <li>Обновление UI</li>
     *     <li>Проверка окончания игры и перезапуск</li>
     * </ul>
     *
     * @param tpf время прошедшее с последнего кадра (time per frame)
     */
    @Override
    protected void onUpdate(double tpf) {
        if (player == null) return;

        movementController.update(tpf);
        collisionSystem.update(player, tpf);
        uiService.update();
        uiService.checkGameOver(player, () -> startGame(currentCharacterType));

        // ленивое обновление чанков
        mapChunkService.updateVisibleChunks(player.getX(), player.getY());
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
