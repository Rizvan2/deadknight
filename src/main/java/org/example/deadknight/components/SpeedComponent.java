package org.example.deadknight.components;

import com.almasb.fxgl.entity.component.Component;
import org.example.deadknight.services.HasSpeed;

public class SpeedComponent extends Component implements HasSpeed {

    private double speed;

    public SpeedComponent(int speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
