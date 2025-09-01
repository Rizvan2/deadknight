package org.example.deadknight;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.player.components.SpeedComponent;
import org.example.deadknight.player.controllers.KnightController;
import org.example.deadknight.player.controllers.MovementController;
import org.example.deadknight.player.controllers.PantherController;
import org.example.deadknight.player.services.HasSpeed;
import org.example.deadknight.services.GameFlowService;
import org.example.deadknight.services.GameInitializerService;
import org.example.deadknight.services.UIService;
import org.example.deadknight.player.systems.CollisionSystem;
import org.example.deadknight.services.init.SettingsInitializer;

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

    @Override
    protected void initSettings(GameSettings settings) {
        SettingsInitializer.initSettings(settings);
    }

    /**
     * Инициализация игры:
     * <ul>
     *     <li>Создание сервисов и систем</li>
     *     <li>Показ экрана выбора персонажа через {@link GameFlowService}</li>
     * </ul>
     */
    @Override
    protected void initGame() {

        gameInitService = new GameInitializerService();
        uiService = new UIService();
        collisionSystem = new CollisionSystem();
        GameFlowService gameFlowService = new GameFlowService();

        // Показываем экран выбора персонажа
        gameFlowService.startCharacterSelection(characterType -> {
            currentCharacterType = characterType;
            startGame(characterType);
        });
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
    }

    /**
     * Создаёт игрока заданного типа и настраивает его контроллер движения.
     *
     * @param characterType выбранный тип персонажа ("knight" или "panther")
     */
    private void initPlayerAndController(String characterType) {
        player = gameInitService.initPlayer(characterType);
        HasSpeed playerData = (HasSpeed) player.getComponent(SpeedComponent.class);
        movementController = new MovementController(playerData, player);

        Supplier<Entity> entitySupplier = () -> player;
        switch (characterType) {
            case "knight" -> KnightController.initInput(entitySupplier);
            case "panther" -> PantherController.initInput(entitySupplier);
        }
    }

    /**
     * Инициализирует интерфейс игрока и спавнит врагов на сцене.
     *
     * @param characterType выбранный тип персонажа (для совместимости с будущими изменениями)
     */
    private void initUIAndEnemies(String characterType) {
        uiService.initUI(player);
        gameInitService.spawnEnemies(4);
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
