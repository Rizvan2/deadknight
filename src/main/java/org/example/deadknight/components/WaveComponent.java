package org.example.deadknight.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import java.util.List;
import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Компонент для магической волны, выпускаемой рыцарем.
 * Волна движется в заданном направлении, наносит урон
 * сущностям с флагом "canTakeDamage" и исчезает после первого удара.
 */
public class WaveComponent extends Component {

    /** Направление движения волны */
    private final Point2D direction;

    /** Скорость движения волны в пикселях/сек */
    private final double speed = 500;

    /** Урон, наносимый волной при попадании */
    private final int damage = 20;

    /**
     * Создает компонент волны.
     *
     * @param direction Направление движения волны (единичный вектор)
     */
    public WaveComponent(Point2D direction) {
        this.direction = direction;
    }

    /**
     * Обновляет состояние волны:
     * <ul>
     *   <li>Двигает волну по направлению</li>
     *   <li>Проверяет столкновения с другими сущностями</li>
     *   <li>Наносит урон и удаляет волну после первого попадания</li>
     * </ul>
     *
     * @param tpf Время, прошедшее с предыдущего кадра (Time Per Frame)
     */
    @Override
    public void onUpdate(double tpf) {
        // Двигаем волну
        entity.translate(direction.multiply(speed * tpf));

        // Список сущностей, которые можно повредить
        List<Entity> toDamage = getGameWorld().getEntities()
                .stream()
                .filter(e -> e != null)
                .filter(e -> e.isColliding(entity))
                .filter(e -> {
                    Object canTakeDamageObj = null;
                    try {
                        canTakeDamageObj = e.getProperties().getValue("canTakeDamage");
                    } catch (Exception ignored) {}
                    return canTakeDamageObj instanceof Boolean && (Boolean) canTakeDamageObj;
                })
                .toList(); // если Java <16: collect(Collectors.toList())

        // Применяем урон и удаляем волну после попадания
        for (Entity e : toDamage) {
            try {
                HealthComponent health = e.getComponent(HealthComponent.class);
                if (health != null) {
                    health.takeDamage(damage);
                    entity.removeFromWorld();
                }
            } catch (Exception ignored) {}
        }
    }
}
