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

    /** Компонент атаки, реализующий логику удара. */
    private final AttackComponent attack;

    /** Компонент анимации для проигрывания атакующих анимаций. */
    private final AnimationComponent animation;

    /**
     * Конструктор.
     *
     * @param attack компонент атаки
     * @param animation компонент анимации
     */
    public CombatService(AttackComponent attack, AnimationComponent animation) {
        this.attack = attack;
        this.animation = animation;
    }

    /**
     * Попытка атаки по указанной цели.
     * <p>
     * Сначала проигрывается анимация атаки, затем выполняется логика удара.
     *
     * @param target цель атаки
     * @param tpf время кадра (time per frame)
     */
    public void tryAttack(Entity target, double tpf) {
        animation.playAttack();
        attack.tryAttack(target, tpf);
    }

    /**
     * Проверяет, находится ли цель в пределах радиуса атаки.
     *
     * @param attacker сущность, выполняющая атаку
     * @param target цель атаки
     * @param range радиус атаки
     * @return true, если цель находится в пределах радиуса
     */
    public boolean isInRange(Entity attacker, Entity target, double range) {
        return attacker.getPosition().distance(target.getPosition()) <= range;
    }
}
