package org.example.deadknight.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
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

        if (player == null) return;

        elapsed += tpf;

        double maxSpeed = entity.getProperties().exists("speed") ? entity.getProperties().getDouble("speed") : 50;

        // Линейно увеличиваем скорость первые 2 сек
        double factor = Math.min(1, elapsed / 2);
        double effectiveSpeed = maxSpeed * factor;

        Point2D direction = player.getPosition().subtract(entity.getPosition());
        double distance = direction.magnitude();

        if (distance > 1) { // пока не дошёл до рыцаря
            Point2D move = direction.normalize().multiply(effectiveSpeed * tpf);
            entity.translate(move);

            // Смотрим направление
            if (entity.getViewComponent().getChildren().get(0) instanceof javafx.scene.image.ImageView view) {
                view.setScaleX(move.getX() >= 0 ? 1 : -1); // вправо или влево
            }
        }
        // Когда достиг рыцаря, scaleX сохраняется
    }
}
