package org.example.deadknight.gameplay.actors.essences.factory;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import org.example.deadknight.gameplay.components.types.EntityTypeEssences;

public class EssenceFactory implements EntityFactory {

    @Spawns("healthEssence")
    public Entity newHealthEssence(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityTypeEssences.HEALTH_ESSENCE)
                .view(FXGL.texture("essences/life/LifeEssence-1.png", 64, 64)) // задаём ширину и высоту
                .bbox(new HitBox("BODY", BoundingShape.box(32, 32))) // hitbox совпадает с текстурой
                .with("healAmount", 20) // храним просто свойство
                .collidable()
                .build();
    }

    @Spawns("upgradeEssence")
    public Entity newEclipseOfForgottenSouls(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityTypeEssences.UPGRADE_ESSENCE) // можно создать отдельный тип, если хочешь
                .view(FXGL.texture("essences/upgradeEssence/eclipse_of_forgotten_souls.png", 32, 32))
                .bbox(new HitBox("BODY", BoundingShape.box(32, 32)))
                .with("damageAmount", 50) // пример свойства, можно любое
                .collidable()
                .build();
    }
}
