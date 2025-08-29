package org.example.deadknight.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.deadknight.components.EnemyComponent;
import org.example.deadknight.components.HealthComponent;
import org.example.deadknight.entities.GoblinEntity;
import org.example.deadknight.types.EntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * Фабрика создания игровых сущностей.
 * <p>
 * Отвечает за создание мобов и игрока в мире FXGL.
 * Используется аннотация {@link Spawns} для регистрации типов сущностей.
 */
public class MobAndPlayerFactory implements EntityFactory {

    /**
     * Создаёт сущность гоблина.
     * <p>
     * Загружает кадры анимации ходьбы и атаки, создаёт объект {@link GoblinEntity},
     * прикрепляет {@link EnemyComponent} и {@link HealthComponent}.
     *
     * @param data данные спавна {@link SpawnData}, могут содержать параметр "health"
     * @return готовая сущность гоблина {@link Entity}
     */
    @Spawns("goblin")
    public Entity newGoblin(SpawnData data) {
        int health = data.getData().containsKey("health") ? (int) data.get("health") : 50;

        // Загружаем кадры для ходьбы
        List<Image> walkFrames = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            walkFrames.add(FXGL.image("goblin-" + i + ".png"));
        }

        // Загружаем кадры для атаки
        List<Image> attackFrames = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            attackFrames.add(FXGL.image("goblin_attack-" + i + ".png"));
        }

        // Создаём объект данных гоблина
        GoblinEntity goblinData = new GoblinEntity(50, 10, walkFrames, attackFrames);

        // Создаём ImageView с первым кадром
        ImageView goblinView = new ImageView(walkFrames.get(0));
        goblinView.setFitWidth(110);
        goblinView.setFitHeight(110);
        goblinView.setPreserveRatio(true);

        // Создаём сущность
        Entity goblin = FXGL.entityBuilder(data)
                .type(EntityType.HOSTILE_MOB)
                .view(goblinView)
                .bbox(new HitBox("BODY", new Point2D(40, 70), BoundingShape.box(20, 30)))

                .with(new EnemyComponent(goblinData))
                .with(new HealthComponent(health))
                .collidable()
                .build();

        // Свойства сущности
        goblin.getProperties().setValue("canTakeDamage", true);
        goblin.getProperties().setValue("speed", 50.0);

        return goblin;
    }
}
