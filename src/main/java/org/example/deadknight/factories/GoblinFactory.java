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
import org.example.deadknight.components.*;
import org.example.deadknight.entities.GoblinEntity;
import org.example.deadknight.types.EntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * Фабрика создания гоблинов для игры.
 * <p>
 * Данный класс отвечает за генерацию враждебных мобов-гоблинов, с настройкой:
 * <ul>
 *     <li>AI поведения через {@link EnemyComponent}</li>
 *     <li>Здоровья через {@link HealthComponent} с хитбаром</li>
 *     <li>Разделения для предотвращения наложения через {@link SeparationComponent}</li>
 *     <li>Физических коллизий через {@link HitBox}</li>
 *     <li>Визуализации через {@link ImageView} с анимацией</li>
 * </ul>
 * <p>
 * Использует аннотацию {@link Spawns} для регистрации типа "goblin" в FXGL.
 */
public class GoblinFactory implements EntityFactory {

    @Spawns("goblin")
    public Entity newGoblin(SpawnData data) {
        int health = getHealthFromData(data);
        List<Image> walkFrames = loadWalkFrames();
        List<Image> attackFrames = loadAttackFrames();

        GoblinEntity goblinData = new GoblinEntity(100, 10, walkFrames, attackFrames);
        ImageView goblinView = createGoblinView(walkFrames.get(0));

        Entity goblin = buildGoblinEntity(data, goblinData, goblinView, health);

        attachHealthBar(goblin);

        goblin.getProperties().setValue("canTakeDamage", true);
        goblin.getProperties().setValue("speed", 50.0);
        goblin.getProperties().setValue("pushStrength", 2000); // сила отталкивания


        return goblin;
    }

    private int getHealthFromData(SpawnData data) {
        return data.getData().containsKey("health") ? (int) data.get("health") : 50;
    }

    private List<Image> loadWalkFrames() {
        List<Image> frames = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            frames.add(FXGL.image("goblin-" + i + ".png"));
        }
        return frames;
    }

    private List<Image> loadAttackFrames() {
        List<Image> frames = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            frames.add(FXGL.image("goblin_attack-" + i + ".png"));
        }
        return frames;
    }

    private ImageView createGoblinView(Image firstFrame) {
        ImageView view = new ImageView(firstFrame);
        view.setFitWidth(110);
        view.setFitHeight(110);
        view.setPreserveRatio(true);
        return view;
    }

    private Entity buildGoblinEntity(SpawnData data, GoblinEntity goblinData, ImageView view, int health) {
        return FXGL.entityBuilder(data)
                .type(EntityType.HOSTILE_MOB)
                .view(view)
                .bbox(new HitBox("BODY", new Point2D(40, 90), BoundingShape.box(20, 30)))
                .with(new EnemyComponent(goblinData))
                .with(new HealthComponent(health))
                .with(new SeparationComponent(50, 0.5))
                .with(new PushComponent())
                .collidable()
                .build();
    }

    private void attachHealthBar(Entity goblin) {
        Rectangle healthBar = new Rectangle(40, 5, Color.LIME);
        healthBar.setTranslateX(25);
        healthBar.setTranslateY(40);
        goblin.getViewComponent().addChild(healthBar);

        HealthComponent healthComp = goblin.getComponent(HealthComponent.class);
        healthComp.valueProperty().addListener((obs, oldVal, newVal) -> {
            double percent = newVal.doubleValue() / healthComp.getMaxValue();
            healthBar.setWidth(40 * percent);
        });
    }
}
