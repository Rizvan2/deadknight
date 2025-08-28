package org.example.deadknight.handlers;

import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.components.HealthComponent;

public class AttackHandler {

    private final int damage;

    public AttackHandler(int damage) {
        this.damage = damage;
    }

    public void attack(Entity player) {
        if (player == null || !player.hasComponent(HealthComponent.class)) return;

        player.getComponentOptional(HealthComponent.class).ifPresent(h -> {
            if (!h.isDead()) {
                h.takeDamage(damage);
                if (h.isDead()) player.removeFromWorld();
            }
        });
    }
}
