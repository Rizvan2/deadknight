package org.example.deadknight.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import org.example.deadknight.types.EntityType;

public class SeparationComponent extends Component {
    private final double minDistance;
    private final double pushFactor;

    public SeparationComponent(double minDistance, double pushFactor) {
        this.minDistance = minDistance;
        this.pushFactor = pushFactor;
    }

    @Override
    public void onUpdate(double tpf) {
        FXGL.getGameWorld().getEntitiesByType(EntityType.HOSTILE_MOB)
            .stream()
            .filter(e -> e != entity)
            .forEach(e -> {
                Point2D diff = entity.getCenter().subtract(e.getCenter());
                double dist = diff.magnitude();
                if (dist < minDistance && dist > 0) {
                    entity.translate(diff.normalize().multiply((minDistance - dist) * tpf * pushFactor));
                }
            });
    }
}
