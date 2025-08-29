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
import org.example.deadknight.components.*;
import org.example.deadknight.entities.GoblinEntity;
import org.example.deadknight.types.EntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * Фабрика создания игровых сущностей для FXGL.
 * <p>
 * Отвечает за спавн игрока и враждебных мобов. В частности, здесь реализован метод
 * {@link #newGoblin(SpawnData)} для создания гоблина с анимацией, жизнью и поведением.
 * <p>
 * Использует аннотацию {@link Spawns} для регистрации типов сущностей и их спавна через FXGL.
 * <p>
 * Каждая сущность гоблина содержит:
 * <ul>
 *     <li>{@link EnemyComponent} — AI для движения к игроку и атаки.</li>
 *     <li>{@link HealthComponent} — управление здоровьем.</li>
 *     <li>{@link SeparationComponent} — предотвращает наслаивание гоблинов друг на друга.</li>
 *     <li>HitBox для физического взаимодействия и коллизий.</li>
 *     <li>ImageView с первой анимацией для визуализации сущности.</li>
 * </ul>
 */
public class MobAndPlayerFactory implements EntityFactory {

    /**
     * Создаёт сущность гоблина.
     *
     * @param data данные спавна {@link SpawnData}, могут содержать:
     *             <ul>
     *                 <li>ключ "health" для установки начального здоровья гоблина</li>
     *             </ul>
     * @return {@link Entity} — готовая сущность гоблина с компонентами поведения, здоровья и separation.
     */
    @Spawns("goblin")
    public Entity newGoblin(SpawnData data) {

        int health = data.getData().containsKey("health") ? (int) data.get("health") : 50;

        // Загружаем кадры для анимации ходьбы
        List<Image> walkFrames = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            walkFrames.add(FXGL.image("goblin-" + i + ".png"));
        }

        // Загружаем кадры для анимации атаки
        List<Image> attackFrames = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            attackFrames.add(FXGL.image("goblin_attack-" + i + ".png"));
        }

        // Создаём объект данных гоблина
        GoblinEntity goblinData = new GoblinEntity(50, 10, walkFrames, attackFrames);

        // Создаём ImageView для визуализации сущности
        ImageView goblinView = new ImageView(walkFrames.get(0));
        goblinView.setFitWidth(110);
        goblinView.setFitHeight(110);
        goblinView.setPreserveRatio(true);

        // Создаём сущность с компонентами
        Entity goblin = FXGL.entityBuilder(data)
                .type(EntityType.HOSTILE_MOB)
                .view(goblinView)
                .bbox(new HitBox("BODY", new Point2D(40, 90), BoundingShape.box(20, 30)))
                .with(new EnemyComponent(goblinData))
                .with(new HealthComponent(health))
                .with(new SeparationComponent(50, 0.5))// предотвращение наслаивания гоблинов
                .collidable()
                .build();


        // Устанавливаем свойства сущности
        goblin.getProperties().setValue("canTakeDamage", true);
        goblin.getProperties().setValue("speed", 50.0);

        return goblin;
    }
}
