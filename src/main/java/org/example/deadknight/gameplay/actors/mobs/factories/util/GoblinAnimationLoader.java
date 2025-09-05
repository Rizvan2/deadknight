package org.example.deadknight.gameplay.actors.mobs.factories.util;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.image.ImageView;

import java.util.Arrays;

/**
 * Утилитарный класс для загрузки анимаций гоблина.
 * <p>
 * Предоставляет методы для получения кадров анимации ходьбы,
 * атаки и смерти, а также для создания зеркальных версий кадров
 * для движения влево.
 */
public class GoblinAnimationLoader {

    private final int goblinSize;

    /**
     * Утилитарный класс для загрузки анимаций гоблина.
     * <p>
     * Предоставляет методы для получения кадров анимации ходьбы,
     * атаки и смерти, а также для создания зеркальных версий кадров
     * для движения влево.
     */
    public GoblinAnimationLoader(int goblinSize) {
        this.goblinSize = goblinSize;
    }

    /**
     * Загружает кадры анимации ходьбы вправо.
     *
     * @return массив ImageView с кадрами анимации ходьбы вправо
     */
    public ImageView[] loadWalkRight() {
        return loadFrames("goblin/goblin-", 25);
    }

    /**
     * Создает зеркальные кадры анимации ходьбы для движения влево.
     *
     * @return массив ImageView с кадрами анимации ходьбы влево
     */
    public ImageView[] loadWalkLeft() {
        return createMirrored(loadWalkRight());
    }

    /**
     * Загружает кадры анимации атаки вправо.
     *
     * @return массив ImageView с кадрами анимации атаки вправо
     */
    public ImageView[] loadAttackRight() {
        return loadFrames("goblin/goblin_attack-", 15);
    }

    /**
     * Создает зеркальные кадры анимации атаки для движения влево.
     *
     * @return массив ImageView с кадрами анимации атаки влево
     */
    public ImageView[] loadAttackLeft() {
        return createMirrored(loadAttackRight());
    }

    /**
     * Загружает кадры анимации смерти гоблина.
     * <p>
     * Последний кадр дублируется, чтобы визуально "заставить"
     * гоблина оставаться мертвым на экране.
     *
     * @return массив ImageView с кадрами анимации смерти
     */
    public ImageView[] loadDeathFrames() {
        ImageView[] frames = loadFrames("goblin/goblin_death-", 4);
        // повторяем последний кадр
        ImageView last = frames[frames.length - 1];
        ImageView[] extended = Arrays.copyOf(frames, frames.length + 1);
        extended[frames.length] = new ImageView(last.getImage());
        return extended;
    }

    /**
     * Загружает последовательность кадров из ресурсов.
     *
     * @param prefix префикс имени файла (например, "goblin/goblin-")
     * @param count  количество кадров
     * @return массив ImageView с загруженными кадрами
     */
    private ImageView[] loadFrames(String prefix, int count) {
        ImageView[] frames = new ImageView[count];
        for (int i = 1; i <= count; i++) {
            ImageView iv = new ImageView(FXGL.image(prefix + i + ".png"));
            iv.setFitWidth(goblinSize);
            iv.setFitHeight(goblinSize);
            frames[i - 1] = iv;
        }
        return frames;
    }

    /**
     * Создает зеркальные копии кадров для движения влево.
     *
     * @param original массив исходных кадров
     * @return массив ImageView с зеркальными кадрами
     */
    private ImageView[] createMirrored(ImageView[] original) {
        ImageView[] mirrored = new ImageView[original.length];
        for (int i = 0; i < original.length; i++) {
            ImageView iv = new ImageView(original[i].getImage());
            iv.setFitWidth(goblinSize);
            iv.setFitHeight(goblinSize);
            iv.setScaleX(-1);
            mirrored[i] = iv;
        }
        return mirrored;
    }
}
