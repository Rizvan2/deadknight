package org.example.deadknight.services.init;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import org.example.deadknight.gameplay.actors.player.entities.KnightEntity;
import org.example.deadknight.gameplay.actors.player.entities.IlyasPantherEntity;
import org.example.deadknight.gameplay.actors.mobs.entities.Spikes;
import org.example.deadknight.gameplay.actors.player.factories.KnightFactory;
import org.example.deadknight.gameplay.actors.player.factories.PantherFactory;

/**
 * Класс инициализации игрового мира.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Создание игрового персонажа (рыцарь или пантера)</li>
 *     <li>Расстановку препятствий (шипы)</li>
 *     <li>Генерацию и загрузку карты (пол и деревья)</li>
 * </ul>
 * <p>
 * Генерация карты создаёт PNG-файлы в папке <code>generated</code>, если они ещё не существуют.
 * Затем PNG загружается в игру как фоновые слои.
 */
public class GameInitializer {

    /**
     * Инициализирует игрового персонажа и игровой мир.
     *
     * @param characterType тип персонажа: "knight" или "panther"
     * @return созданная сущность персонажа {@link Entity}
     * @throws IllegalArgumentException если передан неизвестный тип персонажа
     */
    public static Entity initGame(String characterType) {
        Point2D mapSize = MapInitializer.generateMediumWorld("средний4");

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

// Привязываем камеру к персонажу, чтобы он был в центре экрана
        FXGL.getGameScene().getViewport().bindToEntity(
                character,
                FXGL.getAppWidth() / 2.0,
                FXGL.getAppHeight() / 2.0
        );

        int mapWidth = (int) mapSize.getX();
        int mapHeight = (int) mapSize.getY();
// Ограничиваем камеру границами карты (если карта 15x10 тайлов, tileSize = 64)
        FXGL.getGameScene().getViewport().setBounds(0, 0, mapWidth, mapHeight);

// Добавляем препятствия
        FXGL.getGameWorld().addEntity(Spikes.create(200, 300));
        FXGL.getGameWorld().addEntity(Spikes.create(400, 300));

        return character;
    }
}
