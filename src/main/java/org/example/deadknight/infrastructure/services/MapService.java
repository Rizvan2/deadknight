package org.example.deadknight.infrastructure.services;

import com.almasb.fxgl.dsl.FXGL;
import javafx.embed.swing.SwingFXUtils;
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

public class MapService {
    /**
     * Генерирует или загружает слои карты.
     * <p>
     * Если PNG-файлы пола или деревьев отсутствуют, они будут сгенерированы.
     * Затем слои подгружаются в игру с указанными Z-индексами.
     *
     * @param mapName имя карты
     * @param tilesX  количество тайлов по горизонтали
     * @param tilesY  количество тайлов по вертикали
     */
    public static void generateBattlefieldLayers(String mapName, int tilesX, int tilesY) {
        File dir = new File("generated");
        createDirectoryIfNotExists(dir);

        File groundFile = new File(dir, mapName + "_ground.png");
        File treesFile = new File(dir, mapName + "_trees.png");

        BattlefieldBackgroundGenerator generator = new BattlefieldBackgroundGenerator(tilesX, tilesY, System.currentTimeMillis());

// Генерация пола
        createLayerIfMissing(groundFile, generator::generateGroundTiles, "пол");

// Генерация деревьев
        createLayerIfMissing(treesFile, generator::generateTreesTiles, "деревья");


// ===== Подгрузка слоев в игру =====
        loadLayerImage(groundFile, tilesX, tilesY, -100); // пол
        loadLayerImage(treesFile, tilesX, tilesY, 1000);  // деревья сверху
    }

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
     * Генерирует слой пола карты, если он ещё не существует.
     *
     * @param generator экземпляр {@link BattlefieldBackgroundGenerator}
     * @param groundFile файл PNG для пола
     */
    private static void generateGroundLayer(BattlefieldBackgroundGenerator generator, File groundFile) {
        if (!groundFile.exists()) {
            Pane tempPane = new Pane();
            generator.generateGroundTiles(tempPane); // передаём Pane для отрисовки
            WritableImage snapshot = tempPane.snapshot(new SnapshotParameters(), null); // snapshot с Pane
            saveImage(snapshot, groundFile);
            System.out.println("Слой пола создан: " + groundFile.getAbsolutePath());
        } else {
            System.out.println("Слой пола уже существует: " + groundFile.getAbsolutePath());
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
