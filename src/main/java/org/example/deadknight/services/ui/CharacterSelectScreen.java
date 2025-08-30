package org.example.deadknight.services.ui;

import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Экран выбора персонажа.
 * <p>
 * Отображает кнопки для выбора рыцаря или пантеры
 * и уведомляет колбэк о выбранном персонаже.
 */
public class CharacterSelectScreen {

    /**
     * Отображает экран выбора персонажа.
     * <p>
     * Показывает две кнопки: "Рыцарь" и "Пантера".
     * При нажатии на кнопку вызывается переданный колбэк с типом выбранного персонажа.
     * Также у кнопки пантеры реализован hover-эффект, который меняет изображение при наведении.
     *
     * @param onCharacterSelected колбэк, вызываемый с типом выбранного персонажа ("knight" или "panther")
     */
    public static void show(Consumer<String> onCharacterSelected) {

        // Кнопка рыцаря
        var btnKnight = FXGL.getUIFactoryService().newButton("Рыцарь");
        btnKnight.setOnAction(e -> onCharacterSelected.accept("knight"));
        btnKnight.setStyle("-fx-background-color: black; -fx-text-fill: white;");

        // Кнопка пантеры с картинкой
        var btnPanther = FXGL.getUIFactoryService().newButton(" "); // пробел для корректного клика
        btnPanther.setPrefWidth(220);
        btnPanther.setPrefHeight(220);
        btnPanther.setStyle("-fx-background-color: black;");

        var pantherImage = new ImageView(
                new Image(CharacterSelectScreen.class.getResource("/assets/textures/bleckpanter.png").toExternalForm())
        );
        pantherImage.setFitWidth(200);
        pantherImage.setFitHeight(200);
        pantherImage.setPreserveRatio(true);
        btnPanther.setGraphic(pantherImage);

        btnPanther.setOnAction(e -> onCharacterSelected.accept("panther"));

        // VBox с кнопками
        VBox menuBox = new VBox(20, btnKnight, btnPanther);
        menuBox.setAlignment(Pos.CENTER);

        // StackPane с черным фоном на весь экран
        StackPane background = new StackPane(menuBox);
        background.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        background.setStyle("-fx-background-color: black;");

        FXGL.addUINode(background);
    }

}
