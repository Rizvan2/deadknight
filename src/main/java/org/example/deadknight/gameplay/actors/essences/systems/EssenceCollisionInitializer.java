package org.example.deadknight.gameplay.actors.essences.systems;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import org.example.deadknight.gameplay.components.HealthComponent;
import org.example.deadknight.gameplay.actors.player.entities.types.EntityType;
import org.example.deadknight.gameplay.components.types.EntityTypeEssences;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getPhysicsWorld;

/**
 * Инициализатор коллизий эссенций.
 * <p>
 * Регистрирует обработчики столкновений между игроком и различными типами эссенций.
 * В текущей реализации:
 * <ul>
 *     <li>При столкновении {@link EntityType#KNIGHT} с {@link EntityTypeEssences#HEALTH_ESSENCE} —
 *     игрок получает лечение.</li>
 *     <li>Значение здоровья увеличивается на величину, хранящуюся в параметре "healAmount" сущности эссенции.</li>
 *     <li>Здоровье не превышает максимального значения из {@link HealthComponent}.</li>
 *     <li>Сущность эссенции удаляется из игрового мира.</li>
 * </ul>
 * </p>
 */
public class EssenceCollisionInitializer {

    /**
     * Регистрирует все обработчики коллизий для эссенций в текущем {@code PhysicsWorld}.
     * <p>
     * В текущей версии:
     * <ul>
     *     <li>Рыцарь ({@link EntityType#KNIGHT}) может подбирать эссенцию здоровья
     *     ({@link EntityTypeEssences#HEALTH_ESSENCE}).</li>
     *     <li>При подборе здоровье увеличивается, но не выше максимального значения.</li>
     *     <li>Эссенция после столкновения удаляется из игрового мира.</li>
     * </ul>
     * </p>
     */
    public void init() {
        getPhysicsWorld().addCollisionHandler(
                new CollisionHandler(EntityType.KNIGHT, EntityTypeEssences.HEALTH_ESSENCE) {
                    @Override
                    protected void onCollisionBegin(Entity player, Entity essence) {
                        HealthComponent health = player.getComponent(HealthComponent.class);
                        int healAmount = essence.getInt("healAmount");

                        int oldHealth = health.getValue();
                        int newHealth = Math.min(oldHealth + healAmount, health.getMaxValue());
                        health.valueProperty().set(newHealth);

                        essence.removeFromWorld();
                    }
                }
        );
    }
}
