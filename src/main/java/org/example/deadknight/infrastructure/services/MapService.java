package org.example.deadknight.infrastructure.services;

import com.almasb.fxgl.dsl.FXGL;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.example.deadknight.infrastructure.generation.BattlefieldBackgroundGenerator;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import static org.example.deadknight.infrastructure.generation.BattlefieldBackgroundGenerator.tileSize;

/**
 * Сервис для генерации, загрузки и отображения слоёв карты.
 * <p>
 * Основные возможности:
 * <ul>
 *     <li>Генерация PNG-файлов слоёв карты (пол, деревья), если они отсутствуют</li>
 *     <li>Подгрузка слоёв в игру и создание соответствующих сущностей</li>
 *     <li>Управление Z-индексами слоёв для корректного рендеринга</li>
 *     <li>Создание необходимых директорий для хранения сгенерированных слоёв</li>
 * </ul>
 * <p>
 * Используется вместе с {@link BattlefieldBackgroundGenerator} для генерации тайлов.
 */
public class MapService {

    /**
     * Генерирует или загружает слои карты (пол и деревья).
     * <p>
     * Если PNG-файлы отсутствуют, они будут сгенерированы. После этого слои подгружаются в игру.
     *
     * @param mapName имя карты
     * @param tilesX  количество тайлов по горизонтали
     * @param tilesY  количество тайлов по вертикали
     * @return размеры карты в пикселях {@link Point2D}
     */
    public static Point2D generateBattlefieldLayers(String mapName, int tilesX, int tilesY) {
        createGeneratedDirectory();

        File groundFile = new File("generated", mapName + "_ground.png");
        File treesFile = new File("generated", mapName + "_trees.png");

        BattlefieldBackgroundGenerator generator = new BattlefieldBackgroundGenerator(tilesX, tilesY, System.currentTimeMillis());

        generateGroundLayerIfMissing(groundFile, generator);
//        generateTreesLayerIfMissing(treesFile, generator);

        loadLayerImage(groundFile, tilesX, tilesY, -100);
        loadLayerImage(treesFile, tilesX, tilesY, 1000);

        return new Point2D(tilesX * tileSize, tilesY * tileSize);
    }

    /**
     * Создаёт папку <code>generated</code>, если она отсутствует.
     */
    private static void createGeneratedDirectory() {
        File dir = new File("generated");
        createDirectoryIfNotExists(dir);
    }

    /**
     * Генерирует слой пола, если PNG-файл отсутствует.
     *
     * @param groundFile файл слоя пола
     * @param generator  генератор слоев {@link BattlefieldBackgroundGenerator}
     */
    private static void generateGroundLayerIfMissing(File groundFile, BattlefieldBackgroundGenerator generator) {
        createLayerIfMissing(groundFile, generator::generateGroundTiles, "пол");
    }

//    /**
//     * Генерирует слой деревьев, если PNG-файл отсутствует.
//     *
//     * @param treesFile файл слоя деревьев
//     * @param generator генератор слоев {@link BattlefieldBackgroundGenerator}
//     */
//    private static void generateTreesLayerIfMissing(File treesFile, BattlefieldBackgroundGenerator generator) {
//        createLayerIfMissing(treesFile, generator::generateTreesTiles, "деревья");
//    }
//

    /**
     * Создаёт PNG-файл слоя, если он отсутствует.
     *
     * @param outputFile   PNG-файл слоя
     * @param generatorFn  функция, которая рисует слой на переданном {@link Pane}
     * @param layerName    имя слоя для логов
     */
    private static void createLayerIfMissing(File outputFile,
                                             Consumer<Pane> generatorFn,
                                             String layerName) {
        if (!outputFile.exists()) {
            Pane tempRoot = new Pane();
            generatorFn.accept(tempRoot); // рисуем слой прямо здесь

            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);

            WritableImage snapshot = tempRoot.snapshot(params, null);
            saveImage(snapshot, outputFile);

            System.out.println("Слой " + layerName + " создан: " + outputFile.getAbsolutePath());
        } else {
            System.out.println("Слой " + layerName + " найден, загружаем: " + outputFile.getAbsolutePath());
        }
    }

    /**
     * Создаёт сущность с изображением слоя карты и добавляет её в мир.
     *
     * @param file   PNG-файл слоя
     * @param tilesX количество тайлов по горизонтали
     * @param tilesY количество тайлов по вертикали
     * @param zIndex Z-индекс слоя
     */
    private static void loadLayerImage(File file, int tilesX, int tilesY, int zIndex) {
        Image image = new Image(file.toURI().toString());
        ImageView iv = new ImageView(image);
        iv.setFitWidth(tilesX * tileSize);
        iv.setFitHeight(tilesY * tileSize);
        iv.setSmooth(true);
        iv.setCache(false);

        FXGL.entityBuilder()
                .at(0, 0)
                .view(iv)
                .zIndex(zIndex)
                .buildAndAttach();
    }

    /**
     * Сохраняет WritableImage в PNG-файл.
     *
     * @param image изображение для сохранения
     * @param file  файл, куда будет сохранено изображение
     * @throws RuntimeException если сохранение не удалось
     */
    private static void saveImage(WritableImage image, File file) {
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            System.out.println("Файл создан: " + file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить изображение: " + file.getName(), e);
        }
    }


    /**
     * Создаёт папку, если она ещё не существует.
     *
     * @param dir объект папки
     */
    private static void createDirectoryIfNotExists(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("Создана папка: " + dir.getAbsolutePath());
        }
    }
}
