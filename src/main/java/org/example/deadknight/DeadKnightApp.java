package org.example.deadknight;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.deadknight.components.HealthComponent;
import org.example.deadknight.controllers.MovementController;
import org.example.deadknight.controllers.PantherController;
import org.example.deadknight.controllers.WASDController;
import org.example.deadknight.controllers.KnightController;
import org.example.deadknight.init.GameInitializer;
import org.example.deadknight.init.SettingsInitializer;
import org.example.deadknight.systems.CollisionSystem;
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
        showCharacterSelectMenu();
    }

    private void showCharacterSelectMenu() {
        // Кнопка Рыцарь
        var btnKnight = FXGL.getUIFactoryService().newButton("Рыцарь");
        btnKnight.setOnAction(e -> startGame("knight"));
        btnKnight.setStyle("-fx-background-color: black; -fx-text-fill: white;");

        // Кнопка Пантера с картинкой
        var btnPanther = FXGL.getUIFactoryService().newButton("");
        var pantherImage = new ImageView(
                new Image(getClass().getResource("/assets/textures/bleckpanter.png").toExternalForm())
        );
        pantherImage.setFitWidth(200);
        pantherImage.setFitHeight(200);
        pantherImage.setPreserveRatio(true);
        btnPanther.setGraphic(pantherImage);
        btnPanther.setOnAction(e -> startGame("panther"));
        btnPanther.setStyle("-fx-background-color: black;");

        // hover — меняем картинку на iles.png при наведении
        btnPanther.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
            if (isNowHovered) {
                var ilesImage = new ImageView(
                        new Image(getClass().getResource("/assets/textures/Iles.png").toExternalForm())
                );
                ilesImage.setFitWidth(pantherImage.getFitWidth());
                ilesImage.setFitHeight(pantherImage.getFitHeight());
                ilesImage.setPreserveRatio(true);

                btnPanther.setGraphic(ilesImage);
            } else {
                btnPanther.setGraphic(pantherImage);
            }
        });

        // VBox с кнопками
        var menuBox = new VBox(20, btnKnight, btnPanther);
        menuBox.setAlignment(Pos.CENTER);

        // StackPane с черным фоном на весь экран
        var background = new StackPane(menuBox);
        background.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        background.setStyle("-fx-background-color: black;");

        FXGL.addUINode(background);
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

