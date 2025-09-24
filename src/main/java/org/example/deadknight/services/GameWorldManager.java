package org.example.deadknight.services;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import lombok.Getter;
import lombok.Setter;
import org.example.deadknight.gameplay.actors.player.services.*;
import org.example.deadknight.gameplay.actors.player.services.ui.UIService;
import org.example.deadknight.gameplay.actors.player.systems.CollisionSystem;
import org.example.deadknight.infrastructure.dto.GameWorldData;
import org.example.deadknight.infrastructure.factory.GameWorldFactory;
import org.example.deadknight.infrastructure.render.services.MapChunkService;

/**
 * Менеджер игрового мира.
 * <p>
 * Основные задачи:
 * <ul>
 *     <li>Инициализация игрового мира (карта, игрок, враги)</li>
 *     <li>Создание игрока через {@link PlayerService} и управление его логикой</li>
 *     <li>Управление пользовательским интерфейсом через {@link UIService}</li>
 *     <li>Обработка коллизий и обновление карты чанков</li>
 *     <li>Перезапуск игры после смерти игрока</li>
 * </ul>
 * <p>
 * Использует {@link GameWorldFactory} для создания всех игровых объектов и соблюдения принципа SRP.
 */
@Getter
@Setter
public class GameWorldManager {

    /** Сервис для инициализации игровых данных (карта, игроки, враги) */
    private final GameInitializerService initializer;

    /** Сервис для управления UI: полоска здоровья, GameOver, апгрейды */
    private final UIService uiService;

    /** Текущий выбранный тип персонажа ("knight", "panther" и т.д.) */
    private String currentCharacterType;

    /** Сущность игрока */
    private Entity player;

    /** Сервис игрока: движение, апгрейды */
    private PlayerService playerService;

    /** Сервис для управления отображаемыми чанками карты */
    private MapChunkService mapChunkService;

    /** Система обработки коллизий */
    private CollisionSystem collisionSystem;

    /**
     * Конструктор менеджера игрового мира.
     *
     * @param initializer сервис для инициализации игрового мира
     * @param uiService   сервис для управления UI
     */
    public GameWorldManager(GameInitializerService initializer, UIService uiService) {
        this.initializer = initializer;
        this.uiService = uiService;
    }

    /**
     * Запускает игру с указанным типом персонажа.
     * <p>
     * Метод выполняет полный рестарт игрового мира для выбранного персонажа:
     * <ol>
     *     <li>Очищает текущую сцену (игрок, сущности, UI, ввод).</li>
     *     <li>Создает новый игровой мир через {@link GameInitializerService}.</li>
     *     <li>Инициализирует игрока, PlayerService, карту и систему коллизий через {@link GameWorldFactory}.</li>
     *     <li>Настраивает камеру и ввод для игрока.</li>
     * </ol>
     *
     * @param characterType Тип персонажа, например "knight" или "panther".
     */
    public void startGame(String characterType) {
        this.currentCharacterType = characterType;
        clearScene();

        GameWorldData worldData = initializer.initGameWorld(characterType);

        initializeGameWorld(worldData);
        setupCameraAndInput(worldData);
    }

    /**
     * Создает объекты игрового мира через фабрику {@link GameWorldFactory}.
     * <p>
     * Инициализируются:
     * <ul>
     *     <li>{@link #player} — сущность игрока</li>
     *     <li>{@link #playerService} — сервис игрока для движения и апгрейдов</li>
     *     <li>{@link #mapChunkService} — сервис управления картой и чанками</li>
     *     <li>{@link #collisionSystem} — система обработки коллизий</li>
     * </ul>
     *
     * @param worldData Данные игрового мира, возвращаемые {@link GameInitializerService}.
     */
    private void initializeGameWorld(GameWorldData worldData) {
        GameWorldFactory.GameWorldObjects gwo = GameWorldFactory.create(worldData, uiService);

        this.player = gwo.player;
        this.playerService = gwo.playerService;
        this.mapChunkService = gwo.mapChunkService;
        this.collisionSystem = new CollisionSystem();
    }

    /**
     * Настраивает камеру и обработку ввода для игрока.
     * <p>
     * Использует:
     * <ul>
     *     <li>{@link CameraManager} для слежения камеры за игроком</li>
     *     <li>{@link PlayerInputService} для связывания действий клавиатуры/мыши с игроком</li>
     * </ul>
     *
     * @param worldData Данные игрового мира для корректного позиционирования камеры.
     */
    private void setupCameraAndInput(GameWorldData worldData) {
        CameraManager cameraManager = new CameraManager();
        cameraManager.bindToPlayer(player, worldData);
        PlayerInputService.initInput(currentCharacterType, () -> player);
    }

    /**
     * Очищает текущую сцену: сущности, UI и ввод.
     * <p>
     * Используется при старте новой игры или перезапуске после Game Over.
     */
    private void clearScene() {
        if (mapChunkService != null) mapChunkService.clearChunks();
        FXGL.getGameWorld().removeEntities(FXGL.getGameWorld().getEntitiesCopy());
        FXGL.getGameScene().clearUINodes();
        FXGL.getInput().clearAll();
    }

    /**
     * Обновляет игровую логику каждый кадр.
     * <p>
     * Вызывает обновление:
     * <ul>
     *     <li>логики игрока (движение, апгрейды)</li>
     *     <li>коллизий</li>
     *     <li>UI (HealthBar, апгрейды)</li>
     *     <li>проверку Game Over</li>
     *     <li>карты чанков вокруг игрока</li>
     * </ul>
     *
     * @param tpf время, прошедшее с предыдущего кадра (time per frame)
     */
    public void update(double tpf) {
        if (player == null) return;

        playerService.update(tpf);                         // движение + апгрейды
        collisionSystem.update(player, tpf);               // коллизии
        uiService.update();                                // HealthBar и апгрейды UI
        uiService.checkGameOver(player, () -> startGame(currentCharacterType)); // GameOver
        mapChunkService.updateVisibleChunks(player.getX(), player.getY());
    }
}
