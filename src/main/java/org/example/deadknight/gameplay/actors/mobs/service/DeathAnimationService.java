package org.example.deadknight.gameplay.actors.mobs.service;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.image.ImageView;
import org.example.deadknight.gameplay.actors.mobs.components.AnimationComponent;
import org.example.deadknight.gameplay.actors.mobs.components.DeathAnimationComponent;
import org.example.deadknight.gameplay.actors.mobs.entities.GoblinEntity;

/**
 * Сервис для проигрывания анимации смерти сущности.
 * <p>
 * Отвечает за создание новой сущности с анимацией смерти и удаление оригинальной сущности.
 * Использует {@link GoblinEntity} для получения кадров анимации и {@link AnimationComponent}
 * для определения направления взгляда (вправо или влево).
 */
public class DeathAnimationService {

    private final Entity entity;
    private final GoblinEntity goblinData;
    private final AnimationComponent animationComponent;

    /** Флаг, показывающий, была ли проиграна анимация смерти */
    private boolean deathPlayed = false;

    public DeathAnimationService(Entity entity, GoblinEntity goblinData, AnimationComponent animationComponent) {
        this.entity = entity;
        this.goblinData = goblinData;
        this.animationComponent = animationComponent;
    }

    /** Проверяет, была ли уже проиграна анимация смерти */
    public boolean isDeathPlayed() {
        return deathPlayed;
    }

    public void playDeathAnimation() {
        if (deathPlayed) return; // защита от повторного вызова

        ImageView[] frames = goblinData.getDeathFrames();
        if (frames == null || frames.length == 0) return;

        boolean facingRight = animationComponent.isFacingRight();

        FXGL.entityBuilder()
                .at(entity.getX(), entity.getY())
                .zIndex(100)
                .buildAndAttach()
                .addComponent(new DeathAnimationComponent(frames, facingRight));

        entity.removeFromWorld();
        deathPlayed = true; // помечаем, что анимация проиграна
    }
}
