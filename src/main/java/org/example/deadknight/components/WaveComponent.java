package org.example.deadknight.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import org.example.deadknight.components.HealthComponent;
import org.example.deadknight.exceptions.ComponentNotFoundException;
import org.example.deadknight.types.EntityType;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class WaveComponent extends Component {

    private final Point2D direction;
    private final double speed = 200; // скорость движения
    private final int damage = 20;    // урон волны

    public WaveComponent(Point2D direction) {
        this.direction = direction;
    }

    @Override
    public void onUpdate(double tpf) {
        // двигаем волну
        entity.translate(direction.multiply(speed * tpf));

        // Собираем в отдельный список все сущности, по которым волна должна ударить
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

        // Применяем урон и удаляем волну
        for (Entity e : toDamage) {
            HealthComponent health = null;
            try {
                health = e.getComponent(HealthComponent.class);
            } catch (Exception ignored) {}

            if (health != null) {
                health.takeDamage(damage);
                entity.removeFromWorld(); // волна исчезает после удара
            }
        }
    }

}
