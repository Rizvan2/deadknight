package org.example.deadknight.gameplay.actors.mobs.service;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import org.example.deadknight.gameplay.actors.mobs.components.AttackComponent;
import org.example.deadknight.gameplay.actors.mobs.components.AnimationComponent;

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
     * Проверяет, находится ли цель в радиусе атаки относительно атакующего.
     * <p>
     * Радиус вычисляется как расстояние между центрами сущностей (attacker и target)
     * и сравнивается с указанным диапазоном {@code range}.
     * Если расстояние меньше или равно {@code range}, метод возвращает {@code true}.
     *
     * @param attacker сущность, выполняющая атаку
     * @param target цель атаки
     * @param range радиус атаки в пикселях
     * @return {@code true}, если центр цели находится на расстоянии {@code range} или ближе, иначе {@code false}
     */
    public boolean isInRange(Entity attacker, Entity target, double range) {
        Point2D attackerCenter = attacker.getCenter();
        Point2D targetCenter = target.getCenter();
        return attackerCenter.distance(targetCenter) <= range;
    }
}
