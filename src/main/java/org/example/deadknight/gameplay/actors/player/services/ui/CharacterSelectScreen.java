package org.example.deadknight.gameplay.actors.player.services.ui;

import com.almasb.fxgl.dsl.FXGL;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Экран выбора персонажа.
 * <p>
 * Отображает кнопки для выбора рыцаря или пантеры
 * и уведомляет колбэк о выбранном персонаже.
 * <p>
 * В Java 21 можно использовать <b>виртуальные потоки</b> для асинхронной загрузки изображений,
 * чтобы UI не блокировался при загрузке тяжёлой графики.
 * Виртуальные потоки — это лёгкие потоки, которые JVM планирует на системные потоки,
 * что позволяет запускать тысячи параллельных задач без перегрузки системы.
 * <p>
 * Применение в игровом проекте:
 * <ul>
 *     <li>Асинхронная загрузка спрайтов и текстур.</li>
 *     <li>Сетевые запросы (мультиплеер, рейтинги, статистика).</li>
 *     <li>Фоновая обработка данных и логирование.</li>
 * </ul>
 */
public class CharacterSelectScreen {

    /**
     * Отображает экран выбора персонажа.
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

        var pantherImage = new ImageView();
        pantherImage.setFitWidth(200);
        pantherImage.setFitHeight(200);
        pantherImage.setPreserveRatio(true);
        btnPanther.setGraphic(pantherImage);

// Полностью виртуальный поток для загрузки
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> {
                // Загружаем картинку в виртуальном потоке, UI не блокируется
                Image img = new Image(
                        CharacterSelectScreen.class.getResource("/assets/textures/bleckpanter.png").toExternalForm()
                );

                // На UI-поток только один вызов, чтобы отобразить результат
                Platform.runLater(() -> pantherImage.setImage(img));
            });
        }



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
