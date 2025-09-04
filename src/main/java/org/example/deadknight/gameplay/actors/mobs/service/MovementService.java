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
    private final AnimationComponent animation;
    private final double speed;
    private Point2D direction = Point2D.ZERO;

    public MovementService(Entity entity, AnimationComponent animation, double speed) {
        this.entity = entity;
        this.animation = animation;
        this.speed = speed;
    }

    /**
     * Устанавливает направление движения сущности.
     * <p>
     * Если переданный вектор равен нулю, движение при обновлении будет остановлено.
     *
     * @param dir вектор направления движения
     */
    public void setDirection(Point2D dir) {
        this.direction = dir.magnitude() > 0 ? dir.normalize() : Point2D.ZERO;
    }

    /**
     * Обновляет позицию сущности на основе текущего направления и скорости.
     * <p>
     * Следует вызывать каждый кадр с переданным {@code tpf} (time per frame),
     * чтобы движение было независимым от частоты кадров.
     *
     * @param tpf время кадра (seconds per frame)
     */
    public void update(double tpf) {
        if (direction.magnitude() == 0) return;

        entity.translate(direction.multiply(speed * tpf));
        animation.setScaleX(direction.getX() >= 0 ? 1 : -1);
    }

    /**
     * Автоматически вычисляет направление к цели (с учётом центра хитбокса)
     * и перемещает сущность на один кадр.
     *
     * @param target цель, к которой движется сущность
     * @param tpf    время кадра
     */
    public void moveToTarget(Entity target, double tpf) {
        // вычисляем вектор к центру цели
        Point2D dir = target.getCenter().subtract(entity.getCenter());
        setDirection(dir);        // нормализуем и сохраняем направление
        update(tpf);              // перемещаем на основе направления и скорости
    }
}
