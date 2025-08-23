package org.example.deadknight.services;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import static com.almasb.fxgl.dsl.FXGL.*;

import org.example.deadknight.components.WaveComponent;

public class WaveService {

    public static void shoot(Entity shooter) {
        if (shooter == null) return;

        // Берём направление для стрельбы
        String dir = shooter.getProperties().getValue("direction");
        Point2D vector;

        switch (dir) {
            case "UP": vector = new Point2D(0, -1); break;
            case "DOWN": vector = new Point2D(0, 1); break;
            case "LEFT": vector = new Point2D(-1, 0); break;
            default: vector = new Point2D(1, 0); // RIGHT
        }

        // Создаём текстуру волны
        Texture waveTex = texture("wave.png");
        waveTex.setFitWidth(64);
        waveTex.setFitHeight(64);

        // Поворот текстуры по направлению
        switch (dir) {
            case "UP": waveTex.setRotate(90); waveTex.setScaleX(1); break;
            case "DOWN": waveTex.setRotate(-90); waveTex.setScaleX(1); break;
            case "LEFT": waveTex.setRotate(0); waveTex.setScaleX(1); break;
            case "RIGHT": waveTex.setRotate(0); waveTex.setScaleX(-1); break;
        }

        // Стрельба волной
        entityBuilder()
                .at(shooter.getCenter())
                .viewWithBBox(waveTex)
                .with(new WaveComponent(vector))
                .buildAndAttach();
    }
}
