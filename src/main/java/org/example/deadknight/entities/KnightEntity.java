package org.example.deadknight.entities;

import lombok.Getter;
import lombok.Setter;
import org.example.deadknight.components.HealthComponent;
import org.example.deadknight.components.SpeedComponent;

/**
 * Класс, представляющий рыцаря (игрока) в игре.
 * <p>
 * Содержит:
 * <ul>
 *     <li>{@link HealthComponent} для управления здоровьем</li>
 *     <li>{@link SpeedComponent} для управления скоростью</li>
 *     <li>Направление движения или взгляда</li>
 * </ul>
 */
@Getter
@Setter
public class KnightEntity {

    /** Компонент здоровья */
    private final HealthComponent health;

    /** Компонент скорости */
    private final SpeedComponent speed;

    /** Текущие направление движения или взгляда ("left", "right", "up", "down") */
    private String direction;

    /**
     * Конструктор для инициализации рыцаря с заданным здоровьем, скоростью и направлением.
     *
     * @param hp              количество очков здоровья
     * @param speedValue      значение скорости
     * @param initialDirection начальное направление движения/взгляда
     */
    public KnightEntity(int hp, int speedValue, String initialDirection) {
        this.health = new HealthComponent(hp);
        this.speed = new SpeedComponent(speedValue);
        this.direction = initialDirection;
    }
}
