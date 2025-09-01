package org.example.deadknight.mobs.service;

import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.mobs.components.AttackComponent;
import org.example.deadknight.mobs.components.AnimationComponent;

/**
 * Сервис для управления боевым поведением сущности.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Попытку атаки по цели с проигрыванием анимации атаки</li>
 *     <li>Проверку, находится ли цель в пределах радиуса атаки</li>
 * </ul>
 */
public class CombatService {

    private final AttackComponent attack;
    private final AnimationComponent animation;

    public CombatService(AttackComponent attack, AnimationComponent animation) {
        this.attack = attack;
        this.animation = animation;
    }

    /**
     * Попытка атаки по указанной цели.
     * <p>
     * Если анимация атаки ещё не выполняется, она запускается.
     * Логика удара вызывается каждый кадр через {@link AttackComponent#tryAttack(Entity, double)}.
     *
     * @param target цель атаки
     * @param tpf время кадра (time per frame)
     */
    public void tryAttack(Entity target, double tpf) {
        if (!animation.isAttacking()) {
            animation.playAttack();
        }
        attack.tryAttack(target, tpf);
    }

    /**
     * Проверяет, находится ли цель в радиусе атаки.
     *
     * @param attacker сущность, выполняющая атаку
     * @param target цель атаки
     * @param range радиус атаки
     * @return true, если цель в пределах радиуса
     */
    public boolean isInRange(Entity attacker, Entity target, double range) {
        return attacker.getPosition().distance(target.getPosition()) <= range;
    }
}
