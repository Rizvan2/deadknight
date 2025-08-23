package org.example.deadknight.components;

import com.almasb.fxgl.entity.component.Component;

public class HealthComponent extends Component {

    private int value;

    public HealthComponent(int value) {
        this.value = value;
    }

    public void takeDamage(int dmg) {
        value -= dmg;

        if (value <= 0 && entity != null) {
            entity.removeFromWorld(); // удаляем сущность из мира
        }
    }

    public int getValue() {
        return value;
    }
}
