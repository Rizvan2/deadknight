package org.example.deadknight.components;

import com.almasb.fxgl.entity.component.Component;

public class SpeedComponent extends Component {

    private int speed;

    public SpeedComponent(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
