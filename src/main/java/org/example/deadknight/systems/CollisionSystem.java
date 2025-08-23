package org.example.deadknight.systems;

import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.components.HealthComponent;
import org.example.deadknight.types.EntityType;

/**
 * Система обработки столкновений.
 * <p>
 * Отвечает за проверку столкновений игрока (рыцаря) с опасными объектами,
 * такими как шипы, и за нанесение урона с учётом кулдауна.
 */
public class CollisionSystem {

    /** Время до следующего возможного нанесения урона (секунды). */
    private double damageCooldown = 0;

    /**
     * Обновляет состояние столкновений на каждом кадре.
     * <p>
     * Проверяет, сталкивается ли рыцарь с объектами типа {@link EntityType#SPIKES}.
     * Если столкновение произошло и кулдаун урона истёк, наносит урон рыцарю.
     * Если здоровье рыцаря падает до нуля или ниже, удаляет его из мира.
     *
     * @param knight сущность рыцаря, которую контролирует игрок
     * @param tpf    время кадра (Time Per Frame) в секундах
     */
    public void update(Entity knight, double tpf) {
        if (knight.getWorld() == null) return;

        damageCooldown -= tpf;

        for (Entity spike : knight.getWorld().getEntitiesByType(EntityType.SPIKES)) {
            if (knight.isColliding(spike) && damageCooldown <= 0) {
                HealthComponent health = knight.getComponent(HealthComponent.class);
                health.takeDamage(10);
                damageCooldown = 1.0;

                if (health.getValue() <= 0) {
                    knight.removeFromWorld();
                }
            }
        }
    }
}
