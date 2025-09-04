package org.example.deadknight.services;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import org.example.deadknight.gameplay.actors.mobs.factories.GoblinFactory;
import org.example.deadknight.gameplay.actors.player.controllers.KnightController;
import org.example.deadknight.gameplay.actors.player.controllers.PantherController;
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
        return GameInitializer.createGameWorld(characterType);
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
     * Спавнит указанное количество гоблинов после небольшой задержки, чтобы дождаться полной загрузки карты.
     * <p>
     * Каждый гоблин создаётся с равным шагом по координате X.
     *
     * @param count количество гоблинов для спавна
     */
    public void spawnEnemiesAfterMapLoaded(int count) {
        FXGL.runOnce(() -> {
            for (int i = 0; i < count; i++) {
                int xPos = 200 + i * 50;
                int yPos = 200;
                FXGL.spawn("goblin", new SpawnData(xPos, yPos));
            }
        }, Duration.seconds(7)); // ждём полсекунды после загрузки карты
    }

    public static void spawnEnemiesFromAllSidesWithDelay(int countPerSide, Point2D mapSize) {
        double width = mapSize.getX();
        double height = mapSize.getY();

        double stepX = width / (countPerSide + 1);
        double stepY = height / (countPerSide + 1);

        for (int i = 0; i < countPerSide; i++) {
            double xOffset = stepX * (i + 1);
            double yOffset = stepY * (i + 1);

            int index = i;

            FXGL.runOnce(() -> {
                // Верхняя сторона
                FXGL.spawn("goblin", new SpawnData(xOffset, 0));

                // Нижняя сторона
                FXGL.spawn("goblin", new SpawnData(xOffset, height));

                // Левая сторона
                FXGL.spawn("goblin", new SpawnData(0, yOffset));

                // Правая сторона
                FXGL.spawn("goblin", new SpawnData(width, yOffset));
            }, Duration.seconds(index));
        }
    }



}
