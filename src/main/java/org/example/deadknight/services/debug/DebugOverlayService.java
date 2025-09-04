package org.example.deadknight.services.debug;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.Getter;
import org.example.deadknight.config.GameConfig;

import java.util.List;

/**
 * Сервис для отрисовки хитбоксов всех сущностей.
 * Можно включать и выключать на лету через флаг {@link GameConfig#DEBUG_HITBOXES}.
 */
public class DebugOverlayService {

    @Getter
    private Canvas canvas;
    private GraphicsContext g;

    public void init() {
        canvas = new Canvas(FXGL.getAppWidth(), FXGL.getAppHeight());
        g = canvas.getGraphicsContext2D();
        canvas.setMouseTransparent(true);
        FXGL.getGameScene().addUINode(canvas);

        FXGL.getGameTimer().runAtInterval(this::draw, Duration.seconds(1.0 / 60));
    }


    private void draw() {
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (!GameConfig.DEBUG_HITBOXES) return;

        List<Entity> entities = FXGL.getGameWorld().getEntities();
        g.setStroke(Color.RED);
        g.setFill(Color.color(1, 0, 0, 0.2));

        for (Entity e : entities) {
            if (!e.hasComponent(BoundingBoxComponent.class)) continue;

            BoundingBoxComponent bbox = e.getComponent(BoundingBoxComponent.class);

            for (HitBox hb : bbox.hitBoxesProperty()) {
                double x = e.getX() + hb.getMinX();
                double y = e.getY() + hb.getMinY();
                g.strokeRect(x, y, hb.getWidth(), hb.getHeight());
                g.fillRect(x, y, hb.getWidth(), hb.getHeight());
            }
        }
    }

    /** Позволяет очистить Canvas (например, при выключении сервиса) */
    public void clear() {
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
