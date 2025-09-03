package org.example.deadknight.gameplay.actors.player.services;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import org.example.deadknight.gameplay.components.WaveComponent;

import static com.almasb.fxgl.dsl.FXGL.*;

public class WaveService {

    public static void shoot(Entity shooter) {
        if (shooter == null) return;

        String dir = shooter.getProperties().getValue("direction");
        Point2D vector = getDirectionVector(dir);
        double[] size = getWaveSize(dir);
        Texture waveTex = prepareTexture(dir);

        double[] offsets = getOffsets(dir, size[0], size[1]);

        Entity wave = entityBuilder()
                .at(shooter.getCenter().getX() - 32, shooter.getCenter().getY() - 32)
                .view(waveTex)
                .bbox(new HitBox("BODY",
                        new Point2D(offsets[0], offsets[1]),
                        BoundingShape.box(size[0], size[1])))
                .with(new WaveComponent(vector))
                .buildAndAttach();

        runOnce(() -> {
            if (wave.isActive()) wave.removeFromWorld();
        }, Duration.seconds(1));
    }

    private static Point2D getDirectionVector(String dir) {
        return switch (dir) {
            case "UP" -> new Point2D(0, -1);
            case "DOWN" -> new Point2D(0, 1);
            case "LEFT" -> new Point2D(-1, 0);
            default -> new Point2D(1, 0); // RIGHT
        };
    }

    private static double[] getWaveSize(String dir) {
        return switch (dir) {
            case "UP", "DOWN" -> new double[]{64, 2};
            case "LEFT", "RIGHT" -> new double[]{2, 64};
            default -> new double[]{64, 64};
        };
    }

    private static Texture prepareTexture(String dir) {
        Texture waveTex = texture("wave.png");
        waveTex.setFitWidth(64);
        waveTex.setFitHeight(64);

        switch (dir) {
            case "UP" -> waveTex.setRotate(90);
            case "DOWN" -> waveTex.setRotate(-90);
            case "LEFT" -> waveTex.setRotate(0);
            case "RIGHT" -> {
                waveTex.setRotate(0);
                waveTex.setScaleX(-1);
            }
        }
        return waveTex;
    }

    private static double[] getOffsets(String dir, double width, double height) {
        double offsetX = (64 - width) / 2;
        double offsetY = (64 - height) / 2;
        if (dir.equals("LEFT") || dir.equals("RIGHT")) {
            offsetY = (64 - 2) / 2;
        }
        return new double[]{offsetX, offsetY};
    }
}
