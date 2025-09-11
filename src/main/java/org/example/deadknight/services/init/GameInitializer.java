package org.example.deadknight.services.init;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
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

    public static GameWorldData createGameWorld(String characterType) {

        // 1. Генерация карты
        int mapTilesX = 1280;
        int mapTilesY = 1280;
        BattlefieldBackgroundGenerator generator = new BattlefieldBackgroundGenerator(mapTilesX, mapTilesY, 12345);
        Image[][] groundTiles;
        groundTiles = generator.getGroundTileArray();

        // 2. Создание MapChunkService
// 2. Создание MapChunkService
        MapChunkService mapChunkService = new MapChunkService(groundTiles);


        // 3. Стартовая позиция игрока — центр карты
        double startX = mapTilesX * BattlefieldBackgroundGenerator.tileSize / 2.0;
        double startY = mapTilesY * BattlefieldBackgroundGenerator.tileSize / 2.0;

// 4. Создаём игрока
        Entity player = switch (characterType) {
            case "knight" -> KnightFactory.create(new KnightEntity(100, 0.6, "RIGHT"), startX, startY);
            case "panther" -> PantherFactory.create(new IlyasPantherEntity(120, 60, "RIGHT"), startX, startY);
            default -> throw new IllegalArgumentException("Неизвестный тип персонажа: " + characterType);
        };

// Добавляем игрока в мир
        FXGL.getGameWorld().addEntity(player);

        return new GameWorldData(player, mapChunkService,
                mapTilesX * BattlefieldBackgroundGenerator.tileSize,
                mapTilesY * BattlefieldBackgroundGenerator.tileSize);
    }

}
