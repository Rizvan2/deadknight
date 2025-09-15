package org.example.deadknight.gameplay.actors.essences.systems;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import org.example.deadknight.gameplay.actors.player.entities.types.EntityType;
import org.example.deadknight.gameplay.components.UpgradeComponent;
import org.example.deadknight.gameplay.components.types.EntityTypeEssences;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getPhysicsWorld;

/**
 * Инициализатор коллизий апгрейд-эссенций.
 * <p>
 * При столкновении игрока с эссенцией апгрейда увеличивает
 * поле {@code upgradeEssenceCount} на 1 и удаляет эссенцию из мира.
 */
public class UpgradeEssenceCollisionInitializer {

    public void init() {
        getPhysicsWorld().addCollisionHandler(
                new CollisionHandler(EntityType.KNIGHT, EntityTypeEssences.UPGRADE_ESSENCE) {
                    @Override
                    protected void onCollisionBegin(Entity player, Entity essence) {
                        UpgradeComponent upgrade = player.getComponent(UpgradeComponent.class);
                        upgrade.increment();

                        essence.removeFromWorld();
                    }
                }
        );
    }
}
