package org.example.deadknight.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class HealthComponent extends Component {

    private final IntegerProperty value;
    private final int maxValue;

    public HealthComponent(int value) {
        this.value = new SimpleIntegerProperty(value);
        this.maxValue = value;
    }

    public void takeDamage(int dmg) {
        value.set(value.get() - dmg);

        if (value.get() <= 0 && entity != null) {
            entity.removeFromWorld(); // удаляем сущность
        }
    }

    public IntegerProperty valueProperty() {
        return value;
    }

    public int getValue() {
        return value.get();
    }

    public int getMaxValue() {
        return maxValue;
    }

    public boolean isDead() {
        return value.get() <= 0;
    }
}
