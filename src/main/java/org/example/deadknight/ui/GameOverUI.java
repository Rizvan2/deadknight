package org.example.deadknight.ui;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GameOverUI {

    /**
     * Показывает экран смерти персонажа с кнопкой "Restart".
     * @param onRestart Runnable, который вызывается при рестарте игры
     */
    public static void show(Runnable onRestart) {
        Button restartBtn = new Button("Restart");
        restartBtn.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 18;");
        restartBtn.setOnAction(e -> {
            FXGL.getGameScene().clearUINodes(); // убираем UI
            onRestart.run();                     // рестарт игры
        });

        VBox layout = new VBox(20, restartBtn);
        layout.setStyle("-fx-alignment: center;");
        StackPane background = new StackPane(layout);
        background.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        background.setStyle("-fx-background-color: black;");

        FXGL.addUINode(background);
    }
}
