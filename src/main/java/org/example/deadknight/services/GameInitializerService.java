package org.example.deadknight.services;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.factories.GoblinFactory;
import org.example.deadknight.init.GameInitializer;

import java.util.List;

/**
 * Сервис для инициализации игровой сцены.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Регистрацию фабрики сущностей</li>
 *     <li>Создание игрока</li>
 *     <li>Спавн врагов на сцене</li>
 * </ul>
 */
public class GameInitializerService {

    /**
     * Конструктор сервиса.
     * Регистрирует {@link GoblinFactory} в игровом мире.
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
     * Спавнит врагов на сцене в указанных координатах.
     *
     * @param positions список координат, где каждый элемент — массив из двух значений: x и y
     */
    public void spawnEnemies(List<double[]> positions) {
        for (double[] pos : positions) {
            FXGL.spawn("goblin", pos[0], pos[1]);
        }
    }
}
