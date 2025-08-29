package org.example.deadknight.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

/**
 * Двигает моба в заданном направлении с указанной скоростью.
 * Можно управлять как динамически, так и через onUpdate.
 */
public class MovementComponent extends Component {

    private final double speed;

    public MovementComponent(double speed) {
        this.speed = speed;
    }

    /**
     * Двигает моба в указанном направлении.
     *
     * @param direction вектор направления (например, к игроку)
     * @param tpf       время между кадрами
     */
    public void moveTowards(Point2D direction, double tpf) {
        if (direction.magnitude() == 0) return;

        Point2D move = direction.normalize().multiply(speed * tpf);
        entity.translate(move);

        // Меняем масштаб спрайта для направления
        if (!entity.getViewComponent().getChildren().isEmpty()) {
            entity.getViewComponent().getChildren().get(0).setScaleX(move.getX() >= 0 ? 1 : -1);
        }
    }

    @Override
    public void onUpdate(double tpf) {
        // Можно оставить пустым или вызывать moveTowards для автоматического следования за игроком
    }
}
