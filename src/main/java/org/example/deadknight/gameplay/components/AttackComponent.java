package org.example.deadknight.gameplay.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;


/**
 * Компонент, отвечающий за атаку сущности (например, врага) по игроку.
 * <p>
 * Основные функции:
 * <ul>
 *     <li>Хранение урона атаки {@link #damage} и времени перезарядки {@link #cooldown}</li>
 *     <li>Попытка атаки игрока через {@link #tryAttack(Entity, double)}</li>
 *     <li>Запуск анимации атаки через {@link AnimationComponent}, если он добавлен к сущности</li>
 * </ul>
 */
public class AttackComponent extends Component {

    /** Урон, который наносит сущность при атаке */
    private final int damage;

    /** Время перезарядки между атаками (в секундах) */
    private final double cooldown;

    /** Время, прошедшее с последней атаки */
    private double lastAttackTime;

    public AttackComponent(int damage, double cooldown) {
        this.damage = damage;
        this.cooldown = cooldown;
    }

    /**
     * Пытается нанести урон указанной сущности (например, игроку).
     * <p>
     * Если прошедшее время с последней атаки превышает {@link #cooldown},
     * наносится урон, обновляется анимация атаки (если есть {@link AnimationComponent})
     * и сбрасывается таймер {@link #lastAttackTime}.
     *
     * @param player цель атаки (обычно игрок)
     * @param tpf    время кадра (time per frame) для обновления таймера атаки
     */
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
