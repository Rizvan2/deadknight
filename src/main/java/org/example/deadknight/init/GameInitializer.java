package org.example.deadknight.init;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.example.deadknight.entities.KnightEntity;
import org.example.deadknight.entities.IlyasPantherEntity;
import org.example.deadknight.entities.Spikes;
import org.example.deadknight.factories.KnightFactory;
import org.example.deadknight.factories.PantherFactory;

/**
 * Класс инициализации игры.
 * <p>
 * Создает игрового персонажа (рыцарь или пантера), добавляет его в мир,
 * расставляет препятствия и устанавливает фон сцены.
 */
public class GameInitializer {

    /**
     * Инициализирует игрового персонажа и базовый игровой мир.
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

        for (int i = 0; i < 10; i++) {
            int x = 200 + i * 50;
            int y = 200;

            FXGL.getGameTimer().runOnceAfter(() -> {
                FXGL.spawn("goblin", new SpawnData(x, y)

                );
            }, Duration.seconds(8 + i));
        }



        return character;
    }

}
