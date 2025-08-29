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
 * <p>
 * Для каждой волны:
 * <ul>
 *     <li>Определяется направление движения на основе свойства "direction" shooter'а</li>
 *     <li>В зависимости от направления подбирается размер хитбокса (width, height)</li>
 *     <li>Применяется поворот и отражение текстуры для визуала</li>
 * </ul>
 */
public class WaveService {

    /**
     * Выпускает волну из позиции персонажа.
     *
     * @param shooter объект, выпускающий волну (например, игрок или враг)
     */
    public static void shoot(Entity shooter) {
        if (shooter == null) return;

        String dir = shooter.getProperties().getValue("direction");
        Point2D vector;

        // Эти переменные определяют размер хитбокса волны в зависимости от направления
        double width = 64;
        double height = 64;

        switch (dir) {
            case "UP" -> {
                vector = new Point2D(0, -1);
                width = 64;
                height = 2;
            }
            case "DOWN" -> {
                vector = new Point2D(0, 1);
                width = 64;
                height = 2;
            }
            case "LEFT" -> {
                vector = new Point2D(-1, 0);
                width = 2;
                height = 64;
            }
            default -> {
                vector = new Point2D(1, 0);
                width = 2;
                height = 64;
            } // RIGHT
        }


        Texture waveTex = texture("wave.png");
        waveTex.setFitWidth(64);
        waveTex.setFitHeight(64);

// Поворот и отражение только для визуала
        switch (dir) {
            case "UP" -> waveTex.setRotate(90);
            case "DOWN" -> waveTex.setRotate(-90);
            case "LEFT" -> waveTex.setRotate(0);
            case "RIGHT" -> {
                waveTex.setRotate(0);
                waveTex.setScaleX(-1);
            }
        }

// Считаем смещение для хитбокса
        double offsetX = (64 - width) / 2;
        double offsetY = (64 - height) / 2;

// Для горизонтальных направлений хитбокс тонкий, и нужно центрировать его по вертикали
        if (dir.equals("LEFT") || dir.equals("RIGHT")) {
            offsetY = (64 - 2) / 2; // центрировать тонкую полоску
        }

// Стартовая позиция: центр стрелка минус половина текстуры
        double startX = shooter.getCenter().getX() - 32;
        double startY = shooter.getCenter().getY() - 32;

        Entity wave = entityBuilder()
                .at(startX, startY)
                .view(waveTex)
                .bbox(new HitBox("BODY", new Point2D(offsetX, offsetY), BoundingShape.box(width, height)))
                .with(new WaveComponent(vector))
                .buildAndAttach();


        runOnce(() -> {
            if (wave.isActive()) wave.removeFromWorld();
        }, Duration.seconds(1));

    }
}
