package org.example.deadknight.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;


public class EnemyComponent extends Component {

    private double elapsed = 0;

    @Override
    public void onUpdate(double tpf) {
        Entity player = FXGL.getGameWorld()
                .getEntities()
                .stream()
                .filter(e -> e.getProperties().exists("isPlayer") && e.getProperties().getBoolean("isPlayer"))
                .findFirst()
                .orElse(null);

        if (player != null) {
            elapsed += tpf;

            double maxSpeed = entity.getProperties().exists("speed")
                    ? entity.getProperties().getDouble("speed")
                    : 50;

            // линейно увеличиваем скорость первые 0.5 сек
            double factor = Math.min(1, elapsed / 2);
            double effectiveSpeed = maxSpeed * factor;

            Point2D direction = player.getPosition().subtract(entity.getPosition()).normalize();
            entity.translate(direction.multiply(effectiveSpeed * tpf));
        }
    }


}
