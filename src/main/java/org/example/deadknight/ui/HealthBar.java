package org.example.deadknight.ui;

import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.example.deadknight.components.HealthComponent;
import com.almasb.fxgl.entity.Entity;

public class HealthBar {

    private final Rectangle backgroundBar;
    private final Rectangle foregroundBar;
    private final Entity knight;

    public HealthBar(Entity knight) {
        this.knight = knight;

        // Красный фон
        backgroundBar = new Rectangle(200, 20);
        backgroundBar.setFill(Color.DARKRED);
        backgroundBar.setTranslateX(20);
        backgroundBar.setTranslateY(20);

        // Зеленый градиент
        foregroundBar = new Rectangle(200, 20);
        foregroundBar.setFill(new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.LIMEGREEN),
                new Stop(1, Color.DARKGREEN)
        ));
        foregroundBar.setTranslateX(20);
        foregroundBar.setTranslateY(20);

        FXGL.getGameScene().addUINode(backgroundBar);
        FXGL.getGameScene().addUINode(foregroundBar);
    }

    public void update() {
        if (knight == null || knight.getWorld() == null) {
            return; // рыцарь мёртв, обновлять нечего
        }
        HealthComponent health = knight.getComponent(HealthComponent.class);
        double targetWidth = 200 * Math.max(0, health.getValue() / 100.0);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.3),
                        new KeyValue(foregroundBar.widthProperty(), targetWidth))
        );
        timeline.play();
    }

}
