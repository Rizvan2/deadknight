package org.example.deadknight.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.image.ImageView;
import org.example.deadknight.entities.IlyasPantherEntity;
import org.example.deadknight.services.AnimationService;
import org.example.deadknight.services.PantherAttackService;
import org.example.deadknight.types.EntityType;

public class PantherFactory {

    public static Entity create(IlyasPantherEntity data, double x, double y) {
        ImageView pantherSprite = new ImageView(FXGL.image("panter1-1.png"));
        pantherSprite.setFitWidth(122);
        pantherSprite.setFitHeight(122);

        Entity panther = FXGL.entityBuilder()
                .at(x, y)
                .view(pantherSprite)
                .bbox(new HitBox("BODY", BoundingShape.box(122, 122)))
                .with(data.getHealth())
                .with(data.getSpeed())
                .type(EntityType.PANTHER)
                .zIndex(100)
                .build();

        initProperties(panther, data);

        return panther;
    }

    private static void initProperties(Entity panther, IlyasPantherEntity data) {
        panther.getProperties().setValue("isAttacking", false);
        panther.getProperties().setValue("moving", false);
        panther.getProperties().setValue("direction", data.getDirection());
        panther.getProperties().setValue("spriteDir", data.getDirection());
        panther.getProperties().setValue("shootDir", data.getDirection());

        // Анимация ходьбы
        String[] frames = {"panter1.png"};
        AnimationService.attach(panther, frames);

        // Убираем хардкод атаки, только команда:
        panther.getProperties().setValue("playAttack", (Runnable) () ->
                PantherAttackService.playAttack(panther, "panter_attack.png", 0.3, 150)
        );
    }
}
