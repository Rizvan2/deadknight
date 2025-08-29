package org.example.deadknight.services;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import static com.almasb.fxgl.dsl.FXGL.*;
import javafx.util.Duration;
import org.example.deadknight.components.WaveComponent;

/**
 * Сервис для создания и управления волновыми атаками персонажей.
 * <p>
 * Волна создаётся в направлении персонажа и автоматически удаляется через заданное время.
 */
public class WaveService {

    /**
     * Выпускает волну из позиции персонажа.
     * <p>
     * Направление волны определяется свойством "direction" объекта shooter.
     * Волна создаётся с текстурой "wave.png", масштабируется и вращается в зависимости от направления.
     *
     * @param shooter объект, выпускающий волну (например, игрок или враг)
     */
    public static void shoot(Entity shooter) {
        if (shooter == null) return;

        String dir = shooter.getProperties().getValue("direction");
        Point2D vector;

        double width = 64;
        double height = 64;

        switch (dir) {
            case "UP" -> { vector = new Point2D(0, -1); width = 20; height = 64; }
            case "DOWN" -> { vector = new Point2D(0, 1); width = 20; height = 64; }
            case "LEFT" -> { vector = new Point2D(-1, 0); width = 64; height = 20; }
            default -> { vector = new Point2D(1, 0); width = 64; height = 20; } // RIGHT
        }

        Texture waveTex = texture("wave.png");
        waveTex.setFitWidth(64);
        waveTex.setFitHeight(64);

        // Поворот и отражение для визуала
        switch (dir) {
            case "UP" -> waveTex.setRotate(90);
            case "DOWN" -> waveTex.setRotate(-90);
            case "LEFT" -> waveTex.setRotate(0);
            case "RIGHT" -> {
                waveTex.setRotate(0);
                waveTex.setScaleX(-1);
            }
        }

        Entity wave = entityBuilder()
                .at(shooter.getCenter())
                .viewWithBBox(waveTex)
                .bbox(new HitBox("BODY", BoundingShape.box(5, 5))) // хитбокс по направлению
                .with(new WaveComponent(vector))
                .buildAndAttach();

        runOnce(() -> {
            if (wave.isActive()) wave.removeFromWorld();
        }, Duration.seconds(1));
    }

}
