package org.example.deadknight.gameplay.actors.mobs.factories.util;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.image.ImageView;

import java.util.Arrays;

/**
 * Утилитарный класс для загрузки и подготовки анимационных кадров гоблина.
 * <p>
 * Класс предоставляет методы для получения массивов {@link ImageView},
 * которые содержат последовательности кадров для различных анимаций:
 * <ul>
 *     <li>Ходьба вправо и влево</li>
 *     <li>Атака вправо и влево</li>
 *     <li>Смерть</li>
 * </ul>
 * Для анимаций влево кадры автоматически зеркалируются с помощью {@code setScaleX(-1)}.
 * <p>
 * Размер кадров задаётся при создании экземпляра класса.
 */
public class GoblinAnimationLoader {

    /** Размер (ширина и высота) кадра гоблина в пикселях. */
    private final int goblinSize;

    /**
     * Создаёт загрузчик анимаций для гоблина.
     *
     * @param goblinSize требуемый размер кадров (ширина и высота) в пикселях
     */
    public GoblinAnimationLoader(int goblinSize) {
        this.goblinSize = goblinSize;
    }

    /**
     * Загружает кадры анимации ходьбы вправо.
     *
     * @return массив кадров ходьбы вправо
     */
    public ImageView[] loadWalkRight() {
        return loadFrames("goblin/goblin-", 25, false);
    }

    /**
     * Загружает кадры анимации ходьбы влево (зеркальные).
     *
     * @return массив кадров ходьбы влево
     */
    public ImageView[] loadWalkLeft() {
        return loadFrames("goblin/goblin-", 25, true);
    }

    /**
     * Загружает кадры анимации атаки вправо.
     *
     * @return массив кадров атаки вправо
     */
    public ImageView[] loadAttackRight() {
        return loadFrames("goblin/goblin_attack-", 15, false);
    }

    /**
     * Загружает кадры анимации атаки влево (зеркальные).
     *
     * @return массив кадров атаки влево
     */
    public ImageView[] loadAttackLeft() {
        return loadFrames("goblin/goblin_attack-", 15, true);
    }

    /**
     * Загружает кадры анимации смерти.
     * <p>
     * Последний кадр дублируется, чтобы зафиксировать гоблина
     * в позе смерти.
     *
     * @return массив кадров смерти
     */
    public ImageView[] loadDeathFrames() {
        ImageView[] frames = loadFrames("goblin/goblin_death-", 4, false);
        ImageView last = frames[frames.length - 1];
        ImageView[] extended = Arrays.copyOf(frames, frames.length + 1);
        extended[frames.length] = new ImageView(last.getImage());
        return extended;
    }

    /**
     * Загружает указанное количество кадров по заданному префиксу
     * и, при необходимости, зеркалирует их.
     *
     * @param prefix   путь и префикс имени файлов (например, {@code "goblin/goblin-"})
     * @param count    количество кадров
     * @param mirrored если {@code true}, кадры зеркалируются по оси X
     * @return массив кадров анимации
     */
    private ImageView[] loadFrames(String prefix, int count, boolean mirrored) {
        ImageView[] frames = new ImageView[count];
        for (int i = 1; i <= count; i++) {
            ImageView iv = new ImageView(FXGL.image(prefix + i + ".png"));
            iv.setFitWidth(goblinSize);
            iv.setFitHeight(goblinSize);
            if (mirrored) {
                iv.setScaleX(-1);
            }
            frames[i - 1] = iv;
        }
        return frames;
    }
}
