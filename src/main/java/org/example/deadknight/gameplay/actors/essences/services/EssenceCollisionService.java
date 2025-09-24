package org.example.deadknight.gameplay.actors.essences.services;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import org.example.deadknight.gameplay.actors.player.entities.types.EntityType;
import com.almasb.fxgl.physics.PhysicsWorld;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Глобальный сервис для регистрации всех коллизий эссенций.
 * <p>
 * Позволяет централизованно обрабатывать подбор эссенции и удалять её из мира.
 */
public class EssenceCollisionService {

    /** Ссылка на физический мир FXGL, где происходят коллизии. */
    private final PhysicsWorld physicsWorld;

    /** Сервис для обработки самой сущности: удаление из мира и базовые действия. */
    private final EssenceService essenceService = new EssenceService();

    /**
     * Создаёт сервис для регистрации коллизий в указанном физическом мире.
     *
     * @param physicsWorld физический мир, где будут обрабатываться коллизии
     */
    public EssenceCollisionService(PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
    }

    /**
     * Регистрирует коллизию между игроком и эссенцией.
     *
     * @param essenceType тип эссенции (любой Enum)
     * @param effect      действие, которое выполняется при подборе
     */
    public void registerCollision(Enum<?> essenceType, BiConsumer<Entity, Entity> effect) {
        physicsWorld.addCollisionHandler(new CollisionHandler(EntityType.KNIGHT, essenceType) {
            @Override
            protected void onCollisionBegin(Entity player, Entity essence) {
                essenceService.handleEssence(player, essence, () -> effect.accept(player, essence));
            }
        });
    }
}
