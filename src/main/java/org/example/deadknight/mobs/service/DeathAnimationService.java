package org.example.deadknight.mobs.service;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.image.Image;
import org.example.deadknight.mobs.components.AnimationComponent;
import org.example.deadknight.mobs.entities.GoblinEntity;

import java.util.List;

/**
 * Сервис для проигрывания анимации смерти сущности.
 */
public class DeathAnimationService {

    private final Entity entity;
    private final GoblinEntity goblinData;
    private final AnimationComponent animationComponent;

    public DeathAnimationService(Entity entity, GoblinEntity goblinData, AnimationComponent animationComponent) {
        this.entity = entity;
        this.goblinData = goblinData;
        this.animationComponent = animationComponent;
    }

    public void playDeathAnimation() {
        List<Image> frames = goblinData.getDeathFrames();
        if (frames == null || frames.isEmpty()) return;

        boolean facingRight = animationComponent.getScaleX() > 0;

        FXGL.entityBuilder()
            .at(entity.getX(), entity.getY())
            .zIndex(100)
            .buildAndAttach()
            .addComponent(new org.example.deadknight.mobs.components.DeathAnimationComponent(frames, facingRight));

        entity.removeFromWorld();
    }
}
