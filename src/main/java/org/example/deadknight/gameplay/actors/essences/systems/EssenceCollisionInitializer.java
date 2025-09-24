package org.example.deadknight.gameplay.actors.essences.systems;

import com.almasb.fxgl.dsl.FXGL;
import org.example.deadknight.gameplay.actors.essences.services.EssenceCollisionService;
import org.example.deadknight.gameplay.components.HealthComponent;
import org.example.deadknight.gameplay.components.UpgradeComponent;
import org.example.deadknight.gameplay.components.types.EntityTypeEssences;

/**
 * Инициализатор коллизий для всех игровых эссенций.
 * <p>
 * Регистрирует обработчики столкновений между игроком ({@code EntityType.KNIGHT})
 * и различными типами эссенций:
 * <ul>
 *     <li>{@link EntityTypeEssences#HEALTH_ESSENCE} — увеличивает здоровье игрока на величину healAmount
 *     из эссенции, но не выше максимального значения.</li>
 *     <li>{@link EntityTypeEssences#UPGRADE_ESSENCE} — увеличивает счётчик апгрейдов
 *     в {@link UpgradeComponent} и удаляет эссенцию из мира.</li>
 * </ul>
 * <p>
 * Позволяет легко добавлять новые типы эссенций с кастомной логикой, регистрируя их через
 * {@link EssenceCollisionService#registerCollision(Enum, java.util.function.BiConsumer)}.
 * </p>
 */
public class EssenceCollisionInitializer {

    /**
     * Регистрирует коллизии для всех эссенций.
     * <p>
     * Для каждой эссенции задаётся конкретный эффект на игрока.
     * После применения эффекта сущность автоматически удаляется из игрового мира.
     */
    public void init() {
        EssenceCollisionService collisionService =
                new EssenceCollisionService(FXGL.getPhysicsWorld());

        registerHealthEssence(collisionService);
        registerUpgradeEssence(collisionService);

        // Можно легко добавлять новые эссенции:
        // registerNewEssence(collisionService);
    }

    /** Регистрирует коллизию для эссенции здоровья */
    private void registerHealthEssence(EssenceCollisionService collisionService) {
        collisionService.registerCollision(EntityTypeEssences.HEALTH_ESSENCE, (player, essence) -> {
            HealthComponent health = player.getComponent(HealthComponent.class);
            int healAmount = essence.getInt("healAmount"); // берём из эссенции
            int newHealth = Math.min(health.getValue() + healAmount, health.getMaxValue());
            health.valueProperty().set(newHealth);
        });
    }

    /** Регистрирует коллизию для эссенции апгрейда */
    private void registerUpgradeEssence(EssenceCollisionService collisionService) {
        collisionService.registerCollision(EntityTypeEssences.UPGRADE_ESSENCE, (player, essence) -> {
            UpgradeComponent upgrade = player.getComponent(UpgradeComponent.class);
            upgrade.increment();
        });
    }
}
