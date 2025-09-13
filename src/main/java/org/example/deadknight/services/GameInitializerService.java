package org.example.deadknight.services;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.util.Duration;
import org.example.deadknight.gameplay.actors.mobs.factories.GoblinFactory;
import org.example.deadknight.services.init.GameInitializer;

import java.util.Random;

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
        return GameInitializer.createGameWorld(characterType).player();
    }

    /**
     * Новый метод: динамический спавн с задержкой и пересчетом видимых чанков.
     */
    public void spawnEnemiesAroundPlayer(Entity player, int count, double delayPerSpawnSeconds) {
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            int spawnIndex = i;

            FXGL.runOnce(() -> {
                double playerX = player.getX();
                double playerY = player.getY();

                // радиусы спавна: по X шире, по Y уже
                double radiusX = 1500; // например сбоку дальше
                double radiusY = 1000; // сверху/снизу ближе

                // случайный угол
                double angle = random.nextDouble() * 2 * Math.PI;

                // эллиптическое смещение
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
