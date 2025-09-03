package org.example.deadknight.gameplay.actors.player.controllers;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import org.example.deadknight.gameplay.actors.player.services.HasSpeed;

/**
 * Контроллер движения игрока с постоянной скоростью в пикселях/сек.
 */
public class MovementController {

    private final Entity entity;
    private final double speed;
    private Point2D direction = Point2D.ZERO;

    public MovementController(HasSpeed character, Entity entity) {
        this.entity = entity;
        this.speed = character.getSpeed();
    }

    public void setDirection(Point2D dir) {
        direction = dir.magnitude() > 0 ? dir.normalize() : Point2D.ZERO;
    }

    public void update(double tpf) {
        entity.translate(direction.multiply(speed * tpf));
    }
}
