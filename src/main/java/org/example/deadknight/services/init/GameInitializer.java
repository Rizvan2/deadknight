package org.example.deadknight.services.init;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.CacheHint;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.example.deadknight.maps.BattlefieldBackgroundGenerator;
import org.example.deadknight.player.entities.KnightEntity;
import org.example.deadknight.player.entities.IlyasPantherEntity;
import org.example.deadknight.mobs.entities.Spikes;
import org.example.deadknight.player.factories.KnightFactory;
import org.example.deadknight.player.factories.PantherFactory;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

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

        generateOrLoadBattlefield("bolshayashnaga42",42,42); // а тут циферки это типа размер карты друг на друга перемножаются и столько кубиков будет карта
        return character;
    }

    /**
     * Генерирует карту, если файл отсутствует, или загружает существующую.
     *
     * @param name   имя файла карты без расширения
     * @param tilesX количество тайлов по горизонтали
     * @param tilesY количество тайлов по вертикали
     */
    private static void generateOrLoadBattlefield(String name, int tilesX, int tilesY) {
        System.out.println("=== Работаем с картой: " + name + " (" + tilesX + "x" + tilesY + ") ===");

        File dir = new File("generated");
        createDirectoryIfNotExists(dir);

        File out = new File(dir, name + ".png");

        if (!out.exists()) {
            System.out.println("Файл не найден. Генерируем новую карту...");
            generateBattlefield(out, tilesX, tilesY);
        } else {
            System.out.println("Файл уже существует: " + out.getAbsolutePath());
        }

        loadBattlefieldImage(out, tilesX, tilesY, 20); // короче tileSize последний параметр метода это размер сжатия каждого кубика текстуры карты пон?
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
     * @param tilesX количество тайлов по горизонтали
     * @param tilesY количество тайлов по вертикали
     */
    private static void generateBattlefield(File out, int tilesX, int tilesY) {
        // Генерация карты
        BattlefieldBackgroundGenerator generator = new BattlefieldBackgroundGenerator(tilesX, tilesY, System.currentTimeMillis());
        generator.generateGrassOnly();

        // Снимок сцены
        WritableImage snapshot = FXGL.getGameScene().getRoot().snapshot(new SnapshotParameters(), null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", out);
            System.out.println("Файл создан: " + out.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить " + out.getName(), e);
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
