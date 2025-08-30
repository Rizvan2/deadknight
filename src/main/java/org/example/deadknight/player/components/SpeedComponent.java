package org.example.deadknight.player.components;

import com.almasb.fxgl.entity.component.Component;
import org.example.deadknight.player.services.HasSpeed;

public class SpeedComponent extends Component implements HasSpeed {

    private double speed;

    public SpeedComponent(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
