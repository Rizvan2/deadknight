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
// Загружаем кадры для каждой анимации и направления
        ImageView[] walkRight = loadWalkFramesRight();
        ImageView[] walkLeft = loadWalkFramesLeft();

        ImageView[] attackRight = loadAttackFramesRight();
        ImageView[] attackLeft = loadAttackFramesLeft();

        ImageView[] deathFrames = loadDeathFrames();

// Создаем GoblinEntity с готовыми ImageView массивами
        GoblinEntity goblinData = new GoblinEntity(
                100,        // скорость
                10,         // урон
                walkRight,  // кадры ходьбы вправо
                walkLeft,   // кадры ходьбы влево
                attackRight,// кадры атаки вправо
                attackLeft, // кадры атаки влево
                deathFrames // кадры смерти
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
     * Загружает кадры анимации ходьбы для гоблина.
     */
    private ImageView[] loadWalkFramesRight() {
        List<ImageView> frames = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            ImageView iv = new ImageView(FXGL.image("goblin/goblin-" + i + ".png"));
            iv.setFitWidth(goblinSize);  // пример, под нужный размер
            iv.setFitHeight(goblinSize);
            frames.add(iv);
        }
        return frames.toArray(new ImageView[0]);
    }

    private ImageView[] loadWalkFramesLeft() {
        ImageView[] rightFrames = loadWalkFramesRight();
        ImageView[] leftFrames = new ImageView[rightFrames.length];
        for (int i = 0; i < rightFrames.length; i++) {
            ImageView iv = new ImageView(rightFrames[i].getImage());
            iv.setFitWidth(goblinSize);
            iv.setFitHeight(goblinSize);
            iv.setScaleX(-1); // зеркалируем для левой стороны
            leftFrames[i] = iv;
        }
        return leftFrames;
    }

    /**
     * Загружает кадры анимации атаки.
     */
    private ImageView[] loadAttackFramesRight() {
        List<ImageView> frames = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            ImageView iv = new ImageView(FXGL.image("goblin/goblin_attack-" + i + ".png"));
            iv.setFitWidth(goblinSize);
            iv.setFitHeight(goblinSize);
            frames.add(iv);
        }
        return frames.toArray(new ImageView[0]);
    }

    private ImageView[] loadAttackFramesLeft() {
        ImageView[] rightFrames = loadAttackFramesRight();
        ImageView[] leftFrames = new ImageView[rightFrames.length];
        for (int i = 0; i < rightFrames.length; i++) {
            ImageView iv = new ImageView(rightFrames[i].getImage());
            iv.setFitWidth(goblinSize);
            iv.setFitHeight(goblinSize);
            iv.setScaleX(-1);
            leftFrames[i] = iv;
        }
        return leftFrames;
    }

    /**
     * Загружает кадры анимации смерти.
     */
    private ImageView[] loadDeathFrames() {
        List<ImageView> frames = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            ImageView iv = new ImageView(FXGL.image("goblin/goblin_death-" + i + ".png"));
            iv.setFitWidth(70);
            iv.setFitHeight(70);
            frames.add(iv);
        }
        // повтор последнего кадра
        ImageView last = frames.get(frames.size() - 1);
        frames.add(new ImageView(last.getImage()));
        return frames.toArray(new ImageView[0]);
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
                .bbox(new HitBox("BODY", new Point2D(70, 80), BoundingShape.box(10, 30)))
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
    /**
     * Предзагружает текстуры анимаций гоблина в отдельном потоке.
     * <p>
     * Метод загружает кадры анимации ходьбы, атаки и смерти, чтобы при
     * спавне гоблина не происходили задержки из-за подгрузки изображений.
     * После завершения загрузки вызывается переданный {@link Runnable} в
     * JavaFX-потоке через {@link Platform#runLater(Runnable)}.
     *
     * @param onComplete действие, которое будет выполнено после завершения предзагрузки
     */
    public void preloadGoblinTextures(Runnable onComplete) {
        Thread.startVirtualThread(() -> {
            loadWalkFramesLeft();
            loadAttackFramesLeft();
            loadDeathFrames();

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
