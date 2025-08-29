package org.example.deadknight.init;

import com.almasb.fxgl.dsl.FXGL;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SubScene;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Подсцена загрузочного экрана с прогресс-баром.
 * <p>
 * Используется для плавной подгрузки текстур и отображения прогресса игроку.
 */
public class LoadingScreenSubScene extends SubScene {

    /** Прогресс-бар загрузки */
    private final ProgressBar progressBar;

    /**
     * Создаёт новый загрузочный экран.
     *
     * @param width ширина подсцены
     * @param height высота подсцены
     */
    public LoadingScreenSubScene(double width, double height) {
        super(new StackPane(), width, height);

        StackPane root = (StackPane) getRoot();

        // 1. Фон
        var bgTexture = FXGL.texture("loading_background.png");
        ImageView background = new ImageView(bgTexture.getImage());
        background.setFitWidth(800);
        background.setFitHeight(1000);
        background.setPreserveRatio(true);
        root.getChildren().add(background);

        // 2. Прогрессбар
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(400);
        progressBar.setStyle("-fx-accent: #00ffcc;");
        progressBar.setEffect(new DropShadow(5, Color.BLACK));

        root.getChildren().add(progressBar);
        StackPane.setAlignment(progressBar, Pos.BOTTOM_CENTER);
        StackPane.setMargin(progressBar, new Insets(0, 0, 50, 0));
    }

    /**
     * Асинхронно загружает текстуры с прогрессом.
     * <p>
     * Для каждой текстуры прогресс-бар обновляется, а после окончания вызывается {@code onComplete}.
     *
     * @param onComplete Runnable, который будет вызван после загрузки всех текстур
     */
    public void loadTextures(Runnable onComplete) {
        String[] textures = {
                "knight_left_1.png",
                "knight_left_2.png",
                "knight_left_3.png",
                "knight_left_4.png",
                "knight_left_5.png"
        };

        new Thread(() -> {
            for (int i = 0; i < textures.length; i++) {
                FXGL.texture(textures[i]);
                final int index = i;

                Platform.runLater(() -> progressBar.setProgress((index + 1) / (double) textures.length));

                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            }

            Platform.runLater(onComplete);
        }).start();
    }
}
