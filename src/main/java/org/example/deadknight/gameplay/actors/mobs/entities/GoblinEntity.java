package org.example.deadknight.gameplay.actors.mobs.entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

 * </ul>
 */
@Getter
@Setter
public class GoblinEntity {

    /** Скорость движения моба */
    private final double speed;

    /** Урон, наносимый мобом */
    private final int damage;

    // Кадры для движения и атаки
    private final ImageView[] walkRight;
    private final ImageView[] walkLeft;
    private final ImageView[] attackRight;
    private final ImageView[] attackLeft;
    private final ImageView[] deathFrames;

    public GoblinEntity(double speed,
                        int damage,
                        ImageView[] walkRight,
                        ImageView[] walkLeft,
                        ImageView[] attackRight,
                        ImageView[] attackLeft,
                        ImageView[] deathFrames) {
        this.speed = speed;
        this.damage = damage;
        this.walkRight = walkRight;
        this.walkLeft = walkLeft;
        this.attackRight = attackRight;
        this.attackLeft = attackLeft;
        this.deathFrames = deathFrames;
    }
}
