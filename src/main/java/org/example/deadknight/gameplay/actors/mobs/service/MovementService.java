package org.example.deadknight.gameplay.actors.mobs.service;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import org.example.deadknight.gameplay.components.AnimationComponent;

/**
 * Сервис для управления перемещением сущности в игровом мире.
 * <p>
 * Позволяет перемещать сущность к цели с учётом скорости и времени кадра (tpf),
 * анимации ходьбы и направления взгляда.
 */
public class MovementService {
    private final Entity entity;
    private final double speed;
    private Point2D direction = Point2D.ZERO;

    public MovementService(Entity entity, double speed) {
        this.entity = entity;
        this.speed = speed;
    }

    public void setDirection(Point2D dir) {
        this.direction = dir.magnitude() > 0 ? dir.normalize() : Point2D.ZERO;
    }

    public void update(double tpf) {
        if (direction.magnitude() == 0) return;

        // Получаем компонент прямо из сущности
        AnimationComponent animation = entity.getComponent(AnimationComponent.class);
        animation.setFacingRight(direction.getX() >= 0);

        entity.translate(direction.multiply(speed * tpf));
    }

    public void moveToTarget(Entity target, double tpf) {
        Point2D dir = target.getCenter().subtract(entity.getCenter());
        setDirection(dir);
        update(tpf);
    }
}
