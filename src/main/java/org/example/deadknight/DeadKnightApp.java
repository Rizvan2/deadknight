package org.example.deadknight;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.components.HealthComponent;
import org.example.deadknight.controllers.MovementController;
import org.example.deadknight.controllers.PantherController;
import org.example.deadknight.controllers.KnightController;
import org.example.deadknight.factories.MobAndPlayerFactory;
import org.example.deadknight.init.GameInitializer;
import org.example.deadknight.init.LoadingScreenSubScene;
import org.example.deadknight.init.SettingsInitializer;
import org.example.deadknight.systems.CollisionSystem;
import org.example.deadknight.ui.CharacterSelectScreen;
import org.example.deadknight.ui.GameOverUI;
import org.example.deadknight.ui.UIController;

import java.util.function.Supplier;

/**
 * Главный класс приложения DeadKnight.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Инициализацию игры и настроек</li>
 *     <li>Создание персонажей и врагов</li>
 *     <li>Обработку ввода игрока</li>
 *     <li>Обновление состояния персонажа и UI</li>
 *     <li>Обработку конца игры и перезапуска</li>
 * </ul>
 */
public class DeadKnightApp extends GameApplication {

    /** Сущность персонажа, которой управляет игрок. */
    private Entity knight;

    /** Контроллер движения персонажа. */
    private MovementController movementController;

    /** Система обработки столкновений и урона. */
    private CollisionSystem collisionSystem;

    /** Контроллер UI (например, HealthBar). */
    private UIController uiController;

    /** Тип текущего выбранного персонажа ("knight"/"panther"). */
    private String currentCharacterType;

    /** Флаг для остановки апдейтов после смерти персонажа. */
    private boolean isGameOver = false;

    @Override
    protected void initSettings(GameSettings settings) {
        SettingsInitializer.initSettings(settings);
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new MobAndPlayerFactory());

        CharacterSelectScreen.show(characterType -> {
            currentCharacterType = characterType;
            FXGL.getGameScene().clearUINodes(); // убираем выбор персонажа

            // 1. Создаём экран загрузки
            LoadingScreenSubScene loadingScreen = new LoadingScreenSubScene(
                    FXGL.getAppWidth(),
                    FXGL.getAppHeight()
            );

            FXGL.getGameScene().addUINode(loadingScreen); // показываем экран загрузки

            // 2. Загружаем текстуры и запускаем игру после завершения
            loadingScreen.loadTextures(() -> {
                FXGL.getGameScene().removeUINode(loadingScreen); // убираем экран загрузки
                startGame(characterType);                         // запускаем игру
            });
        });
    }

    /**
     * Инициализация самой игры: спавн игрока и врагов, настройка контроллеров.
     *
     * @param characterType выбранный игроком тип персонажа
     */
    private void startGame(String characterType) {
        FXGL.getGameWorld().removeEntities(FXGL.getGameWorld().getEntitiesCopy());
        FXGL.getGameScene().clearUINodes();
        FXGL.getInput().clearAll(); // очищаем старые действия

        knight = GameInitializer.initGame(characterType);

        // --- Спавн врагов ---
        FXGL.spawn("goblin", 100, 100);
        FXGL.spawn("goblin", 200, 100);
        FXGL.spawn("goblin", 300, 100);

        movementController = new MovementController(knight);
        collisionSystem = new CollisionSystem();
        uiController = new UIController(knight);

        Supplier<Entity> entitySupplier = () -> knight;
        switch (characterType) {
            case "knight" -> KnightController.initInput(entitySupplier);
            case "panther" -> PantherController.initInput(entitySupplier);
        }
    }

    @Override
    protected void onUpdate(double tpf) {
        if (knight == null || isGameOver) return;

        movementController.update(tpf);
        collisionSystem.update(knight, tpf);
        uiController.update();

        HealthComponent health = knight.getComponent(HealthComponent.class);
        if (health.isDead()) {
            isGameOver = true; // останавливаем апдейты
            GameOverUI.show(() -> {
                FXGL.getGameScene().clearUINodes();
                startGame(currentCharacterType); // рестарт
                isGameOver = false;              // разрешаем апдейты снова
            });
        }
    }

    /**
     * Запуск игры.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        launch(args);
    }
}
