package org.example.deadknight.services;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import org.example.deadknight.mobs.factories.GoblinFactory;
import org.example.deadknight.player.controllers.KnightController;
import org.example.deadknight.player.controllers.PantherController;
import org.example.deadknight.services.init.GameInitializer;

import java.util.function.Supplier;

/**
 * Сервис для инициализации игровой сцены.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Создание игрока</li>
 *     <li>Настройку контроллеров персонажей</li>
 *     <li>Инициализацию UI</li>
 *     <li>Спавн врагов на сцене</li>
 * </ul>
 */
public class GameInitializerService {

    /**
     * Конструктор сервиса.
     * Регистрирует {@link GoblinFactory} в игровом мире FXGL.
     */
    public GameInitializerService() {
        FXGL.getGameWorld().addEntityFactory(new GoblinFactory());
    }

    /**
     * Создаёт и возвращает сущность игрока заданного типа.
     *
     * @param characterType тип персонажа ("knight", "panther" и т.д.)
     * @return сущность игрока
     */
    public Entity initPlayer(String characterType) {
        return GameInitializer.initGame(characterType);
    }

    /**
     * Настраивает контроллеры персонажа в зависимости от выбранного типа.
     *
     * @param player        сущность игрока
     * @param characterType тип персонажа ("knight", "panther" и т.д.)
     */
    private void setupControllers(Entity player, String characterType) {
        Supplier<Entity> entitySupplier = () -> player;
        switch (characterType) {
            case "knight" -> KnightController.initInput(entitySupplier);
            case "panther" -> PantherController.initInput(entitySupplier);
        }
    }

    /**
     * Спавнит указанное количество врагов с равным шагом по X.
     *
     * @param count количество врагов для спавна
     */
    public void spawnEnemies(int count) {
        for (int i = 0; i < count; i++) {
            int xPos = 200 + i * 50;
            int yPos = 200;
            FXGL.spawn("goblin", new SpawnData(xPos, yPos));
        }
    }
}
