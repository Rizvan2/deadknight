package org.example.deadknight.handlers;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

public class MovementHandler {

    private final Entity enemy;
    private final double maxSpeed;

    public MovementHandler(Entity enemy, double maxSpeed) {
        this.enemy = enemy;
        this.maxSpeed = maxSpeed;
    }

    public void moveTowards(Entity target, double tpf) {
        if (target == null) return;

        Point2D direction = target.getPosition().subtract(enemy.getPosition());
        double distance = direction.magnitude();

        if (distance > 40) {
            Point2D move = direction.normalize().multiply(maxSpeed * tpf);
            enemy.translate(move);
        }
    }
}
