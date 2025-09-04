package org.example.deadknight.services.init;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import org.example.deadknight.gameplay.actors.player.entities.KnightEntity;
import org.example.deadknight.gameplay.actors.player.entities.IlyasPantherEntity;
import org.example.deadknight.gameplay.actors.mobs.entities.Spikes;
import org.example.deadknight.gameplay.actors.player.factories.KnightFactory;
import org.example.deadknight.gameplay.actors.player.factories.PantherFactory;
import org.example.deadknight.services.GameInitializerService;

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
    public static Entity createGameWorld(String characterType) {
        Point2D mapSize = generateMap();
        Point2D spawnPoint = getCenterOfMap(mapSize); // центр карты

        Entity character = createCharacterAt(characterType, spawnPoint);
        addCharacterToWorld(character);
        bindCameraToCharacter(character, mapSize);
        addObstacles();

        // Спавним врагов с краёв карты
        GameInitializerService.spawnEnemiesFromAllSidesWithDelay(10, mapSize); // 100 мобов с каждой стороны


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
     * Создаёт сущность персонажа в зависимости от типа и позиции.
     *
     * @param characterType "knight" или "panther"
     * @param spawnPoint координаты спавна персонажа {@link Point2D}
     * @return объект {@link Entity} персонажа
     * @throws IllegalArgumentException если передан неизвестный тип персонажа
     */
    private static Entity createCharacterAt(String characterType, Point2D spawnPoint) {
        double x = spawnPoint.getX();
        double y = spawnPoint.getY();

        return switch (characterType) {
            case "knight" -> {
                KnightEntity knightData = new KnightEntity(100, 0.6, "RIGHT");
                yield KnightFactory.create(knightData, x, y);
            }
            case "panther" -> {
                IlyasPantherEntity pantherData = new IlyasPantherEntity(120, 60, "RIGHT");
                yield PantherFactory.create(pantherData, x, y);
            }
            default -> throw new IllegalArgumentException("Неизвестный тип персонажа: " + characterType);
        };
    }

    /**
     * Вычисляет координаты центра карты.
     *
     * @param mapSize размеры карты в пикселях
     * @return координаты центра карты {@link Point2D}
     */
    private static Point2D getCenterOfMap(Point2D mapSize) {
        double centerX = mapSize.getX() / 2.0;
        double centerY = mapSize.getY() / 2.0;
        return new Point2D(centerX, centerY);
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
