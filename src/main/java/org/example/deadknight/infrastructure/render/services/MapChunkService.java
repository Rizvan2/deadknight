package org.example.deadknight.infrastructure.render.services;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.example.deadknight.infrastructure.generation.BattlefieldBackgroundGenerator;
import org.example.deadknight.infrastructure.render.model.Chunk;

import java.util.*;

import static org.example.deadknight.infrastructure.generation.BattlefieldBackgroundGenerator.tileSize;

/**
 * Сервис управления чанками карты.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>хранение ссылок на тайлы земли;</li>
 *     <li>динамическую подгрузку и выгрузку {@link Chunk}
 *     в зависимости от положения игрока;</li>
 *     <li>оптимизацию рендеринга карты через работу с чанками.</li>
 * </ul>
 *
 * <p>Каждый чанк имеет фиксированный размер {@code CHUNK_SIZE} в тайлах.</p>
 *
 * <h2>Пример использования:</h2>
 * <pre>{@code
 * // groundTiles - двумерный массив тайлов карты
 * MapChunkService service = new MapChunkService(groundTiles);
 *
 * // при движении игрока
 * service.updateVisibleChunks(player.getX(), player.getY());
 * }</pre>
 */
public class MapChunkService {

    /** Тайлы земли, из которых состоит карта */
    private final Image[][] groundTiles;

    /** Размер карты в тайлах по X */
    private final int tilesX;

    /** Размер карты в тайлах по Y */
    private final int tilesY;

    /** Размер чанка в тайлах */
    private static final int CHUNK_SIZE = 5;

    /** Все загруженные чанки, ключ - координаты чанка */
    private final Map<Point2D, Chunk> loadedChunks = new HashMap<>();

    public MapChunkService(Image[][] groundTiles) {
        this.groundTiles = groundTiles;
        this.tilesX = groundTiles.length;
        this.tilesY = groundTiles[0].length;
    }



    /**
     * Определяет координаты чанка, в котором находится игрок.
     */
    private Point2D getPlayerChunkCoords(double playerX, double playerY) {
        int tileX = (int) (playerX / BattlefieldBackgroundGenerator.tileSize);
        int tileY = (int) (playerY / BattlefieldBackgroundGenerator.tileSize);

        int chunkX = tileX / CHUNK_SIZE;
        int chunkY = tileY / CHUNK_SIZE;

        return new Point2D(chunkX, chunkY);
    }

    private Set<Point2D> calculateVisibleChunks(Point2D playerChunk) {
        Set<Point2D> visible = new HashSet<>();

        int cxCenter = (int) playerChunk.getX();
        int cyCenter = (int) playerChunk.getY();

        // добавляем центральный чанк и верхний/нижний по одному
        for (int dx = -2; dx <= 2; dx++) {       // два ряда слева и справа
            for (int dy = -1; dy <= 1; dy++) {   // один ряд сверху и снизу
                int cx = cxCenter + dx;
                int cy = cyCenter + dy;

                if (cx >= 0 && cy >= 0 && cx * CHUNK_SIZE < tilesX && cy * CHUNK_SIZE < tilesY) {
                    visible.add(new Point2D(cx, cy));
                }
            }
        }

        return visible;
    }

    public void updateVisibleChunks(double playerX, double playerY) {
        Point2D playerChunk = getPlayerChunkCoords(playerX, playerY);
        Set<Point2D> newVisible = calculateVisibleChunks(playerChunk);

        loadNewChunks(newVisible);
        unloadInvisibleChunks(newVisible);
    }


    /**
     * Загружает новые чанки, которые ещё не были в памяти.
     */
    private void loadNewChunks(Set<Point2D> newVisible) {
        for (Point2D coord : newVisible) {
            if (!loadedChunks.containsKey(coord)) {
                Chunk chunk = loadChunk((int) coord.getX(), (int) coord.getY());
                loadedChunks.put(coord, chunk);
            }
        }
    }

    /**
     * Выгружает чанки, которые больше не видны.
     */
    private void unloadInvisibleChunks(Set<Point2D> newVisible) {
        loadedChunks.keySet().removeIf(coord -> {
            if (!newVisible.contains(coord)) {
                loadedChunks.get(coord).unload();
                return true;
            }
            return false;
        });
    }


    /**
     * Загружает новый чанк: собирает тайлы и создаёт сущности.
     */
    private Chunk loadChunk(int cx, int cy) {
        Chunk chunk = new Chunk(cx, cy, CHUNK_SIZE);

        ImageView view = createChunkView(cx * CHUNK_SIZE, cy * CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE);
        chunk.setGroundLayer(view);

        Entity chunkEntity = FXGL.entityBuilder()
                .at(chunk.getWorldX(), chunk.getWorldY())
                .view(view)
                .zIndex(-100)
                .buildAndAttach();

        chunk.addEntity(chunkEntity);

        return chunk;
    }

    /**
     * Собирает картинку чанка из тайлов.
     */
    private ImageView createChunkView(int startX, int startY, int chunkSizeX, int chunkSizeY) {
        Pane temp = new Pane();
        temp.setPrefSize(chunkSizeX * tileSize, chunkSizeY * tileSize);

        for (int y = 0; y < chunkSizeY; y++) {
            for (int x = 0; x < chunkSizeX; x++) {
                int worldX = startX + x;
                int worldY = startY + y;
                if (worldX >= tilesX || worldY >= tilesY) continue;

                Image ground = groundTiles[worldX][worldY];
                if (ground != null) {
                    ImageView iv = new ImageView(ground);
                    iv.setFitWidth(tileSize);
                    iv.setFitHeight(tileSize);
                    iv.setTranslateX(x * tileSize);
                    iv.setTranslateY(y * tileSize);
                    temp.getChildren().add(iv);
                }
            }
        }

        return new ImageView(temp.snapshot(null, null));
    }
}
