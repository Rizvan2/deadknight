package org.example.deadknight.gameplay.actors.mobs.service;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.deadknight.gameplay.components.AnimationComponent;
import org.example.deadknight.gameplay.components.DeathAnimationComponent;
import org.example.deadknight.gameplay.actors.mobs.entities.GoblinEntity;

/**
 * Сервис для проигрывания анимации смерти сущности.
 * <p>
 * Отвечает за создание новой сущности с анимацией смерти и удаление оригинальной сущности.
 * Использует {@link GoblinEntity} для получения кадров анимации и {@link AnimationComponent}
 * для определения направления взгляда (вправо или влево).
 */
public class DeathAnimationService {

    /** Сущность, для которой проигрывается анимация смерти. */
    private final Entity entity;

    /** Данные гоблина, содержащие кадры анимации смерти. */
    private final GoblinEntity goblinData;

    /** Компонент анимации для определения направления взгляда сущности. */
    private final AnimationComponent animationComponent;

    /**
     * Конструктор сервиса.
     *
     * @param entity сущность, для которой будет проигрываться анимация смерти
     * @param goblinData данные гоблина, содержащие кадры анимации
     * @param animationComponent компонент анимации для определения направления взгляда
     */
    public DeathAnimationService(Entity entity, GoblinEntity goblinData, AnimationComponent animationComponent) {
        this.entity = entity;
        this.goblinData = goblinData;
        this.animationComponent = animationComponent;
    }

    /**
     * Проигрывает анимацию смерти для сущности.
     * <p>
     * Создаёт новую сущность с компонентом {@link DeathAnimationComponent},
     * устанавливает её позицию и направление взгляда, а затем удаляет оригинальную сущность из мира.
     */
    public void playDeathAnimation() {
        ImageView[] frames = goblinData.getDeathFrames(); // теперь это массив ImageView
        if (frames == null || frames.length == 0) return;

        boolean facingRight = animationComponent.isFacingRight();

        FXGL.entityBuilder()
                .at(entity.getX(), entity.getY())
                .zIndex(100)
                .buildAndAttach()
                .addComponent(new DeathAnimationComponent(frames, facingRight));

        entity.removeFromWorld();
    }

}
