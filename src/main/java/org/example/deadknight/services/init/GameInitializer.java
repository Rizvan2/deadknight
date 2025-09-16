package org.example.deadknight.services.init;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import org.example.deadknight.gameplay.actors.player.entities.KnightEntity;
import org.example.deadknight.gameplay.actors.player.entities.IlyasPantherEntity;
import org.example.deadknight.gameplay.actors.player.factories.KnightFactory;
import org.example.deadknight.gameplay.actors.player.factories.PantherFactory;
import org.example.deadknight.infrastructure.dto.GameWorldData;
import org.example.deadknight.infrastructure.generation.BattlefieldBackgroundGenerator;
import org.example.deadknight.infrastructure.render.services.MapChunkService;

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
     * Главный метод создания игрового мира.
     *
     * @param characterType тип персонажа ("knight", "panther")
     * @return данные игрового мира {@link GameWorldData}
     */
    public static GameWorldData createGameWorld(String characterType) {
        Image[][] groundTiles = generateMapTiles(128, 128, 12345);
        MapChunkService mapChunkService = createMapChunkService(groundTiles);
        Point2D startPosition = calculateStartPosition(128, 128);
        Entity player = createPlayer(characterType, startPosition);


        return new GameWorldData(
                player,
                mapChunkService,
                128 * BattlefieldBackgroundGenerator.tileSize,
                128 * BattlefieldBackgroundGenerator.tileSize
        );
    }

    /**
     * Генерация тайлов карты.
     *
     * @param width  ширина карты в тайлах
     * @param height высота карты в тайлах
     * @param seed   сид для генерации
     * @return массив тайлов {@link Image[][]}
     */
    private static Image[][] generateMapTiles(int width, int height, long seed) {
        BattlefieldBackgroundGenerator generator = new BattlefieldBackgroundGenerator(width, height, seed);
        return generator.getGroundTileArray();
    }

    /**
     * Создаёт сервис работы с чанками карты.
     *
     * @param groundTiles массив тайлов карты
     * @return {@link MapChunkService}
     */
    private static MapChunkService createMapChunkService(Image[][] groundTiles) {
        return new MapChunkService(groundTiles);
    }

    /**
     * Вычисляет стартовую позицию игрока — центр карты.
     *
     * @param mapTilesX ширина карты в тайлах
     * @param mapTilesY высота карты в тайлах
     * @return позиция игрока {@link Point2D}
     */
    private static Point2D calculateStartPosition(int mapTilesX, int mapTilesY) {
        double x = mapTilesX * BattlefieldBackgroundGenerator.tileSize / 2.0;
        double y = mapTilesY * BattlefieldBackgroundGenerator.tileSize / 2.0;
        return new Point2D(x, y);
    }

    /**
     * Создаёт сущность игрока нужного типа на заданной позиции.
     *
     * @param characterType тип персонажа
     * @param startPosition стартовая позиция
     * @return сущность игрока {@link Entity}
     */
    private static Entity createPlayer(String characterType, Point2D startPosition) {
        return switch (characterType) {
            case "knight" -> KnightFactory.create(new KnightEntity(100, 0.6, "RIGHT"),
                    startPosition.getX(), startPosition.getY());
            case "panther" -> PantherFactory.create(new IlyasPantherEntity(120, 60, "RIGHT"),
                    startPosition.getX(), startPosition.getY());
            default -> throw new IllegalArgumentException("Неизвестный тип персонажа: " + characterType);
        };
    }
}

