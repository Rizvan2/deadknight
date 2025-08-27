package org.example.deadknight;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.controllers.MovementController;
import org.example.deadknight.controllers.PantherController;
import org.example.deadknight.controllers.KnightController;
import org.example.deadknight.init.GameInitializer;
import org.example.deadknight.init.SettingsInitializer;
import org.example.deadknight.systems.CollisionSystem;
import org.example.deadknight.ui.CharacterSelectScreen;
import org.example.deadknight.ui.UIController;

import java.util.function.Supplier;


/**
 * Главный класс приложения DeadKnight.
 * <p>
 * Отвечает за инициализацию игры, управление вводом,
 * обновление состояния персонажа и взаимодействие с объектами мира.
 */
public class DeadKnightApp extends GameApplication {

    /** Сущность рыцаря, которой управляет игрок. */
    private Entity knight;

    /** Контроллер движения рыцаря. */
    private MovementController movementController;

    /** Система обработки столкновений и урона. */
    private CollisionSystem collisionSystem;

    /** Контроллер UI (например, HealthBar). */
    private UIController uiController;

    @Override
    protected void initSettings(GameSettings settings) {
        SettingsInitializer.initSettings(settings);
    }

    @Override
    protected void initGame() {
        CharacterSelectScreen.show(characterType -> {
            FXGL.getGameScene().clearUINodes(); // убираем экран выбора
            startGame(characterType);           // запускаем игру
        });
    }



    private void startGame(String characterType) {
        FXGL.getGameWorld().removeEntities(FXGL.getGameWorld().getEntitiesCopy());
        FXGL.getGameScene().clearUINodes();

        knight = GameInitializer.initGame(characterType);

        movementController = new MovementController(knight);
        collisionSystem = new CollisionSystem();
        uiController = new UIController(knight);

        // Подключаем контроллер ввода динамически
        Supplier<Entity> entitySupplier = () -> knight;
        switch (characterType) {
            case "knight" -> KnightController.initInput(entitySupplier);
            case "panther" -> PantherController.initInput(entitySupplier);
        }
        System.out.println("Starting game with: " + characterType);
        System.out.println("Knight: " + (knight != null));
    }


    @Override
    protected void onUpdate(double tpf) {
        if (knight == null) return;

        movementController.update(tpf);
        collisionSystem.update(knight, tpf);
        uiController.update();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

