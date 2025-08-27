package org.example.deadknight.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.image.ImageView;
import org.example.deadknight.entities.KnightEntity;
import org.example.deadknight.services.AnimationService;
import org.example.deadknight.types.EntityType;

public class KnightFactory {

    public static Entity create(KnightEntity knightData, double x, double y) {
        // Дефолтный спрайт
        ImageView knightSprite = new ImageView(FXGL.image("knight_left-1.png"));
        knightSprite.setFitWidth(64);
        knightSprite.setFitHeight(64);

        Entity knight = FXGL.entityBuilder()
                .at(x, y)
                .view(knightSprite)
                .bbox(new HitBox("BODY", BoundingShape.box(44, 44)))
                .with(knightData.getHealth())
                .with(knightData.getSpeed())
                .type(EntityType.KNIGHT)
                .zIndex(100)
                .build();

        // Свойства сущности
        knight.getProperties().setValue("isAttacking", false);
        knight.getProperties().setValue("moving", false);
        knight.getProperties().setValue("direction", knightData.getDirection());
        knight.getProperties().setValue("spriteDir", knightData.getDirection());
        knight.getProperties().setValue("shootDir", knightData.getDirection());

        // Подключаем анимацию ходьбы
        String[] frames = {
                "knight_left-1.png",
                "knight_left-2.png",
                "knight_left-3.png",
                "knight_left-4.png",
                "knight_left-5.png"
        };
        AnimationService.attach(knight, frames);

        knight.getProperties().setValue("playAttack", (Runnable) () ->
                AnimationService.playAttack(knight, "knight_attack.png", 0.25)
        );


        return knight;
    }
}
