package org.example.deadknight.mobs.service;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.image.Image;
import org.example.deadknight.mobs.components.AnimationComponent;
import org.example.deadknight.mobs.entities.GoblinEntity;

import java.util.List;

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
     * Создаёт новую сущность с компонентом {@link org.example.deadknight.mobs.components.DeathAnimationComponent},
     * устанавливает её позицию и направление взгляда, а затем удаляет оригинальную сущность из мира.
     */
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
