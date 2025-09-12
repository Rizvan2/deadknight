package org.example.deadknight.infrastructure.render.model;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;
import org.example.deadknight.infrastructure.generation.BattlefieldBackgroundGenerator;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Чанк карты с Canvas для рендеринга.
 */
@Getter
@Setter
public class Chunk {

    private final Point2D coords;
    private final int size;
    private final List<Entity> entities = new ArrayList<>();
    private final Canvas canvas;
    private final GraphicsContext gc;

    public Chunk(int cx, int cy, int size) {
        this.coords = new Point2D(cx, cy);
        this.size = size;

        int pixelSize = size * BattlefieldBackgroundGenerator.tileSize;
        this.canvas = new Canvas(pixelSize, pixelSize);
        this.gc = canvas.getGraphicsContext2D();
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void unload() {
        for (Entity e : entities) {
            e.removeFromWorld();
        }
        entities.clear();
    }

    public int getWorldX() {
        return (int) (coords.getX() * size * BattlefieldBackgroundGenerator.tileSize);
    }

    public int getWorldY() {
        return (int) (coords.getY() * size * BattlefieldBackgroundGenerator.tileSize);
    }
}
