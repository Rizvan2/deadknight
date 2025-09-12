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
    private final Canvas canvas;
    private final GraphicsContext gc;

    // текущая FXGL-сущность для этого чанка
    private Entity entity;

    public Chunk(int cx, int cy, int size) {
        this.coords = new Point2D(cx, cy);
        this.size = size;

        int pixelSize = size * BattlefieldBackgroundGenerator.tileSize;
        this.canvas = new Canvas(pixelSize, pixelSize);
        this.gc = canvas.getGraphicsContext2D();
    }

    public GraphicsContext getGc() {
        return gc;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Point2D getCoords() {
        return coords;
    }

    public void addEntity(Entity entity) {
        this.entity = entity;
    }

    public int getWorldX() {
        return (int) (coords.getX() * size * BattlefieldBackgroundGenerator.tileSize);
    }

    public int getWorldY() {
        return (int) (coords.getY() * size * BattlefieldBackgroundGenerator.tileSize);
    }

    /** Прикрепить Canvas как Entity в FXGL-мир */
    public void attach() {
        if (entity == null || !entity.isActive()) {
            entity = com.almasb.fxgl.dsl.FXGL.entityBuilder()
                    .at(getWorldX(), getWorldY())
                    .view(canvas)
                    .zIndex(-100)
                    .buildAndAttach();
        }
    }

    /** Убрать с карты, но не уничтожать */
    public void detach() {
        if (entity != null && entity.isActive()) {
            entity.removeFromWorld();
        }
    }

    /** Полное уничтожение чанка */
    public void unload() {
        detach();
        entity = null;
    }
}
