package org.example.deadknight.player.components;

import com.almasb.fxgl.entity.component.Component;
import org.example.deadknight.player.services.HasSpeed;

/**
 * Компонент, отвечающий за скорость движения сущности.
 * <p>
 * Реализует интерфейс {@link HasSpeed}, позволяя другим системам
 * получать и изменять скорость игрока или врагов.
 */
public class SpeedComponent extends Component implements HasSpeed {

    /** Значение скорости сущности */
    private double speed;

    /**
     * Конструктор компонента.
     *
     * @param speed начальная скорость сущности
     */
    public SpeedComponent(double speed) {
        this.speed = speed;
    }

    /**
     * Получает текущую скорость сущности.
     *
     * @return значение скорости
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Устанавливает новую скорость сущности.
     *
     * @param speed новое значение скорости
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
