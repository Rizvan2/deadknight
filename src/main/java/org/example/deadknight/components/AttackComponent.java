package org.example.deadknight.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;

/**
 * Отвечает за атаку игрока с заданным уроном и кулдауном.
 */
public class AttackComponent extends Component {

    private final int damage;
    private final double cooldown;
    private double lastAttackTime = 0;

    public AttackComponent(int damage, double cooldown) {
        this.damage = damage;
        this.cooldown = cooldown;
    }

    public void tryAttack(Entity player, double tpf) {
        lastAttackTime += tpf;
        if (lastAttackTime >= cooldown) {
            lastAttackTime = 0;
            player.getComponentOptional(HealthComponent.class).ifPresent(h -> {
                if (!h.isDead()) {
                    h.takeDamage(damage);
                    if (h.isDead()) {
                        player.removeFromWorld();
                    }
                }
            });
            entity.getComponentOptional(AnimationComponent.class)
                    .ifPresent(AnimationComponent::playAttack);
        }
    }
}
