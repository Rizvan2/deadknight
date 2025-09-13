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

/**
 * Сервис для управления чанками карты и их рендеринга с использованием Canvas.
 *
 * <p>Особенности:
 * <ul>
 *     <li>Хранит загруженные чанки в loadedChunks.</li>
 *     <li>Перемещает невидимые чанки в LRU-кеш cachedChunks.</li>
 *     <li>Кеш автоматически удаляет старые чанки при превышении CACHE_LIMIT.</li>
 *     <li>Повторно использует чанки из кеша вместо пересоздания.</li>
 * </ul>
 */
public class MapChunkService {

    private final Image[][] groundTiles;
    private final int tilesX;
    private final int tilesY;

    /** Размер чанка в тайлах */
    public static final int CHUNK_SIZE = 5;

    /** Максимальный размер LRU-кеша */
    private static final int CACHE_LIMIT = 20;

    /** Загруженные в мир чанки */
    private final Map<Point2D, Chunk> loadedChunks = new HashMap<>();

    /** LRU-кеш чанков, accessOrder=true для автоматического удаления старых */
    private final Map<Point2D, Chunk> cachedChunks = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Point2D, Chunk> eldest) {
            if (size() > CACHE_LIMIT) {
                eldest.getValue().unload(); // реально освобождаем ресурсы
                return true; // автоматически удаляем из кеша
            }
            return false;
        }
    };

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
    public Set<Point2D> calculateVisibleChunks(Point2D playerChunk) {
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

    /**
     * Переносит невидимые чанки в кеш.
     * Старые чанки в кеше будут автоматически удалены через LRU-механику.
     *
     * @param newVisible множество координат чанков, которые должны быть видны
     */
    private void unloadInvisibleChunks(Set<Point2D> newVisible) {
        moveInvisibleChunksToCache(newVisible);
    }

    /** Переносим невидимые чанки из loadedChunks в cachedChunks */
    private void moveInvisibleChunksToCache(Set<Point2D> newVisible) {
        Iterator<Map.Entry<Point2D, Chunk>> it = loadedChunks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Point2D, Chunk> entry = it.next();
            Point2D coord = entry.getKey();
            Chunk chunk = entry.getValue();

            if (!newVisible.contains(coord)) {
                chunk.detach(); // убираем из мира, но не уничтожаем
                cachedChunks.put(coord, chunk); // перемещаем в кеш
                it.remove();
            }
        }
    }

    /** Загружаем новые чанки или достаем их из кеша */
    private void loadNewChunks(Set<Point2D> newVisible) {
        for (Point2D coord : newVisible) {
            if (loadedChunks.containsKey(coord)) continue;

            Chunk chunk = cachedChunks.remove(coord); // проверяем кеш
            if (chunk != null) {
                chunk.attach(); // добавляем обратно в мир
            } else {
                chunk = loadChunk((int) coord.getX(), (int) coord.getY());
            }
            loadedChunks.put(coord, chunk);
        }
    }

    /** Загружаем чанк с Canvas */
    private Chunk loadChunk(int cx, int cy) {
        Chunk chunk = createChunk(cx, cy);
        renderTiles(chunk, cx, cy);
        attachEntity(chunk);
        return chunk;
    }

    /** Создает объект Chunk с канвасом нужного размера */
    private Chunk createChunk(int cx, int cy) {
        Chunk chunk = new Chunk(cx, cy, CHUNK_SIZE);

        int chunkWidthTiles = Math.min(CHUNK_SIZE + 1, tilesX - cx * CHUNK_SIZE);
        int chunkHeightTiles = Math.min(CHUNK_SIZE + 1, tilesY - cy * CHUNK_SIZE);

        Canvas canvas = chunk.getCanvas();
        canvas.setWidth(chunkWidthTiles * tileSize);
        canvas.setHeight(chunkHeightTiles * tileSize);

        return chunk;
    }

    /** Отрисовывает тайлы на канвасе чанка */
    private void renderTiles(Chunk chunk, int cx, int cy) {
        Canvas canvas = chunk.getCanvas();
        GraphicsContext gc = canvas.getGraphicsContext2D();

        int chunkWidthTiles = (int) (canvas.getWidth() / tileSize);
        int chunkHeightTiles = (int) (canvas.getHeight() / tileSize);

        for (int y = 0; y < chunkHeightTiles; y++) {
            for (int x = 0; x < chunkWidthTiles; x++) {
                int worldX = cx * CHUNK_SIZE + x;
                int worldY = cy * CHUNK_SIZE + y;
                if (worldX >= tilesX || worldY >= tilesY) continue;

                Image tile = groundTiles[worldX][worldY];
                if (tile != null) {
                    gc.drawImage(tile,
                            Math.round(x * tileSize),
                            Math.round(y * tileSize),
                            tileSize,
                            tileSize
                    );
                }
            }
        }
    }

    /** Создает FXGL Entity и привязывает к чанку */
    private void attachEntity(Chunk chunk) {
        Entity entity = FXGL.entityBuilder()
                .at(Math.round(chunk.getWorldX()), Math.round(chunk.getWorldY()))
                .view(chunk.getCanvas())
                .zIndex(-100)
                .buildAndAttach();
        chunk.addEntity(entity);
    }

    /**
     * Переводит мировые координаты в координаты чанка.
     *
     * @param worldPos мировая позиция (например, позиция игрока)
     * @return координаты чанка в виде {@link Point2D}
     */
    public Point2D worldToChunk(Point2D worldPos) {
        int chunkX = (int) Math.floor(worldPos.getX() / (CHUNK_SIZE * tileSize));
        int chunkY = (int) Math.floor(worldPos.getY() / (CHUNK_SIZE * tileSize));
        return new Point2D(chunkX, chunkY);
    }

    /**
     * Полностью очищает все загруженные и кешированные чанки.
     * <p>
     * Для каждого чанка вызывается {@link Chunk#unload()}, после чего
     * коллекции {@code loadedChunks} и {@code cachedChunks} очищаются.
     * </p>
     * <p>
     * Используется, например, при перезапуске игры, смене карты или
     * необходимости освободить ресурсы.
     * </p>
     */
    public void clearChunks() {
        for (Chunk chunk : loadedChunks.values()) {
            chunk.unload();
        }
        loadedChunks.clear();

        for (Chunk chunk : cachedChunks.values()) {
            chunk.unload();
        }
        cachedChunks.clear();
    }
}
