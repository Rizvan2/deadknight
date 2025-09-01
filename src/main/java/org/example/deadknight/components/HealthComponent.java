package org.example.deadknight.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Getter;

/**
 * Компонент, отвечающий за здоровье сущности.
 * <p>
 * Позволяет:
 * <ul>
 *     <li>Отслеживать текущее и максимальное здоровье через свойства JavaFX.</li>
 *     <li>Наносить урон через {@link #takeDamage(int)}.</li>
 *     <li>Определять, жива ли сущность, через {@link #isDead()}.</li>
 *     <li>Автоматически удалять сущность из мира, если здоровье достигло 0.</li>
 * </ul>
 */
public class HealthComponent extends Component {

    /** Текущее здоровье сущности */
    private final IntegerProperty value;

    /** Максимальное здоровье сущности */
    @Getter
    private final int maxValue;

    /**
     * Конструктор компонента.
     *
     * @param value начальное (и максимальное) здоровье сущности
     */
    public HealthComponent(int value) {
        this.value = new SimpleIntegerProperty(value);
        this.maxValue = value;
    }

    /**
     * Наносит урон сущности.
     * <p>
     * Если здоровье становится меньше или равно 0, сущность удаляется из мира.
     *
     * @param dmg количество урона
     */
    public void takeDamage(int dmg) {
        value.set(value.get() - dmg);
    }

    /**
     * Получает свойство текущего здоровья.
     *
     * @return IntegerProperty с текущим значением здоровья
     */
    public IntegerProperty valueProperty() {
        return value;
    }

    /**
     * Возвращает текущее здоровье.
     *
     * @return текущее значение здоровья
     */
    public int getValue() {
        return value.get();
    }

    /**
     * Проверяет, умерла ли сущность.
     *
     * @return true, если здоровье меньше или равно 0, иначе false
     */
    public boolean isDead() {
        return value.get() <= 0;
    }
}
