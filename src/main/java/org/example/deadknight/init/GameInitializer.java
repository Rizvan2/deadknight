package org.example.deadknight.init;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.paint.Color;
import org.example.deadknight.entities.KnightEntity;
import org.example.deadknight.entities.IlyasPantherEntity; // твоя будущая пантера
import org.example.deadknight.entities.Spikes;
import org.example.deadknight.factories.KnightFactory;
import org.example.deadknight.factories.PantherFactory;    // фабрика пантеры

public class GameInitializer {

    public static Entity initGame(String characterType) {
        Entity character;

        switch (characterType) {
            case "knight":
                KnightEntity knightData = new KnightEntity(100, 40, "RIGHT");
                character = KnightFactory.create(knightData, 100, 300);
                break;

            case "panther":
                IlyasPantherEntity pantherData = new IlyasPantherEntity(120, 60, "RIGHT"); // свои статы
                character = PantherFactory.create(pantherData, 100, 300);
                break;

            default:
                throw new IllegalArgumentException("Неизвестный тип персонажа: " + characterType);
        }

        FXGL.getGameWorld().addEntity(character);

        FXGL.getGameWorld().addEntity(Spikes.create(200, 300));
        FXGL.getGameWorld().addEntity(Spikes.create(400, 300));
        FXGL.getGameScene().setBackgroundColor(Color.BLACK);

        return character;
    }

}
