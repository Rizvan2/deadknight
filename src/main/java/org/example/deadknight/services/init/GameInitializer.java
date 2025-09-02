package org.example.deadknight.services.init;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.CacheHint;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.example.deadknight.exceptions.MapSaveException;
import org.example.deadknight.maps.BattlefieldBackgroundGenerator;
import org.example.deadknight.player.entities.KnightEntity;
import org.example.deadknight.player.entities.IlyasPantherEntity;
import org.example.deadknight.mobs.entities.Spikes;
import org.example.deadknight.player.factories.KnightFactory;
import org.example.deadknight.player.factories.PantherFactory;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static org.example.deadknight.maps.BattlefieldBackgroundGenerator.tileSize;

/**
 * Класс инициализации игры.
 * <p>
 * Отвечает за создание игрового персонажа, расстановку препятствий, установку фона сцены
 * и спавн врагов.
 */
public class GameInitializer {

    /**
     * Инициализирует игрового персонажа и базовый игровой мир.
     * <p>
     * В зависимости от выбранного типа персонажа создается соответствующая сущность
     * {@link KnightEntity} или {@link IlyasPantherEntity}, добавляются препятствия
     * {@link Spikes} и устанавливается фон карты.
     *
     * @param characterType тип персонажа: "knight" или "panther"
     * @return созданная сущность персонажа {@link Entity}
     * @throws IllegalArgumentException если передан неизвестный тип персонажа
     */
    public static Entity initGame(String characterType) {

        // ===== Генерация или загрузка карты =====
        generateBattlefieldLayers("bolshayashnaga42", 10, 10);

        Entity character;

        switch (characterType) {
            case "knight":
                KnightEntity knightData = new KnightEntity(100, 0.6, "RIGHT");
                character = KnightFactory.create(knightData, 100, 300);
                break;

            case "panther":
                IlyasPantherEntity pantherData = new IlyasPantherEntity(120, 60, "RIGHT");
                character = PantherFactory.create(pantherData, 100, 300);
                break;

            default:
                throw new IllegalArgumentException("Неизвестный тип персонажа: " + characterType);
        }

        // Добавляем персонажа в мир
        FXGL.getGameWorld().addEntity(character);

        // Добавляем препятствия
        FXGL.getGameWorld().addEntity(Spikes.create(200, 300));
        FXGL.getGameWorld().addEntity(Spikes.create(400, 300));

        return character;
    }

    private static void generateBattlefieldLayers(String mapName, int tilesX, int tilesY) {
        File dir = new File("generated");
        createDirectoryIfNotExists(dir);

        File groundFile = new File(dir, mapName + "_ground.png");
        File treesFile = new File(dir, mapName + "_trees.png");

        BattlefieldBackgroundGenerator generator = new BattlefieldBackgroundGenerator(tilesX, tilesY, System.currentTimeMillis());

        generateGroundLayer(generator, groundFile);

// ===== Генерация деревьев =====
        if (!treesFile.exists()) {
            FXGL.getGameScene().clearGameViews(); // очищаем сцену от пола перед деревьями
            Pane tempRoot = new Pane();
            generator.generateTreesTiles(tempRoot);

            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            WritableImage treesSnapshot = tempRoot.snapshot(params, null); // <- вот здесь tempRoot
            saveImage(treesSnapshot, treesFile);
            System.out.println("Слой деревьев создан: " + treesFile.getAbsolutePath());

        } else {
            System.out.println("Слой деревьев найден, загружаем: " + treesFile.getAbsolutePath());
        }

// ===== Подгрузка слоев в игру =====
        loadLayerImage(groundFile, tilesX, tilesY, -100); // пол
        loadLayerImage(treesFile, tilesX, tilesY, 1000);  // деревья сверху
    }

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



    private static void saveImage(WritableImage image, File file) {
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            System.out.println("Файл создан: " + file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить изображение: " + file.getName(), e);
        }
    }


    /**
     * Создает папку, если она еще не существует.
     *
     * @param dir объект папки
     */
    private static void createDirectoryIfNotExists(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("Создана папка: " + dir.getAbsolutePath());
        }
    }

    /**
     * Генерирует игровое поле и сохраняет его в PNG.
     *
     * @param out    файл, куда будет сохранена карта
     * @param tilesX количество тайлов по горизонтали (ширина карты в тайлах)
     * @param tilesY количество тайлов по вертикали (высота карты в тайлах)
     *
     * Тайл (tile) — это просто маленький квадратный кусочек карты, из которых строится весь фон или уровень игры.
     */
    private static void generateBattlefield(File out, int tilesX, int tilesY) {
        // Генерация карты
        BattlefieldBackgroundGenerator generator = new BattlefieldBackgroundGenerator(tilesX, tilesY, System.currentTimeMillis());

        // Снимок сцены
        WritableImage snapshot = FXGL.getGameScene().getRoot().snapshot(new SnapshotParameters(), null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", out);
            System.out.println("Файл создан: " + out.getAbsolutePath());
        } catch (IOException e) {
            throw new MapSaveException("Не удалось сохранить карту ¯\\_(ツ)_/¯ :" + out.getName(), e);
        }
    }

    /**
     * Загружает карту и отображает ее в игровом мире с учетом размера тайла.
     *
     * @param out      файл PNG карты
     * @param tilesX   количество тайлов по горизонтали
     * @param tilesY   количество тайлов по вертикали
     * @param tileSize размер тайла в пикселях
     */
    private static void loadBattlefieldImage(File out, int tilesX, int tilesY, int tileSize) {
        Image bgImage = new Image(out.toURI().toString());
        ImageView iv = new ImageView(bgImage);
        iv.setFitWidth(tilesX * tileSize);
        iv.setFitHeight(tilesY * tileSize);
        iv.setCache(true);
        iv.setCacheHint(CacheHint.SPEED);

        FXGL.entityBuilder()
                .at(0, 0)
                .view(iv)
                .zIndex(-100)
                .buildAndAttach();
    }
}
