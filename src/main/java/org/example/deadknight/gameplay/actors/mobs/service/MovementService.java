package org.example.deadknight.gameplay.actors.mobs.service;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import org.example.deadknight.gameplay.components.AnimationComponent;

/**
 * Сервис для перемещения сущности в игровом мире.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Перемещение сущности в направлении к цели с учётом скорости и времени кадра (tpf)</li>
 *     <li>Проигрывание анимации ходьбы через {@link AnimationComponent}</li>
 *     <li>Обновление направления взгляда сущности в зависимости от движения</li>
 * </ul>
 */
public class MovementService {

    /** Сущность, которую нужно перемещать. */
    private final Entity entity;

    /** Компонент анимации для управления кадрами ходьбы и направления взгляда. */
    private final AnimationComponent animation;

    /** Скорость перемещения сущности. */
    private final double speed;

    /**
     * Конструктор сервиса.
     *
     * @param entity сущность, которую нужно перемещать
     * @param animation компонент анимации для управления кадрами ходьбы
     * @param speed скорость перемещения сущности
     */
    public MovementService(Entity entity, AnimationComponent animation, double speed) {
        this.entity = entity;
        this.animation = animation;
        this.speed = speed;
    }

    /**
     * Перемещает сущность в указанном направлении.
     * <p>
     * Метод нормализует вектор направления, умножает его на скорость и время кадра (tpf),
     * затем обновляет позицию сущности. Также корректирует направление взгляда сущности
     * по оси X в зависимости от движения (смотрит влево или вправо).
     *
     * @param direction вектор направления движения
     * @param tpf время кадра (time per frame)
     */
    public void moveTowards(Point2D direction, double tpf) {
        Point2D move = direction.normalize().multiply(speed * tpf);
        entity.translate(move);
        animation.setScaleX(move.getX() >= 0 ? 1 : -1);
    }

}
