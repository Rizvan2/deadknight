package org.example.deadknight.services.init;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.example.deadknight.player.entities.KnightEntity;
import org.example.deadknight.player.entities.IlyasPantherEntity;
import org.example.deadknight.mobs.entities.Spikes;
import org.example.deadknight.player.factories.KnightFactory;
import org.example.deadknight.player.factories.PantherFactory;

/**
 * Класс инициализации игры.
 * <p>
 * Отвечает за создание игрового персонажа, расстановку препятствий, установку фона сцены
 * и спавн врагов.
 */
public class GameInitializer {

    /**
     * Инициализирует игрового персонажа и базовый игровой мир.
     * <p>
     * В зависимости от выбранного типа персонажа:
     * <ul>
     *     <li>Создает сущность {@link KnightEntity} или {@link IlyasPantherEntity}</li>
     *     <li>Добавляет персонажа в игровой мир</li>
     *     <li>Добавляет препятствия {@link Spikes}</li>
     *     <li>Устанавливает фон сцены</li>
     *     <li>Запускает таймер на спавн врагов через {@link FXGL#getGameTimer()}</li>
     * </ul>
     *
     * @param characterType тип персонажа: "knight" или "panther"
     * @return созданная сущность персонажа {@link Entity}
     * @throws IllegalArgumentException если передан неизвестный тип персонажа
     */
    public static Entity initGame(String characterType) {
        Entity character;

        switch (characterType) {
            case "knight":
                KnightEntity knightData = new KnightEntity(100, 0.6, "RIGHT");
                character = KnightFactory.create(knightData, 100, 300);
                break;

            case "panther":
                IlyasPantherEntity pantherData = new IlyasPantherEntity(120, 60, "RIGHT");
                character = PantherFactory.create(pantherData, 100, 300);
                break;

            default:
                throw new IllegalArgumentException("Неизвестный тип персонажа: " + characterType);
        }

        // Добавляем персонажа в мир
        FXGL.getGameWorld().addEntity(character);

        // Добавляем препятствия
        FXGL.getGameWorld().addEntity(Spikes.create(200, 300));
        FXGL.getGameWorld().addEntity(Spikes.create(400, 300));

        // Устанавливаем фон сцены
        FXGL.getGameScene().setBackgroundColor(Color.BLACK);

        return character;
    }
}
