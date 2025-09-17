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

/**
 * Сервис для инициализации игрового мира.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Генерацию карты и её чанков</li>
 *     <li>Создание игрока выбранного типа и размещение его в стартовой позиции</li>
 *     <li>Спавн врагов вокруг игрока (один раз за запуск)</li>
 * </ul>
 */
@Getter
@Setter
public class GameInitializerService {

    /** Ширина карты в тайлах */
    private static final int MAP_WIDTH_TILES = 128;

    /** Высота карты в тайлах */
    private static final int MAP_HEIGHT_TILES = 128;

    /** Сервис для управления добычей (лутом) */
    private final LootService lootService;

    /** Флаг первого спавна врагов, чтобы не дублировать их */
    private boolean enemiesSpawned = false;

    /** Конструктор сервиса */
    public GameInitializerService() {
        this.lootService = new LootService();
    }

    /**
     * Инициализирует весь игровой мир: создаёт карту, игрока и врагов.
     *
     * @param characterType тип персонажа ("knight" или "panther")
     * @return {@link GameWorldData} с игроком, картой и размерами мира
     */
    public GameWorldData initGameWorld(String characterType) {

        MapChunkService mapChunkService = generateMap();
        Entity player = spawnPlayer(characterType, mapChunkService);
        spawnEnemiesOnce(player);

        return new GameWorldData(
                player,
                mapChunkService,
                MAP_WIDTH_TILES * BattlefieldBackgroundGenerator.tileSize,
                MAP_HEIGHT_TILES * BattlefieldBackgroundGenerator.tileSize
        );
    }

    /**
     * Генерирует карту и возвращает сервис для управления чанками.
     *
     * @return {@link MapChunkService} для работы с чанками карты
     */
    private MapChunkService generateMap() {
        BattlefieldBackgroundGenerator generator = new BattlefieldBackgroundGenerator(
                MAP_WIDTH_TILES, MAP_HEIGHT_TILES, new Random().nextLong()
        );
        return new MapChunkService(generator.getGroundTileArray());
    }

    /**
     * Создаёт игрока выбранного типа и размещает его в центре карты.
     *
     * @param characterType тип персонажа ("knight" или "panther")
     * @param mapChunkService сервис карты для возможного взаимодействия
     * @return созданная сущность игрока
     */
    private Entity spawnPlayer(String characterType, MapChunkService mapChunkService) {
        double startX = MAP_WIDTH_TILES * BattlefieldBackgroundGenerator.tileSize / 2.0;
        double startY = MAP_HEIGHT_TILES * BattlefieldBackgroundGenerator.tileSize / 2.0;

        Entity player = switch (characterType) {
            case "knight" -> KnightFactory.create(new KnightEntity(100, 0.6, "RIGHT"), startX, startY);
            case "panther" -> PantherFactory.create(new IlyasPantherEntity(120, 60, "RIGHT"), startX, startY);
            default -> throw new IllegalArgumentException("Неизвестный тип персонажа: " + characterType);
        };

        FXGL.getGameWorld().addEntity(player);
        return player;
    }

    /**
     * Спавнит врагов вокруг игрока, но только один раз.
     *
     * @param player сущность игрока, вокруг которого спавнятся враги
     */
    private void spawnEnemiesOnce(Entity player) {
        if (!enemiesSpawned) {
            spawnEnemiesAroundPlayer(player, 100, 0.5);
            enemiesSpawned = true;
        }
    }

    /**
     * Динамический спавн врагов с задержкой.
     *
     * @param player сущность игрока, рядом с которой спавнятся враги
     * @param count количество врагов для спавна
     * @param delayPerSpawnSeconds задержка между спавном каждого врага (в секундах)
     */
    public void spawnEnemiesAroundPlayer(Entity player, int count, double delayPerSpawnSeconds) {
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            scheduleEnemySpawn(player, random, i, delayPerSpawnSeconds);
        }
    }

    /**
     * Запланировать спавн одного врага с задержкой.
     *
     * @param player сущность игрока
     * @param random объект для генерации случайного положения
     * @param spawnIndex индекс врага (для вычисления задержки)
     * @param delayPerSpawnSeconds задержка между спавнами
     */
    private void scheduleEnemySpawn(Entity player, Random random, int spawnIndex, double delayPerSpawnSeconds) {
        FXGL.runOnce(() -> spawnEnemy(player, random), Duration.seconds(delayPerSpawnSeconds * spawnIndex));
    }

    /**
     * Создаёт и размещает врага рядом с игроком.
     *
     * @param player сущность игрока
     * @param random объект для генерации случайного положения
     */
    private void spawnEnemy(Entity player, Random random) {
        double radiusX = 1500;
        double radiusY = 1000;

        double angle = random.nextDouble() * 2 * Math.PI;
        double offsetX = Math.cos(angle) * radiusX;
        double offsetY = Math.sin(angle) * radiusY;

        double spawnX = player.getX() + offsetX;
        double spawnY = player.getY() + offsetY;

        FXGL.spawn("goblin", new SpawnData(spawnX, spawnY));

        System.out.println("[DeadKnight] Гоблин заспавнен рядом с игроком: (" + spawnX + ", " + spawnY + ")");
    }
}
