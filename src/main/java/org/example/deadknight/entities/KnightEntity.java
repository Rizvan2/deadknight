package org.example.deadknight.entities;

import org.example.deadknight.components.HealthComponent;
import org.example.deadknight.components.SpeedComponent;
import org.example.deadknight.skills.Skill;

public class KnightEntity {

    private final HealthComponent health;
    private final SpeedComponent speed;
    private String direction;

    public KnightEntity(int hp, int speedValue, String initialDirection) {
        this.health = new HealthComponent(hp);
        this.speed = new SpeedComponent(speedValue);
        this.direction = initialDirection;
    }

    public HealthComponent getHealth() {
        return health;
    }

    public SpeedComponent getSpeed() {
        return speed;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
