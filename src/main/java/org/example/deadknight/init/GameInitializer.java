package org.example.deadknight.init;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.paint.Color;
import org.example.deadknight.factories.KnightFactory;
import org.example.deadknight.entities.KnightEntity;
import org.example.deadknight.entities.Spikes;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;

public class GameInitializer {

    public static Entity initGame() {
        // Создаём данные рыцаря
        KnightEntity knightData = new KnightEntity(100, 40, "RIGHT"); // hp, скорость, направление
        // Создаём сущность через фабрику
        Entity knight = KnightFactory.create(knightData, 100, 300);
        FXGL.getGameWorld().addEntity(knight);

        FXGL.getGameWorld().addEntity(Spikes.create(200, 300));
        FXGL.getGameWorld().addEntity(Spikes.create(400, 300));
        getGameScene().setBackgroundColor(Color.BLACK);

        return knight;
    }
}
