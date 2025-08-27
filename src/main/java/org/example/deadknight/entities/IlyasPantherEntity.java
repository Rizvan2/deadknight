package org.example.deadknight.entities;

import lombok.Getter;
import lombok.Setter;
import org.example.deadknight.components.HealthComponent;
import org.example.deadknight.components.SpeedComponent;

@Getter
@Setter
public class IlyasPantherEntity {

    private final HealthComponent health;
    private final SpeedComponent speed;
    private String direction;

    public IlyasPantherEntity(int hp, int speedValue, String initialDirection) {
        this.health = new HealthComponent(hp);
        this.speed = new SpeedComponent(speedValue);
        this.direction = initialDirection;
    }
}
