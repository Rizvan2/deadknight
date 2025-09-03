package org.example.deadknight.gameplay.actors.player.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import org.example.deadknight.gameplay.actors.player.entities.IlyasPantherEntity;
import org.example.deadknight.gameplay.actors.player.services.AnimationService;
import org.example.deadknight.gameplay.actors.player.services.PantherAttackService;
import org.example.deadknight.gameplay.actors.player.entities.types.EntityType;

/**
 * Фабрика для создания сущностей пантеры.
 * <p>
 * Отвечает за построение игровой сущности пантеры, установку спрайта, hitbox,
 * свойств и подключения сервисов анимации и атаки.
 */
public class PantherFactory {

    /**
     * Создает игровую сущность пантеры.
     *
     * @param data объект {@link IlyasPantherEntity} с базовыми параметрами пантеры (здоровье, скорость, направление)
     * @param x    координата X для размещения пантеры в мире
     * @param y    координата Y для размещения пантеры в мире
     * @return созданная сущность {@link Entity} пантеры
     */
    public static Entity create(IlyasPantherEntity data, double x, double y) {
        Entity panther = FXGL.entityBuilder()
                .at(x, y)
                .bbox(new HitBox("BODY", BoundingShape.box(64, 64)))
                .with(data.getHealth())
                .with(data.getSpeed())
                .type(EntityType.PANTHER)
                .zIndex(100)
                .build();

        initProperties(panther, data);

        return panther;
    }

    /**
     * Инициализирует свойства сущности пантеры.
     * <p>
     * Устанавливает флаги атаки и движения, направление, спрайт и подключает
     * анимацию ходьбы и метод атаки.
     *
     * @param panther сущность пантеры {@link Entity}
     * @param data    объект {@link IlyasPantherEntity} с параметрами
     */
    private static void initProperties(Entity panther, IlyasPantherEntity data) {
        panther.getProperties().setValue("isAttacking", false);
        panther.getProperties().setValue("moving", false);
        panther.getProperties().setValue("direction", data.getDirection());
        panther.getProperties().setValue("spriteDir", data.getDirection());
        panther.getProperties().setValue("shootDir", data.getDirection());

        // Анимация ходьбы (один кадр по умолчанию)
        String[] frames = {"panter1.png"};
        AnimationService.attach(panther, frames);

        // Метод атаки: запускается через Runnable
        panther.getProperties().setValue("playAttack", (Runnable) () ->
                PantherAttackService.playAttack(panther, "panter_attack.png", 0.3, 150)
        );
    }
}
