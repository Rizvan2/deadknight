package org.example.deadknight.gameplay.actors.player.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import org.example.deadknight.gameplay.actors.player.dialog.DialogueSequenceComponent;
import org.example.deadknight.gameplay.actors.player.dialog.PlayerDialogueComponent;
import org.example.deadknight.gameplay.actors.player.entities.KnightEntity;
import org.example.deadknight.gameplay.actors.player.services.AnimationService;
import org.example.deadknight.gameplay.actors.player.entities.types.EntityType;
import org.example.deadknight.gameplay.components.DialogueComponent;
import org.example.deadknight.gameplay.components.debug.KnightDebugHitBoxComponent;
import org.example.deadknight.gameplay.components.SeparationComponent;
import org.example.deadknight.gameplay.components.UpgradeComponent;

/**
 * Фабрика для создания сущностей рыцаря.
 * <p>
 * Отвечает за построение игровой сущности рыцаря, установку спрайта, hitbox,
 * свойств и подключения сервисов анимации и атаки.
 */
public class KnightFactory {

    /**
     * Создает игровую сущность рыцаря.
     *
     * @param knightData объект {@link KnightEntity} с базовыми параметрами рыцаря (здоровье, скорость, направление)
     * @param x          координата X для размещения рыцаря в мире
     * @param y          координата Y для размещения рыцаря в мире
     * @return созданная сущность {@link Entity} рыцаря
     */
    public static Entity create(KnightEntity knightData, double x, double y) {
        Entity knight = FXGL.entityBuilder()
                .at(x, y)
                .bbox(new HitBox("BODY", new Point2D(40, 25), BoundingShape.box(10, 50)))
                .with(new KnightDebugHitBoxComponent())
                .with(knightData.getHealth())
                .with(knightData.getSpeedComponent()) // компонент скорости, метод возвращает SpeedComponent
                .with(new SeparationComponent(50, 2))
                .with(new DialogueComponent())
                .with(new DialogueSequenceComponent())
                .with(new PlayerDialogueComponent())
                .with(new UpgradeComponent())
                .type(EntityType.KNIGHT)
                .zIndex(100)
                .collidable()   // Дает возможность подбирать сферы здоровья
                .build();

        initProperties(knight, knightData);

        return knight;
    }

    /**
     * Инициализирует свойства сущности рыцаря.
     * <p>
     * Устанавливает флаги атаки и движения, направление, спрайт и подключает
     * анимацию ходьбы и метод атаки.
     *
     * @param knight     сущность рыцаря {@link Entity}
     * @param knightData объект {@link KnightEntity} с параметрами
     */
    private static void initProperties(Entity knight, KnightEntity knightData) {
        knight.getProperties().setValue("isAttacking", false);
        knight.getProperties().setValue("moving", false);
        // Дополнительная метка
        knight.getProperties().setValue("isPlayer", true);
        knight.getProperties().setValue("direction", knightData.getDirection());
        knight.getProperties().setValue("spriteDir", knightData.getDirection());
        knight.getProperties().setValue("shootDir", knightData.getDirection());
//        // Подключаем анимацию ходьбы
//        String[] frames = {
//                "knight/knight_left-1.png",
//                "knight/knight_left-2.png",
//                "knight/knight_left-3.png",
//                "knight/knight_left-4.png",
//                "knight/knight_left-5.png"
//        };
//        AnimationService.attach(knight, frames);



        // Подключаем анимацию ходьбы из спрайт-листов цепной брони
        String[] sheets = {
                "knight/chain/spritesheet-1.png",
                "knight/chain/spritesheet-2.png",
                "knight/chain/spritesheet-3.png",
                "knight/chain/spritesheet-4.png"
        };
        // 5x5 кадров на листе 720x720 => размер кадра 144x144, последовательно по листам
        AnimationService.attachFromSpritesheets(knight, sheets, 512, 512);

        // Метод атаки: запускается через Runnable
        knight.getProperties().setValue("playAttack", (Runnable) () ->
                AnimationService.playAttack(knight, "knight/knight_attack.png", 0.25)
        );
    }
}
