package org.example.deadknight.mobs.factories;

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
import org.example.deadknight.mobs.components.AnimationComponent;
import org.example.deadknight.mobs.components.EnemyComponent;
import org.example.deadknight.mobs.components.PushComponent;
import org.example.deadknight.mobs.components.SeparationComponent;
import org.example.deadknight.mobs.entities.GoblinEntity;
import org.example.deadknight.mobs.entities.types.EntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * Фабрика создания мобов типа "Гоблин".
 * <p>
 * Отвечает за генерацию врагов с преднастроенными компонентами:
 * <ul>
 *     <li>{@link EnemyComponent} — AI поведение.</li>
 *     <li>{@link HealthComponent} — здоровье с визуальным хитбаром.</li>
 *     <li>{@link SeparationComponent} — предотвращение наложения сущностей.</li>
 *     <li>{@link PushComponent} — возможность отталкивания.</li>
 *     <li>Коллизии через {@link HitBox}.</li>
 *     <li>Визуализация через {@link ImageView} с анимацией ходьбы и атаки.</li>
 * </ul>
 * <p>
 * Использует аннотацию {@link Spawns} для регистрации типа "goblin" в FXGL.
 */
public class GoblinFactory implements EntityFactory {

    /**
     * Создает нового гоблина с заданными параметрами.
     *
     * @param data данные для спавна сущности
     * @return готовая сущность гоблина
     */
    @Spawns("goblin")
    public Entity newGoblin(SpawnData data) {
        int health = getHealthFromData(data);
        List<Image> walkFrames = loadWalkFrames();
        List<Image> attackFrames = loadAttackFrames();

        GoblinEntity goblinData = new GoblinEntity(100, 10, walkFrames, attackFrames);
        ImageView goblinView = createGoblinView(walkFrames.get(0));

        Entity goblin = buildGoblinEntity(data, goblinData, goblinView, health);

        attachHealthBar(goblin);

        // Свойства сущности
        goblin.getProperties().setValue("canTakeDamage", true);
        goblin.getProperties().setValue("speed", 50.0);
        goblin.getProperties().setValue("wavePushStrength", 300); // сила отталкивания от волны

        return goblin;
    }

    /**
     * Получает здоровье из данных спавна или возвращает дефолтное.
     */
    private int getHealthFromData(SpawnData data) {
        return data.getData().containsKey("health") ? (int) data.get("health") : 50;
    }

    /**
     * Загружает кадры анимации ходьбы.
     */
    private List<Image> loadWalkFrames() {
        List<Image> frames = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            frames.add(FXGL.image("goblin/goblin-" + i + ".png"));
        }
        return frames;
    }

    /**
     * Загружает кадры анимации атаки.
     */
    private List<Image> loadAttackFrames() {
        List<Image> frames = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            frames.add(FXGL.image("goblin/goblin_attack-" + i + ".png"));
        }
        return frames;
    }

    /**
     * Создает {@link ImageView} для визуализации гоблина.
     */
    private ImageView createGoblinView(Image firstFrame) {
        ImageView view = new ImageView(firstFrame);
        view.setFitWidth(110);
        view.setFitHeight(110);
        view.setPreserveRatio(true);
        return view;
    }

    /**
     * Строит сущность гоблина с необходимыми компонентами и коллизией.
     */
    private Entity buildGoblinEntity(SpawnData data, GoblinEntity goblinData, ImageView view, int health) {
        return FXGL.entityBuilder(data)
                .type(EntityType.HOSTILE_MOB)
                .view(view)
                .bbox(new HitBox("BODY", new Point2D(40, 90), BoundingShape.box(20, 30)))
                .with(new EnemyComponent(goblinData))
                .with(new HealthComponent(health))
                .with(new AnimationComponent(goblinData))
                .with(new SeparationComponent(50, 0.5))
                .with(new PushComponent())
                .collidable()
                .build();
    }

    /**
     * Добавляет хитбар для отображения здоровья сущности.
     *
     * @param goblin сущность гоблина
     */
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
