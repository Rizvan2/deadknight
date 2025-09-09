package org.example.deadknight.infrastructure.services;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import org.example.deadknight.infrastructure.dto.VisibleChunks;
import org.example.deadknight.infrastructure.generation.BattlefieldBackgroundGenerator;

import java.util.*;

import static org.example.deadknight.infrastructure.generation.BattlefieldBackgroundGenerator.tileSize;

@Getter
@Setter
public class MapChunkService {

    private final Image[][] groundTiles;
//    private final Image[][] treeTiles;

    private final int chunkSizeX;
    private final int chunkSizeY;
    private final int tilesX;
    private final int tilesY;

    private final ImageView[][][] chunkGroundViews; // [chunkX][chunkY][ImageView]
    private final ImageView[][][] chunkTreeViews;

    // Размер чанка в тайлах
    private static final int CHUNK_SIZE = 8;


    public MapChunkService(Image[][] groundTiles,
                           int chunkSizeX, int chunkSizeY) {
        this.groundTiles = groundTiles;
        this.chunkSizeX = chunkSizeX;
        this.chunkSizeY = chunkSizeY;

        this.tilesX = groundTiles.length;
        this.tilesY = groundTiles[0].length;

        int chunksX = (tilesX + chunkSizeX - 1) / chunkSizeX;
        int chunksY = (tilesY + chunkSizeY - 1) / chunkSizeY;

        this.chunkGroundViews = new ImageView[chunksX][chunksY][];
        this.chunkTreeViews = new ImageView[chunksX][chunksY][];

    }

    private final Map<Point2D, List<Entity>> loadedChunkEntities = new HashMap<>();

    public void updateVisibleChunks(double playerX, double playerY) {
        int tileX = (int)(playerX / BattlefieldBackgroundGenerator.tileSize);
        int tileY = (int)(playerY / BattlefieldBackgroundGenerator.tileSize);

        int chunkX = tileX / CHUNK_SIZE;
        int chunkY = tileY / CHUNK_SIZE;

        Set<Point2D> newVisibleChunks = new HashSet<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int cx = chunkX + dx;
                int cy = chunkY + dy;

                if (cx >= 0 && cy >= 0 && cx * CHUNK_SIZE < tilesX && cy * CHUNK_SIZE < tilesY) {
                    Point2D chunkCoord = new Point2D(cx, cy);
                    newVisibleChunks.add(chunkCoord);

                    if (!loadedChunkEntities.containsKey(chunkCoord)) {
                        int startX = cx * CHUNK_SIZE;
                        int startY = cy * CHUNK_SIZE;

                        ImageView chunkView = createChunkView(startX, startY, CHUNK_SIZE, CHUNK_SIZE);

                        Entity chunkEntity = FXGL.entityBuilder()
                                .at(startX * BattlefieldBackgroundGenerator.tileSize,
                                        startY * BattlefieldBackgroundGenerator.tileSize)
                                .view(chunkView)
                                .zIndex(-100) // низкий слой
                                .buildAndAttach();

                        loadedChunkEntities.put(chunkCoord, List.of(chunkEntity));
                    }
                }
            }
        }

        // Удаляем чанки вне видимости
        loadedChunkEntities.keySet().removeIf(coord -> {
            if (!newVisibleChunks.contains(coord)) {
                for (Entity e : loadedChunkEntities.get(coord)) {
                    e.removeFromWorld();
                }
                return true;
            }
            return false;
        });
    }


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

//                Image tree = treeTiles[worldX][worldY];
//                if (tree != null) {
//                    ImageView iv = new ImageView(tree);
//                    iv.setFitWidth(tileSize);
//                    iv.setFitHeight(tileSize);
//                    iv.setTranslateX(x * tileSize);
//                    iv.setTranslateY(y * tileSize);
//                    temp.getChildren().add(iv);
//                }
            }
        }

        WritableImage snapshot = temp.snapshot(null, null);
        return new ImageView(snapshot);
    }



//    // Получение Image тайлов чанка из кэша
//    private Image[] getGroundImagesForChunk(int chunkX, int chunkY) {
//        int startX = chunkX * CHUNK_SIZE;
//        int startY = chunkY * CHUNK_SIZE;
//        List<Image> images = new ArrayList<>();
//        for (int x = startX; x < Math.min(startX + CHUNK_SIZE, tilesX); x++) {
//            for (int y = startY; y < Math.min(startY + CHUNK_SIZE, tilesY); y++) {
//                images.add(groundTiles[x][y]);
//            }
//        }
//        return images.toArray(new Image[0]);
//    }
//
//    private Image[] getTreeImagesForChunk(int chunkX, int chunkY) {
//        int startX = chunkX * CHUNK_SIZE;
//        int startY = chunkY * CHUNK_SIZE;
//        List<Image> images = new ArrayList<>();
//        for (int x = startX; x < Math.min(startX + CHUNK_SIZE, tilesX); x++) {
//            for (int y = startY; y < Math.min(startY + CHUNK_SIZE, tilesY); y++) {
//                images.add(treeTiles[x][y]);
//            }
//        }
//        return images.toArray(new Image[0]);
//    }
//
//    /**
//     * Возвращает координаты чанка для заданного тайла.
//     *
//     * @param tileX координата тайла по X
//     * @param tileY координата тайла по Y
//     * @return координаты чанка (chunkX, chunkY)
//     */
//    public static Point2D getChunkCoordForTile(int tileX, int tileY) {
//        int chunkX = tileX / CHUNK_SIZE;
//        int chunkY = tileY / CHUNK_SIZE;
//        return new Point2D(chunkX, chunkY);
//    }
//
//    /**
//     * Возвращает диапазон тайлов для чанка.
//     *
//     * @param chunkX координата чанка по X
//     * @param chunkY координата чанка по Y
//     * @return массив: [xStart, xEnd, yStart, yEnd]
//     */
//    public static int[] getTileRangeForChunk(int chunkX, int chunkY) {
//        int xStart = chunkX * CHUNK_SIZE;
//        int yStart = chunkY * CHUNK_SIZE;
//        int xEnd = xStart + CHUNK_SIZE - 1;
//        int yEnd = yStart + CHUNK_SIZE - 1;
//        return new int[]{xStart, xEnd, yStart, yEnd};
//    }

}
