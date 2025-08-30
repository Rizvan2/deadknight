package org.example.deadknight.player.entities;

import lombok.Getter;
import lombok.Setter;
import org.example.deadknight.components.HealthComponent;
import org.example.deadknight.player.components.SpeedComponent;
import org.example.deadknight.player.factories.PantherFactory;

/**
 * Модель данных для игрового персонажа «Пантера Ильяса».
 * <p>
 * Хранит характеристики персонажа: здоровье, скорость и текущее направление движения.
 * Используется фабрикой {@link PantherFactory}
 * для создания игровой сущности {@link com.almasb.fxgl.entity.Entity}.
 */
@Getter
@Setter
public class IlyasPantherEntity {

    /** Компонент здоровья персонажа. */
    private final HealthComponent health;

    /** Компонент скорости персонажа. */
    private final SpeedComponent speed;

    /** Текущее направление движения персонажа (например, "RIGHT", "LEFT"). */
    private String direction;

    /**
     * Конструктор для инициализации базовых характеристик пантеры.
     *
     * @param hp               количество очков здоровья персонажа
     * @param speedValue       скорость движения персонажа
     * @param initialDirection начальное направление движения персонажа
     */
    public IlyasPantherEntity(int hp, int speedValue, String initialDirection) {
        this.health = new HealthComponent(hp);
        this.speed = new SpeedComponent(speedValue);
        this.direction = initialDirection;
    }
}
