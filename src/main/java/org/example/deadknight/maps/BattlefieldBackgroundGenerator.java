package org.example.deadknight.maps;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.CacheHint;
import javafx.scene.image.ImageView;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;

import java.util.*;

/**
 * Генератор поля брани (фон) для FXGL.
 * Настроен под тайл 32x32 и карту 256x256 тайлов (8192x8192 px). лучше так не делать у тебя игра зависнет пытаясь сгенерировать такую карту
 */
public class BattlefieldBackgroundGenerator {

    public static volatile int tileSize = 128;
    private final int tilesX; // например 256
    private final int tilesY;
    private final Random rnd;
    private String pick(String[] arr) {
        return arr[rnd.nextInt(arr.length)];
    }

    // Список имён ассетов для травы.
// Каждый элемент массива — это путь к картинке в ресурсах игры.
// FXGL будет подгружать эти файлы, чтобы мы могли случайно выбирать
// разный вариант травы и сделать фон разнообразным, а не одинаковым.
    private final String[] grassVariants = {
            "map/stone/stone-1.png", // вариант камня №1
            "map/stone/stone-2.png" // вариант камня №2
    };

    private final String[] treeVariants = {
            "map/tree/tree-1.png",
            "map/tree/tree-2.png"
    };


    public BattlefieldBackgroundGenerator(int tilesX, int tilesY, long seed) {
        this.tilesX = tilesX;
        this.tilesY = tilesY;
        this.rnd = new Random(seed);
    }


    /** Генерация пола */
    public void generateGroundTiles(Pane root) {
        for (int y = 0; y < tilesY; y++) {
            for (int x = 0; x < tilesX; x++) {
                String t = pick(grassVariants, 0.85); // чаще камень/трава
                placeTileForSnapshot(t, x * tileSize, y * tileSize, 1.0, root); // масштаб 1x
            }
        }
    }

    /** Генерация деревьев поверх пола как тайлы */
    public void generateTreesTiles(Pane root) {
        for (int y = 0; y < tilesY; y++) {
            for (int x = 0; x < tilesX; x++) {
                if (Math.random() < 0.1) { // шанс появления дерева
                    String treePath = treeVariants[(int)(Math.random() * treeVariants.length)];
                    placeTileForSnapshot(treePath, x * tileSize, y * tileSize, 6.0, root); // масштаб 1x
                }
            }
        }
    }


    private String pick(String[] arr, double biasKeep) {
        // biasKeep — вероятность выбрать первый вариант (например для частых grass)
        if (rnd.nextDouble() < biasKeep) return arr[0];
        return pick(arr);
    }

    private void placeTileForSnapshot(String asset, int px, int py, double scale, Pane root) {
        var tex = FXGL.texture(asset);
        var iv = new ImageView(tex.getImage());
        iv.setFitWidth(tileSize * scale);
        iv.setFitHeight(tileSize * scale);
        iv.setSmooth(true);
        iv.setCache(true);
        iv.setTranslateX(px);
        iv.setTranslateY(py);
        root.getChildren().add(iv);
    }

//    public void generateMapWithTrees() {
//        // 1. Генерация травы
//        for (int x = 0; x < tilesX; x++) {
//            for (int y = 0; y < tilesY; y++) {
//                String ground = grassVariants[(int) (Math.random() * grassVariants.length)];
//                placeTile(ground, x * tileSize, y * tileSize, 0, 1.0); // z-index 0 — самый низ
//            }
//        }
//
//        // 2. Генерация деревьев поверх травы
//        for (int x = 0; x < tilesX; x++) {
//            for (int y = 0; y < tilesY; y++) {
//                if (Math.random() < 0.3) { // вероятность появления дерева
//                    String tree = treeVariants[(int) (Math.random() * treeVariants.length)];
//                    placeTile(tree, x * tileSize, y * tileSize, 1000, 3); // z-index 1000 — сверху
//                }
//            }
//        }
//    }

//    private final String[] mudVariants = {"ground/mud-1.png","ground/mud-2.png"};
//    private final String[] rockVariants = {"debris/rock-1.png","debris/rock-2.png"};
//    private final String[] bodyVariants = {"bodies/knight-dead-1.png","bodies/knight-dead-2.png","bodies/goblin-dead-1.png"};
//    private final String[] flagVariants = {"flags/flag-torn-1.png","flags/flag-torn-2.png"};
//    private final String[] bloodVariants = {"decals/blood-1.png","decals/blood-2.png","decals/blood-3.png"};
//    private final String[] propVariants = {"props/shield-1.png","props/sword-1.png"};

//    private void generateMudPatches(double probPerTile) {
//        for (int y = 0; y < tilesY; y++) {
//            for (int x = 0; x < tilesX; x++) {
//                if (rnd.nextDouble() < probPerTile) {
//                    // создаём пятно радиус r
//                    int r = 2 + rnd.nextInt(5);
//                    int cx = x + rnd.nextInt(3) - 1;
//                    int cy = y + rnd.nextInt(3) - 1;
//                    paintBlob(cx, cy, r, mudVariants, 2, 1.0);
//                }
//            }
//        }
//    }
//
//    private void paintBlob(int cx, int cy, int r, String[] variants, int zIndex, double scale) {
//        for (int yy = cy - r; yy <= cy + r; yy++) {
//            for (int xx = cx - r; xx <= cx + r; xx++) {
//                if (xx < 0 || yy < 0 || xx >= tilesX || yy >= tilesY) continue;
//                Point2D p1 = new Point2D(cx, cy);
//                Point2D p2 = new Point2D(xx, yy);
//                double d = p1.distance(p2);
//                if (d <= r + rnd.nextDouble() * 0.6) {
//                    placeTileForSnapshot(pick(variants), xx * tileSize, yy * tileSize, zIndex, scale);
//                }
//            }
//        }
//    }

//    private void scatterDebris(int count) {
//        for (int i = 0; i < count; i++) {
//            int x = rnd.nextInt(tilesX) * tileSize;
//            int y = rnd.nextInt(tilesY) * tileSize;
//            placeSprite(pick(rockVariants), x, y, 2, 0.6 + rnd.nextDouble() * 0.8);
//        }
//    }
//
//    private void scatterBodies(int clusters) {
//        for (int c = 0; c < clusters; c++) {
//            int cx = rnd.nextInt(tilesX);
//            int cy = rnd.nextInt(tilesY);
//            int size = 1 + rnd.nextInt(5);
//            for (int i = 0; i < size; i++) {
//                int dx = cx + rnd.nextInt(5) - 2;
//                int dy = cy + rnd.nextInt(5) - 2;
//                if (dx < 0 || dy < 0 || dx >= tilesX || dy >= tilesY) continue;
//                placeSprite(pick(bodyVariants), dx * tileSize, dy * tileSize, 4, 0.8 + rnd.nextDouble() * 0.6);
//                // рядом цепляем кровь
//                if (rnd.nextDouble() < 0.7) {
//                    placeSprite(pick(bloodVariants), dx * tileSize + rnd.nextInt(tileSize)-tileSize/2,
//                            dy * tileSize + rnd.nextInt(tileSize)-tileSize/2, 5, 0.6 + rnd.nextDouble()*0.4);
//                }
//                // предметы
//                if (rnd.nextDouble() < 0.3) {
//                    placeSprite(pick(propVariants), dx * tileSize + rnd.nextInt(tileSize)-tileSize/2,
//                            dy * tileSize + rnd.nextInt(tileSize)-tileSize/2, 4, 0.7 + rnd.nextDouble()*0.6);
//                }
//            }
//        }
//    }
//
//    private void scatterFlags(int count) {
//        for (int i = 0; i < count; i++) {
//            int x = rnd.nextInt(tilesX) * tileSize;
//            int y = rnd.nextInt(tilesY) * tileSize;
//            placeSprite(pick(flagVariants), x, y - rnd.nextInt(tileSize/2), 6, 1.0);
//        }
//    }
//
//    private void scatterBlood(int count) {
//        for (int i = 0; i < count; i++) {
//            int x = rnd.nextInt(tilesX) * tileSize;
//            int y = rnd.nextInt(tilesY) * tileSize;
//            placeSprite(pick(bloodVariants), x, y, 5, 0.4 + rnd.nextDouble()*0.6);
//        }
//    }
//
//    private void generateFrontFog() {
//        // простой полупрозрачный туман слоями
//        for (int i = 0; i < 8; i++) {
//            double scale = 1.0 + rnd.nextDouble() * 2.0;
//            var iv = new ImageView(FXGL.texture("fx/fog-1.png").getImage());
//            iv.setFitWidth(tilesX * tileSize * scale);
//            iv.setPreserveRatio(true);
//            iv.setOpacity(0.08 + rnd.nextDouble()*0.12);
//            iv.setTranslateX(-rnd.nextDouble()*tileSize*10);
//            iv.setTranslateY(-rnd.nextDouble()*tileSize*10);
//            iv.setCache(true);
//            iv.setCacheHint(CacheHint.SPEED);
//            iv.setTranslateX(50 + i);
//            iv.setTranslateY(50); // если нужно сместить и по Y
//            FXGL.getGameScene().addUINode(iv);
//
//        }
//    }




//    private void placeSprite(String asset, int px, int py, int z, double scale) {
//        var tex = FXGL.texture(asset);
//        var iv = new ImageView(tex.getImage());
//        iv.setPreserveRatio(true);
//        iv.setScaleX(scale);
//        iv.setScaleY(scale);
//        iv.setCache(true);
//        iv.setCacheHint(CacheHint.SPEED);
//        FXGL.entityBuilder()
//                .at(px, py)
//                .view(iv)
//                .zIndex(z)
//                .buildAndAttach();
//    }



    //    public void generate() {
//        generateSkyLayer();
//        generateGroundBase();
//        generateMudPatches(0.005); // плотность
//        scatterDebris(tilesX * tilesY / 120); // мелкий мусор
//        scatterBodies(60); // количество кластеров тел
//        scatterFlags(10);
//        scatterBlood(tilesX * tilesY / 500);
//        generateFrontFog();
//        // После генерации можно сделать snapshot всех слоёв в одно изображение для оптимизации
//    }

//    private void generateSkyLayer() {
//        // Загружаем текстуру фона
//        var img = FXGL.texture("bg/hills-1.png");
//
//        // Создаём сущность в игровом мире с низким zIndex, чтобы быть под всеми объектами
//        var bgEntity = FXGL.entityBuilder()
//                .at(0, 0)
//                .view(img)
//                .zIndex(-100) // фон под всем остальным
//                .buildAndAttach();
//
//        // Настраиваем размеры и кеширование
//        ImageView iv = (ImageView) bgEntity.getViewComponent().getChildren().get(0);
//        iv.setFitWidth(tilesX * tileSize);
//        iv.setPreserveRatio(true);
//        iv.setCache(true);
//        iv.setCacheHint(CacheHint.SPEED);
//
//        // Убираем использование UI-слоя — больше не нужно
//        // FXGL.getGameScene().addUINode(iv);
//    }



//    private void generateGroundBase() {
//        generateGroundTiles();
//        generateTrees();
//    }

}
