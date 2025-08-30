package org.example.deadknight.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import java.util.List;
import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Компонент для магической волны, выпускаемой рыцарем.
 * Волна движется в заданном направлении, наносит урон
 * сущностям с флагом "canTakeDamage" и отталкивает их,
 * используя PushComponent.
 */
public class WaveComponent extends Component {

    /** Направление движения волны */
    private final Point2D direction;

    /** Скорость движения волны в пикселях/сек */
    private final double speed = 500;

    /** Урон, наносимый волной */
    private final int damage = 20;

    /**
     * Создает компонент волны.
     *
     * @param direction Направление движения волны (единичный вектор)
     */
    public WaveComponent(Point2D direction) {
        this.direction = direction;
    }

    @Override
    public void onUpdate(double tpf) {
        // Двигаем волну
        entity.translate(direction.multiply(speed * tpf));

        // Получаем список сущностей, которые можно повредить
        List<Entity> toDamage = getGameWorld().getEntities()
                .stream()
                .filter(e -> e != null)
                .filter(e -> e.isColliding(entity))
                .filter(e -> {
                    try {
                        Object canTakeDamageObj = e.getProperties().getValue("canTakeDamage");
                        return canTakeDamageObj instanceof Boolean && (Boolean) canTakeDamageObj;
                    } catch (Exception ignored) {
                        return false;
                    }
                })
                .toList();

        // Применяем урон и отталкивание через PushComponent
        for (Entity e : toDamage) {
            try {
                HealthComponent health = e.getComponent(HealthComponent.class);
                if (health != null) {
                    health.takeDamage(damage);

                    // Берем силу отталкивания из сущности
                    double pushStrength = 0; // дефолт
                    try {
                        Object prop = e.getProperties().getValue("wavePushStrength");
                        if (prop instanceof Number) pushStrength = ((Number) prop).doubleValue();
                    } catch (Exception ignored) {}

                    // Используем PushComponent для плавного отталкивания
                    PushComponent push = e.getComponentOptional(PushComponent.class).orElse(null);
                    if (push != null) {
                        push.addImpulse(direction.multiply(pushStrength));
                    }

                    // Волна исчезает после первого попадания
                    entity.removeFromWorld();
                    break; // чтобы не бить всех сразу
                }
            } catch (Exception ignored) {}
        }
    }
}
