package org.example.deadknight.gameplay.actors.mobs.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.deadknight.gameplay.components.*;
import org.example.deadknight.gameplay.components.AnimationComponent;
import org.example.deadknight.gameplay.components.EnemyComponent;
import org.example.deadknight.gameplay.components.PushComponent;
import org.example.deadknight.gameplay.components.SeparationComponent;
import org.example.deadknight.gameplay.actors.mobs.entities.GoblinEntity;
import org.example.deadknight.gameplay.actors.mobs.entities.types.EntityType;
import org.example.deadknight.gameplay.components.debug.DebugHitBoxComponent;

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

    private final int goblinSize = 140;

    /**
     * Создает нового гоблина с заданными параметрами.
     *
     * @param data данные для спавна сущности
     * @return готовая сущность гоблина
     */
    @Spawns("goblin")
    public Entity newGoblin(SpawnData data) {
        int health = getHealthFromData(data);
        // Загружаем кадры через loader
        GoblinAnimationLoader loader = new GoblinAnimationLoader(goblinSize);
        ImageView[] walkRight = loader.loadWalkRight();
        ImageView[] walkLeft  = loader.loadWalkLeft();
        ImageView[] attackRight = loader.loadAttackRight();
        ImageView[] attackLeft  = loader.loadAttackLeft();
        ImageView[] deathFrames  = loader.loadDeathFrames();

        // Создаем GoblinEntity с готовыми кадрами
        GoblinEntity goblinData = new GoblinEntity(
                100,  // скорость
                10,   // урон
                walkRight,
                walkLeft,
                attackRight,
                attackLeft,
                deathFrames
        );

        ImageView goblinView = createGoblinView(walkRight[0]);

        Entity goblin = buildGoblinEntity(data, goblinData, goblinView, health);

        attachHealthBar(goblin);

        // Свойства сущности
        goblin.getProperties().setValue("canTakeDamage", true);
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
     * Создает {@link ImageView} для визуализации гоблина.
     */
    private ImageView createGoblinView(ImageView firstFrame) {
        ImageView view = new ImageView(firstFrame.getImage());
        view.setFitWidth(firstFrame.getFitWidth());
        view.setFitHeight(firstFrame.getFitHeight());
        view.setSmooth(true);
        return view;
    }

    /**
     * Строит сущность гоблина с необходимыми компонентами и коллизией.
     */
    private Entity buildGoblinEntity(SpawnData data, GoblinEntity goblinData, ImageView view, int health) {

        return FXGL.entityBuilder(data)
                .type(EntityType.HOSTILE_MOB)
                .view(view)
                .bbox(new HitBox("BODY", new Point2D(65, 80), BoundingShape.box(10, 30)))
                .with(new EnemyComponent(goblinData))
                .with(new SpeedComponent(goblinData.getSpeed()))
                .with(new HealthComponent(health))
                .with(new AnimationComponent(goblinData))
                .with(new SeparationComponent(50, 0.5))
                .with(new PushComponent())
                .with(new DebugHitBoxComponent())
                .collidable()
                .build();
    }

    public void preloadGoblinTextures(Runnable onComplete) {
        Thread.startVirtualThread(() -> {
            GoblinAnimationLoader loader = new GoblinAnimationLoader(goblinSize);

            loader.loadWalkRight();
            loader.loadWalkLeft();
            loader.loadAttackRight();
            loader.loadAttackLeft();
            loader.loadDeathFrames();

            Platform.runLater(onComplete);
        });
    }

    /**
     * Добавляет хитбар для отображения здоровья сущности.
     *
     * @param goblin сущность гоблина
     */
    private void attachHealthBar(Entity goblin) {
        Rectangle healthBar = new Rectangle(40, 5, Color.LIME);
// смещаем относительно центра гоблина
        healthBar.setTranslateX(45); // смещаем ещё правее
        healthBar.setTranslateY(50); // ниже, как раньше
        goblin.getViewComponent().addChild(healthBar);

        HealthComponent healthComp = goblin.getComponent(HealthComponent.class);
        healthComp.valueProperty().addListener((obs, oldVal, newVal) -> {
            double percent = newVal.doubleValue() / healthComp.getMaxValue();
            healthBar.setWidth(40 * percent);
        });

    }
}
