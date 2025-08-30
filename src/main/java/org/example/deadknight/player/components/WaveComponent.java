package org.example.deadknight.player.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;
import org.example.deadknight.components.HealthComponent;
import org.example.deadknight.mobs.components.PushComponent;

import java.util.List;
import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Компонент для магической волны, выпускаемой игроком (например, рыцарем).
 * <p>
 * Волна движется в заданном направлении, наносит урон сущностям с флагом {@code "canTakeDamage"}
 * и отталкивает их с использованием {@link PushComponent}. Сила отталкивания каждой
 * сущности может задаваться отдельным свойством {@code "wavePushStrength"}.
 */
@Getter
@Setter
public class WaveComponent extends Component {

    /** Направление движения волны (единичный вектор) */
    private final Point2D direction;

    /** Скорость движения волны в пикселях в секунду */
    private final double speed = 500;

    /** Урон, наносимый волной */
    private final int damage = 20;

    /**
     * Создает компонент волны с заданным направлением движения.
     *
     * @param direction направление движения волны (единичный вектор)
     */
    public WaveComponent(Point2D direction) {
        this.direction = direction;
    }

    /**
     * Вызывается каждый кадр игры.
     * <p>
     * Основная логика включает:
     * <ul>
     *     <li>Перемещение волны</li>
     *     <li>Проверку столкновений с сущностями</li>
     *     <li>Нанесение урона и отталкивание через {@link PushComponent}</li>
     *     <li>Удаление волны после первого попадания</li>
     * </ul>
     *
     * @param tpf время, прошедшее с последнего кадра (Time Per Frame)
     */
    @Override
    public void onUpdate(double tpf) {
        moveWave(tpf);
        handleCollisions();
    }

    /**
     * Перемещает волну в направлении {@link #direction} с учётом {@link #speed} и времени кадра.
     *
     * @param tpf время, прошедшее с последнего кадра
     */
    private void moveWave(double tpf) {
        entity.translate(direction.multiply(speed * tpf));
    }

    /**
     * Обрабатывает столкновения волны с сущностями.
     * <p>
     * Волна наносит урон первой столкнувшейся сущности и отталкивает её,
     * после чего удаляется из мира.
     */
    private void handleCollisions() {
        List<Entity> collidingEntities = getCollidingEntities();
        for (Entity e : collidingEntities) {
            applyDamageAndPush(e);
            removeWave();
            break; // волна исчезает после первого попадания
        }
    }

    /**
     * Возвращает список сущностей, которые сталкиваются с волной
     * и могут получать урон.
     *
     * @return список сущностей для нанесения урона
     */
    private List<Entity> getCollidingEntities() {
        return getGameWorld().getEntities().stream()
                .filter(e -> e != null)
                .filter(e -> e.isColliding(entity))
                .filter(this::canTakeDamage)
                .toList();
    }

    /**
     * Проверяет, может ли сущность получать урон.
     * <p>
     * Проверяет наличие свойства {@code "canTakeDamage"} и его значение.
     *
     * @param e сущность
     * @return true, если сущность может получать урон, иначе false
     */
    private boolean canTakeDamage(Entity e) {
        return e.getProperties()
                .getValueOptional("canTakeDamage")
                .map(v -> v instanceof Boolean && (Boolean) v)
                .orElse(false);
    }

    /**
     * Наносит урон и отталкивает сущность при попадании волны.
     *
     * @param e сущность для обработки
     */
    private void applyDamageAndPush(Entity e) {
        HealthComponent health = e.getComponent(HealthComponent.class);
        if (health == null) return;

        health.takeDamage(damage);

        double pushStrength = getPushStrength(e);
        e.getComponentOptional(PushComponent.class)
                .ifPresent(push -> push.addImpulse(direction.multiply(pushStrength)));
    }

    /**
     * Получает силу отталкивания сущности, заданную в свойстве {@code "wavePushStrength"}.
     *
     * @param e сущность
     * @return сила отталкивания, 0 если свойство отсутствует или некорректное
     */
    private double getPushStrength(Entity e) {
        Object prop = e.getProperties().getValue("wavePushStrength");
        if (prop instanceof Number) return ((Number) prop).doubleValue();
        return 0;
    }

    /**
     * Удаляет волну из игрового мира.
     */
    private void removeWave() {
        entity.removeFromWorld();
    }
}
