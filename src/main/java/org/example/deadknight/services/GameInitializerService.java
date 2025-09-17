package org.example.deadknight.services;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.example.deadknight.gameplay.actors.player.entities.KnightEntity;
import org.example.deadknight.gameplay.actors.player.entities.IlyasPantherEntity;
import org.example.deadknight.gameplay.actors.player.factories.KnightFactory;
import org.example.deadknight.gameplay.actors.player.factories.PantherFactory;
import org.example.deadknight.gameplay.services.LootService;
import org.example.deadknight.infrastructure.dto.GameWorldData;
import org.example.deadknight.infrastructure.generation.BattlefieldBackgroundGenerator;
import org.example.deadknight.infrastructure.render.services.MapChunkService;

import java.util.Random;

@Getter
@Setter
public class GameInitializerService {

    private static final int MAP_WIDTH_TILES = 128;
    private static final int MAP_HEIGHT_TILES = 128;

    private final LootService lootService;
    private boolean enemiesSpawned = false; // ⚡ флаг первого спавна

    /** Конструктор сервиса */
    public GameInitializerService() {
        this.lootService = new LootService();
    }

    /**
     * Создаёт весь игровой мир: карту и игрока.
     * Спавн врагов теперь делается только один раз через enemiesSpawned.
     */
    public GameWorldData initGameWorld(String characterType) {

        // 1. Генерация тайлов карты
        BattlefieldBackgroundGenerator generator = new BattlefieldBackgroundGenerator(
                MAP_WIDTH_TILES, MAP_HEIGHT_TILES, new Random().nextLong()
        );
        MapChunkService mapChunkService = new MapChunkService(generator.getGroundTileArray());

        // 2. Стартовая позиция игрока — центр карты
        double startX = MAP_WIDTH_TILES * BattlefieldBackgroundGenerator.tileSize / 2.0;
        double startY = MAP_HEIGHT_TILES * BattlefieldBackgroundGenerator.tileSize / 2.0;

        Entity player = switch (characterType) {
            case "knight" -> KnightFactory.create(new KnightEntity(100, 0.6, "RIGHT"), startX, startY);
            case "panther" -> PantherFactory.create(new IlyasPantherEntity(120, 60, "RIGHT"), startX, startY);
            default -> throw new IllegalArgumentException("Неизвестный тип персонажа: " + characterType);
        };

        FXGL.getGameWorld().addEntity(player);

        // ⚡ Спавним врагов только один раз
        if (!enemiesSpawned) {
            spawnEnemiesAroundPlayer(player, 100, 0.5);
            enemiesSpawned = true;
        }

        return new GameWorldData(
                player,
                mapChunkService,
                MAP_WIDTH_TILES * BattlefieldBackgroundGenerator.tileSize,
                MAP_HEIGHT_TILES * BattlefieldBackgroundGenerator.tileSize
        );
    }

    /**
     * Динамический спавн врагов с задержкой
     */
    public void spawnEnemiesAroundPlayer(Entity player, int count, double delayPerSpawnSeconds) {
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            int spawnIndex = i;

            FXGL.runOnce(() -> {
                double playerX = player.getX();
                double playerY = player.getY();

                double radiusX = 1500;
                double radiusY = 1000;

                double angle = random.nextDouble() * 2 * Math.PI;
                double offsetX = Math.cos(angle) * radiusX;
                double offsetY = Math.sin(angle) * radiusY;

                double spawnX = playerX + offsetX;
                double spawnY = playerY + offsetY;

                FXGL.spawn("goblin", new SpawnData(spawnX, spawnY));

                System.out.println("[DeadKnight] Гоблин заспавнен рядом с игроком: (" + spawnX + ", " + spawnY + ")");
            }, Duration.seconds(delayPerSpawnSeconds * spawnIndex));
        }
    }
}
