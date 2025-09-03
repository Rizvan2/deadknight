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
 * Класс для инициализации игрового мира и персонажей.
 * <p>
 * Основные функции:
 * <ul>
 *     <li>Создание игрового персонажа (рыцарь или пантера)</li>
 *     <li>Привязка камеры к персонажу</li>
 *     <li>Генерация карты</li>
 *     <li>Добавление препятствий на карту</li>
 * </ul>
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
        Point2D mapSize = generateMap();

        Entity character = createCharacter(characterType);

        addCharacterToWorld(character);
        bindCameraToCharacter(character, mapSize);
        addObstacles();

        return character;
    }

    /**
     * Генерирует карту среднего размера.
     *
     * @return объект {@link Point2D} с размерами карты (ширина, высота)
     */
    private static Point2D generateMap() {
        return MapInitializer.generateMediumWorld("средний4");
    }

    /**
     * Создаёт сущность персонажа в зависимости от типа.
     *
     * @param characterType "knight" или "panther"
     * @return объект {@link Entity} персонажа
     * @throws IllegalArgumentException если передан неизвестный тип персонажа
     */
    private static Entity createCharacter(String characterType) {
        return switch (characterType) {
            case "knight" -> {
                KnightEntity knightData = new KnightEntity(100, 0.6, "RIGHT");
                yield KnightFactory.create(knightData, 100, 300);
            }
            case "panther" -> {
                IlyasPantherEntity pantherData = new IlyasPantherEntity(120, 60, "RIGHT");
                yield PantherFactory.create(pantherData, 100, 300);
            }
            default -> throw new IllegalArgumentException("Неизвестный тип персонажа: " + characterType);
        };
    }

    /**
     * Добавляет персонажа в игровой мир.
     *
     * @param character сущность персонажа {@link Entity}
     */
    private static void addCharacterToWorld(Entity character) {
        FXGL.getGameWorld().addEntity(character);
    }

    /**
     * Привязывает камеру к персонажу и задаёт границы карты.
     *
     * @param character сущность персонажа {@link Entity}
     * @param mapSize   размеры карты {@link Point2D}
     */
    private static void bindCameraToCharacter(Entity character, Point2D mapSize) {
        FXGL.getGameScene().getViewport().bindToEntity(
                character,
                FXGL.getAppWidth() / 2.0,
                FXGL.getAppHeight() / 2.0
        );

        int mapWidth = (int) mapSize.getX();
        int mapHeight = (int) mapSize.getY();

        FXGL.getGameScene().getViewport().setBounds(0, 0, mapWidth, mapHeight);
    }

    /**
     * Добавляет препятствия (шипы) на карту.
     */
    private static void addObstacles() {
        FXGL.getGameWorld().addEntity(Spikes.create(200, 300));
        FXGL.getGameWorld().addEntity(Spikes.create(400, 300));
    }
}
