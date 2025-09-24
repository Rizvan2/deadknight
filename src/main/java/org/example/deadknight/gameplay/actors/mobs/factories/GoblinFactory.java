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
import org.example.deadknight.gameplay.actors.mobs.components.DropComponent;
import org.example.deadknight.infrastructure.assets.GoblinAnimationLoader;
import org.example.deadknight.gameplay.components.*;
import org.example.deadknight.gameplay.actors.mobs.components.EnemyComponent;
import org.example.deadknight.gameplay.components.PushComponent;
import org.example.deadknight.gameplay.components.SeparationComponent;
import org.example.deadknight.gameplay.actors.mobs.entities.GoblinEntity;
import org.example.deadknight.gameplay.actors.mobs.entities.types.EntityType;
import org.example.deadknight.gameplay.components.debug.DebugHitBoxComponent;
import org.example.deadknight.gameplay.services.LootService;

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

    /** Размер гоблина (ширина и высота спрайта) */
    private final int goblinSize = 140;

    private final LootService lootService;

    public GoblinFactory(LootService lootService) {
        this.lootService = lootService;
    }

    /**
     * Создает нового гоблина с заданными параметрами спавна.
     *
     * @param data данные для спавна сущности
     * @return готовая сущность гоблина с анимацией, коллизией и компонентами
     */
    @Spawns("goblin")
    public Entity newGoblin(SpawnData data) {

        GoblinEntity goblinData = createGoblinData();
        ImageView goblinView = createGoblinView(goblinData.getWalkRight()[0]);
        Entity goblin = buildGoblinEntity(data, goblinData, goblinView, goblinData.getHealth());

        attachHealthBar(goblin);

        // Свойства сущности
        goblin.getProperties().setValue("canTakeDamage", true);
        goblin.getProperties().setValue("wavePushStrength", 300); // сила отталкивания от волны

        return goblin;
    }

    /**
     * Создает объект {@link GoblinEntity} с заранее загруженными кадрами анимации.
     *
     * @return объект {@link GoblinEntity} с анимацией ходьбы, атаки и смерти
     */
    private GoblinEntity createGoblinData() {
        GoblinAnimationLoader loader = new GoblinAnimationLoader(goblinSize);
        ImageView[] walkRight = loader.loadWalkRight();
        ImageView[] walkLeft  = loader.loadWalkLeft();
        ImageView[] attackRight = loader.loadAttackRight();
        ImageView[] attackLeft  = loader.loadAttackLeft();
        ImageView[] deathFrames  = loader.loadDeathFrames();

        return new GoblinEntity(
                100,  // скорость
                10,   // урон
                50,
                walkRight,
                walkLeft,
                attackRight,
                attackLeft,
                deathFrames
        );
    }

    /**
     * Строит сущность гоблина с необходимыми компонентами и коллизией.
     *
     * @param data данные спавна
     * @param goblinData данные гоблина (скорость, кадры анимации и т.д.)
     * @param view первый кадр спрайта гоблина
     * @param health количество здоровья гоблина
     * @return готовая сущность {@link Entity} с компонентами
     */
    private Entity buildGoblinEntity(SpawnData data, GoblinEntity goblinData, ImageView view, int health) {
        DropComponent drop = new DropComponent("goblin_basic", lootService);

        return FXGL.entityBuilder(data)
                .type(EntityType.HOSTILE_MOB)
                .view(view)
                .bbox(new HitBox("BODY", new Point2D(65, 80), BoundingShape.box(10, 30)))
                .with(new HealthComponent(health))
                .with(new EnemyComponent(goblinData))
                .with(new SeparationComponent(50, 0.5))
                .with(new PushComponent())
                .with(new DebugHitBoxComponent())
                .with(drop)  // компонент дропа
                .collidable()
                .build();
    }

    /**
     * Предзагружает текстуры анимаций гоблина в отдельном потоке.
     * <p>
     * После завершения загрузки вызывается {@code onComplete} в потоке JavaFX.
     *
     * @param onComplete действие, выполняемое после предзагрузки
     */
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
     * Добавляет хитбар для отображения здоровья сущности.
     *
     * @param goblin сущность гоблина
     */
    private void attachHealthBar(Entity goblin) {
        Rectangle healthBar = new Rectangle(40, 5, Color.LIME);
        healthBar.setTranslateX(45); // правее
        healthBar.setTranslateY(50); // левее
        goblin.getViewComponent().addChild(healthBar);

        HealthComponent healthComp = goblin.getComponent(HealthComponent.class);
        healthComp.valueProperty().addListener((obs, oldVal, newVal) -> {
            double percent = newVal.doubleValue() / healthComp.getMaxValue();
            healthBar.setWidth(40 * percent);
        });
    }
}
