package org.example.deadknight.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;


public class EnemyComponent extends Component {

    @Override
    public void onUpdate(double tpf) {
        Entity player = FXGL.getGameWorld()
                .getEntities()
                .stream()
                .filter(e -> e.getProperties().exists("isPlayer") && e.getProperties().getBoolean("isPlayer"))
                .findFirst()
                .orElse(null);

        if (player != null) {
            // берем скорость из свойств сущности, если не задано — дефолт 50
            double speed = entity.getProperties().exists("speed")
                    ? entity.getProperties().getDouble("speed")
                    : 50;

            Point2D direction = player.getPosition().subtract(entity.getPosition()).normalize();
            entity.translate(direction.multiply(speed * tpf));
        }
    }


}
