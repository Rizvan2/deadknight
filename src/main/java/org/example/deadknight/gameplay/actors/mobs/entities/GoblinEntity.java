package org.example.deadknight.gameplay.actors.mobs.entities;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Класс, представляющий данные моба типа "Гоблин".
 * <p>
 * Содержит характеристики и анимацию моба:
 * <ul>
 *     <li>Скорость движения {@link #speed}</li>
 *     <li>Урон атаки {@link #damage}</li>
 *     <li>Кадры анимации ходьбы {@link #walkFrames}</li>
 *     <li>Кадры анимации атаки {@link #attackFrames}</li>
 * </ul>
 */
@Getter
@Setter
public class GoblinEntity {

    /** Скорость движения моба */
    private final double speed;

    /** Урон, наносимый мобом */
    private final int damage;

    /** Список кадров для анимации ходьбы */
    private final List<Image> walkFrames;

    /** Список кадров для анимации атаки */
    private final List<Image> attackFrames;

    /** Список кадров для анимации смерти */
    private List<Image> deathFrames;


    /**
     * Конструктор для создания нового моба-гоблина.
     *
     * @param speed       скорость движения
     * @param damage      урон атаки
     * @param walkFrames  кадры анимации ходьбы
     * @param attackFrames кадры анимации атаки
     */
    public GoblinEntity(double speed, int damage, List<Image> walkFrames, List<Image> attackFrames, List<Image> deathFrames) {
        this.speed = speed;
        this.damage = damage;
        this.walkFrames = walkFrames;
        this.attackFrames = attackFrames;
        this.deathFrames = deathFrames;
    }
}
