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
 *
 * Отвечает за инициализацию игры, управление вводом,
 * обновление состояния персонажа и взаимодействие с объектами мира.
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
            FXGL.getGameScene().clearUINodes(); // убираем выбор

            // 1. Создаём наш экран загрузки
            LoadingScreenSubScene loadingScreen = new LoadingScreenSubScene(
                    FXGL.getAppWidth(),
                    FXGL.getAppHeight()
            );

            FXGL.getGameScene().addUINode(loadingScreen); // показываем

            // 2. Загружаем текстуры через метод loadTextures
            loadingScreen.loadTextures(() -> {
                FXGL.getGameScene().removeUINode(loadingScreen); // убираем загрузку
                startGame(characterType);                         // запускаем игру
            });
        });
    }




    private void startGame(String characterType) {
        FXGL.getGameWorld().removeEntities(FXGL.getGameWorld().getEntitiesCopy());
        FXGL.getGameScene().clearUINodes();

        FXGL.getInput().clearAll(); // очищаем старые действия

        knight = GameInitializer.initGame(characterType);

        // --- СПАВН ВРАГОВ ---
        FXGL.spawn("goblin", 100, 100);
        FXGL.spawn("goblin", 200, 100);
        FXGL.spawn("goblin", 300, 100);

        // Можно через цикл для нескольких мобов
        // for (int i = 0; i < 5; i++) {
        //     FXGL.spawn("goblin", 50 + i*100, 50 + i*50);
        // }

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

    public static void main(String[] args) {
        launch(args);
    }
}
