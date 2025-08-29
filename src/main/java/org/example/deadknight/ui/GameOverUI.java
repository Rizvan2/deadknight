package org.example.deadknight.ui;

import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.FadeTransition;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Класс для отображения экрана "Game Over".
 * <p>
 * Показывает заголовок, картинку и кнопку рестарта.
 * Заголовок и картинка появляются с плавной анимацией.
 * Кнопка позволяет перезапустить игру через callback {@code onRestart}.
 */
public class GameOverUI {

    /**
     * Отображает экран "Game Over".
     *
     * @param onRestart callback, вызываемый при нажатии на кнопку рестарта.
     */
    public static void show(Runnable onRestart) {
        // Заголовок
        Text title = new Text("GAME OVER");
        title.setFill(Color.RED);
        title.setFont(Font.font("Verdana", 60));
        title.setOpacity(0); // сразу полностью прозрачный

        // Плавное появление заголовка
        FadeTransition fade = new FadeTransition(Duration.seconds(1), title);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        // Картинка
        Image image = new Image(GameOverUI.class.getResource("/assets/textures/knight_Game_over.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);
        imageView.setOpacity(0); // сразу прозрачная

        // Плавное появление картинки
        FadeTransition imageFade = new FadeTransition(Duration.seconds(1), imageView);
        imageFade.setFromValue(0);
        imageFade.setToValue(1);
        imageFade.play();

        // Кнопка рестарта
        Button restartBtn = new Button("Restart");
        restartBtn.setStyle("""
            -fx-background-color: linear-gradient(#ff0000, #990000);
            -fx-text-fill: white;
            -fx-font-size: 24;
            -fx-background-radius: 10;
            -fx-padding: 10 20 10 20;
        """);
        restartBtn.setOnMouseEntered(e -> restartBtn.setStyle("""
            -fx-background-color: linear-gradient(#ff5555, #cc0000);
            -fx-text-fill: white;
            -fx-font-size: 24;
            -fx-background-radius: 10;
            -fx-padding: 10 20 10 20;
        """));
        restartBtn.setOnMouseExited(e -> restartBtn.setStyle("""
            -fx-background-color: linear-gradient(#ff0000, #990000);
            -fx-text-fill: white;
            -fx-font-size: 24;
            -fx-background-radius: 10;
            -fx-padding: 10 20 10 20;
        """));
        restartBtn.setOnAction(e -> {
            FXGL.getGameScene().clearUINodes();
            onRestart.run();
        });

        VBox layout = new VBox(20, title, imageView, restartBtn); // добавили картинку
        layout.setStyle("-fx-alignment: center;");

        StackPane background = new StackPane(layout);
        background.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        background.setStyle("-fx-background-color: rgba(0,0,0,0.7);"); // затемнённый фон

        FXGL.addUINode(background);
    }

}
