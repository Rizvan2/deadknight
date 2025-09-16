package org.example.deadknight.services.init;

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
import org.example.deadknight.gameplay.actors.mobs.factories.GoblinFactory;
import org.example.deadknight.gameplay.services.LootService;

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
        background.setFitWidth(4200);
        background.setFitHeight(1900);
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

    public void loadTextures(Runnable onComplete) {
        LootService lootService = new LootService(); // создаём сервис лута

        GoblinFactory gf = new GoblinFactory(lootService);
        gf.preloadGoblinTextures(() -> {
            Thread.startVirtualThread(() -> {
                String[] textures = {
                        "knight/knight_left-1.png",
                        "knight/knight_left-2.png",
                        "knight/knight_left-3.png",
                        "knight/knight_left-4.png",
                        "knight/knight_left-5.png"
                };

                for (int i = 0; i < textures.length; i++) {
                    FXGL.texture(textures[i]);
                    final int index = i;
                    Platform.runLater(() -> progressBar.setProgress((index + 1) / (double) textures.length));

                    try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                }

                Platform.runLater(onComplete);
            });
        });
    }
}
