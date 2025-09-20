package org.example.deadknight.gameplay.actors.essences.factory;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import org.example.deadknight.gameplay.components.types.EntityTypeEssences;

/**
 * Фабрика для создания игровых сущностей-эссенций (Essences).
 * <p>
 * Поддерживаются два типа:
 * <ul>
 *   <li>{@code healthEssence} — восстанавливает здоровье игроку.</li>
 *   <li>{@code upgradeEssence} — усиливает урон игрока.</li>
 * </ul>
 * <p>
 * Все параметры (размеры текстур, хитбоксов, значения эффектов) вынесены в константы.
 */
public class EssenceFactory implements EntityFactory {

    // --- Константы для healthEssence ---

    /** Размер текстуры HealthEssence (в пикселях). */
    private static final int HEALTH_TEXTURE_SIZE = 64;

    /** Размер хитбокса HealthEssence (в пикселях). */
    private static final int HEALTH_HITBOX_SIZE = 32;

    /** Количество здоровья, восстанавливаемое HealthEssence. */
    private static final int HEAL_AMOUNT = 20;

    /** Путь к текстуре HealthEssence. */
    private static final String HEALTH_TEXTURE = "essences/life/LifeEssence-1.png";

    // --- Константы для upgradeEssence ---

    /** Размер текстуры UpgradeEssence (в пикселях). */
    private static final int UPGRADE_TEXTURE_SIZE = 32;

    /** Размер хитбокса UpgradeEssence (в пикселях). */
    private static final int UPGRADE_HITBOX_SIZE = 32;

    /** Урон, который добавляется игроку при сборе UpgradeEssence. */
    private static final int DAMAGE_AMOUNT = 50;

    /** Путь к текстуре UpgradeEssence. */
    private static final String UPGRADE_TEXTURE = "essences/upgradeEssence/eclipse_of_forgotten_souls.png";


    /**
     * Создаёт сущность {@code healthEssence}, которая восстанавливает здоровье игроку.
     *
     * @param data данные о позиции и параметрах спавна
     * @return готовая сущность {@link Entity}
     */
    @Spawns("healthEssence")
    public Entity newHealthEssence(SpawnData data) {
        return buildEssence(
                data,
                EntityTypeEssences.HEALTH_ESSENCE,
                HEALTH_TEXTURE,
                HEALTH_TEXTURE_SIZE,
                HEALTH_HITBOX_SIZE,
                "healAmount",
                HEAL_AMOUNT
        );
    }

    /**
     * Создаёт сущность {@code upgradeEssence}, которая усиливает урон игрока.
     *
     * @param data данные о позиции и параметрах спавна
     * @return готовая сущность {@link Entity}
     */
    @Spawns("upgradeEssence")
    public Entity newUpgradeEssence(SpawnData data) {
        return buildEssence(
                data,
                EntityTypeEssences.UPGRADE_ESSENCE,
                UPGRADE_TEXTURE,
                UPGRADE_TEXTURE_SIZE,
                UPGRADE_HITBOX_SIZE,
                "damageAmount",
                DAMAGE_AMOUNT
        );
    }

    /**
     * Универсальный метод для построения эссенций.
     *
     * @param data       данные спавна
     * @param type       тип сущности
     * @param texture    путь к текстуре
     * @param texSize    размер текстуры (ширина и высота)
     * @param hitboxSize размер хитбокса
     * @param property   имя свойства (например, "healAmount")
     * @param value      значение свойства
     * @return готовая сущность
     */
    private Entity buildEssence(
            SpawnData data,
            EntityTypeEssences type,
            String texture,
            int texSize,
            int hitboxSize,
            String property,
            int value
    ) {
        return FXGL.entityBuilder(data)
                .type(type)
                .view(FXGL.texture(texture, texSize, texSize))
                .bbox(new HitBox("BODY", BoundingShape.box(hitboxSize, hitboxSize)))
                .with(property, value)
                .collidable()
                .build();
    }
}
