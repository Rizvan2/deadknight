package org.example.deadknight.services.init;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.player.entities.KnightEntity;
import org.example.deadknight.player.entities.IlyasPantherEntity;
import org.example.deadknight.mobs.entities.Spikes;
import org.example.deadknight.player.factories.KnightFactory;
import org.example.deadknight.player.factories.PantherFactory;
import static org.example.deadknight.services.MapService.generateBattlefieldLayers;

/**
 * Класс инициализации игрового мира.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Создание игрового персонажа (рыцарь или пантера)</li>
 *     <li>Расстановку препятствий (шипы)</li>
 *     <li>Генерацию и загрузку карты (пол и деревья)</li>
 * </ul>
 * <p>
 * Генерация карты создаёт PNG-файлы в папке <code>generated</code>, если они ещё не существуют.
 * Затем PNG загружается в игру как фоновые слои.
 */
public class GameInitializer {

    /**
     * Инициализирует игрового персонажа и игровой мир.
     *
     * @param characterType тип персонажа: "knight" или "panther"
     * @return созданная сущность персонажа {@link Entity}
     * @throws IllegalArgumentException если передан неизвестный тип персонажа
     */
    public static Entity initGame(String characterType) {

        // ===== Генерация или загрузка карты =====
        generateBattlefieldLayers("sdfs2342dfksdf", 15, 10);

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

        return character;
    }
}
