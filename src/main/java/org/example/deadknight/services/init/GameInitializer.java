package org.example.deadknight.services.init;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.deadknight.gameplay.actors.player.entities.KnightEntity;
import org.example.deadknight.gameplay.actors.player.entities.IlyasPantherEntity;
import org.example.deadknight.gameplay.actors.mobs.entities.Spikes;
import org.example.deadknight.gameplay.actors.player.factories.KnightFactory;
import org.example.deadknight.gameplay.actors.player.factories.PantherFactory;
import org.example.deadknight.infrastructure.dto.GameWorldData;
import org.example.deadknight.infrastructure.generation.BattlefieldBackgroundGenerator;
import org.example.deadknight.infrastructure.services.MapChunkService;
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

    public static GameWorldData createGameWorld(String characterType) {

        // 1. Генерация карты
        int mapTilesX = 32;
        int mapTilesY = 32;
        BattlefieldBackgroundGenerator generator = new BattlefieldBackgroundGenerator(mapTilesX, mapTilesY, 12345);
        var groundTiles = generator.getGroundTileArray();
        var treeTiles = generator.getTreeTileArray();

        // 2. Создание MapChunkService
        MapChunkService mapChunkService = new MapChunkService(
                groundTiles,
                treeTiles,
                8,
                8
        );

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

        // 5. Прогрузка стартовых чанков вокруг игрока
        mapChunkService.updateVisibleChunks(startX, startY);

        // --- Временная проверка отображения тайла ---
        Image testTile = new Image("map/stone/stone-1.png"); // путь к вашей картинке тайла
        FXGL.entityBuilder()
                .at(100, 100) // координаты на сцене
                .view(new ImageView(testTile))
                .zIndex(-100)
                .buildAndAttach();
        return new GameWorldData(player, mapChunkService,
                mapTilesX * BattlefieldBackgroundGenerator.tileSize,
                mapTilesY * BattlefieldBackgroundGenerator.tileSize);
    }

}
