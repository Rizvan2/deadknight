package org.example.deadknight.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.image.ImageView;
import org.example.deadknight.entities.KnightEntity;
import org.example.deadknight.services.AnimationService;
import org.example.deadknight.types.EntityType;

/**
 * Factory-класс для создания сущности рыцаря в игре.
 * <p>
 * Используется для инициализации визуального представления рыцаря,
 * его физических границ (hitbox), компонентов состояния (здоровье, скорость)
 * и подключения анимаций.
 */
public class KnightFactory {

    /**
     * Создаёт нового рыцаря (Entity) на указанных координатах.
     *
     * @param knightData Объект {@link KnightEntity}, содержащий данные о здоровье, скорости и начальном направлении.
     * @param x          Координата X для размещения рыцаря в игровом мире.
     * @param y          Координата Y для размещения рыцаря в игровом мире.
     * @return {@link Entity} — полностью инициализированный рыцарь для добавления в игровой мир.
     */
    public static Entity create(KnightEntity knightData, double x, double y) {
        ImageView knightSprite = new ImageView(FXGL.image("knight_left-1.png"));
        knightSprite.setFitWidth(64);
        knightSprite.setFitHeight(64);

        Entity knight = FXGL.entityBuilder()
                .at(x, y)
                .view(knightSprite)
                .bbox(new HitBox("BODY", BoundingShape.box(44, 44)))
                .with(knightData.getHealth())
                .with(knightData.getSpeed())
                .type(EntityType.KNIGHT)
                .zIndex(100) // <--- рыцарь будет поверх объектов с меньшим zIndex
                .build();

        knight.getProperties().setValue("moving", false);
        knight.getProperties().setValue("direction", knightData.getDirection());
        knight.getProperties().setValue("spriteDir", knightData.getDirection());
        knight.getProperties().setValue("shootDir", knightData.getDirection());

        // Универсальный сервис анимации
        String[] frames = {
                "knight_left-1.png",
                "knight_left-2.png",
                "knight_left-3.png",
                "knight_left-4.png",
                "knight_left-5.png"
        };
        AnimationService.attach(knight, frames);

        return knight;
    }
}
