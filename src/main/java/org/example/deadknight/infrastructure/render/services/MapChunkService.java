package org.example.deadknight.infrastructure.render.services;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.example.deadknight.infrastructure.render.model.Chunk;

import java.util.*;

import static org.example.deadknight.infrastructure.generation.BattlefieldBackgroundGenerator.tileSize;

public class MapChunkService {

    private final Image[][] groundTiles;
    private final int tilesX;
    private final int tilesY;

    /** Размер чанка в тайлах */
    private static final int CHUNK_SIZE = 5;

    /** Загруженные чанки */
    private final Map<Point2D, Chunk> loadedChunks = new HashMap<>();

    public MapChunkService(Image[][] groundTiles) {
        this.groundTiles = groundTiles;
        this.tilesX = groundTiles.length;
        this.tilesY = groundTiles[0].length;
    }

    /** Определяем координаты чанка, где находится игрок */
    private Point2D getPlayerChunkCoords(double playerX, double playerY) {
        int tileX = (int) (playerX / tileSize);
        int tileY = (int) (playerY / tileSize);
        return new Point2D(tileX / CHUNK_SIZE, tileY / CHUNK_SIZE);
    }

    /** Рассчитываем видимые чанки вокруг игрока */
    private Set<Point2D> calculateVisibleChunks(Point2D playerChunk) {
        Set<Point2D> visible = new HashSet<>();
        int cxCenter = (int) playerChunk.getX();
        int cyCenter = (int) playerChunk.getY();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int cx = cxCenter + dx;
                int cy = cyCenter + dy;
                if (cx >= 0 && cy >= 0 && cx * CHUNK_SIZE < tilesX && cy * CHUNK_SIZE < tilesY) {
                    visible.add(new Point2D(cx, cy));
                }
            }
        }
        return visible;
    }

    /** Обновляем видимые чанки */
    public void updateVisibleChunks(double playerX, double playerY) {
        Point2D playerChunk = getPlayerChunkCoords(playerX, playerY);
        Set<Point2D> newVisible = calculateVisibleChunks(playerChunk);
        loadNewChunks(newVisible);
        unloadInvisibleChunks(newVisible);
    }

    /** Загружаем новые чанки */
    private void loadNewChunks(Set<Point2D> newVisible) {
        for (Point2D coord : newVisible) {
            if (!loadedChunks.containsKey(coord)) {
                Chunk chunk = loadChunk((int) coord.getX(), (int) coord.getY());
                loadedChunks.put(coord, chunk);
            }
        }
    }

    /** Выгружаем чанки, которые больше не видны */
    private void unloadInvisibleChunks(Set<Point2D> newVisible) {
        loadedChunks.keySet().removeIf(coord -> {
            if (!newVisible.contains(coord)) {
                loadedChunks.get(coord).unload();
                return true;
            }
            return false;
        });
    }

    /** Загружаем чанк с Canvas */
    private Chunk loadChunk(int cx, int cy) {
        Chunk chunk = new Chunk(cx, cy, CHUNK_SIZE);

        // Рассчитываем сколько тайлов рисуем с дополнительным рядом справа/снизу
        int chunkWidthTiles = Math.min(CHUNK_SIZE + 1, tilesX - cx * CHUNK_SIZE);
        int chunkHeightTiles = Math.min(CHUNK_SIZE + 1, tilesY - cy * CHUNK_SIZE);

        Canvas canvas = chunk.getCanvas();
        canvas.setWidth(chunkWidthTiles * tileSize);
        canvas.setHeight(chunkHeightTiles * tileSize);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        for (int y = 0; y < chunkHeightTiles; y++) {
            for (int x = 0; x < chunkWidthTiles; x++) {
                int worldX = cx * CHUNK_SIZE + x;
                int worldY = cy * CHUNK_SIZE + y;
                if (worldX >= tilesX || worldY >= tilesY) continue;

                Image tile = groundTiles[worldX][worldY];
                if (tile != null) {
                    int renderTileSize = tileSize;
                    gc.drawImage(tile,
                            Math.round(x * renderTileSize),
                            Math.round(y * renderTileSize),
                            renderTileSize,
                            renderTileSize
                    );

                }
            }
        }

        // Создаем сущность FXGL
        Entity entity = FXGL.entityBuilder()
                .at(Math.round(cx * CHUNK_SIZE * tileSize), Math.round(cy * CHUNK_SIZE * tileSize))
                .view(canvas)
                .zIndex(-100)
                .buildAndAttach();

        chunk.addEntity(entity);

        return chunk;
    }

}
