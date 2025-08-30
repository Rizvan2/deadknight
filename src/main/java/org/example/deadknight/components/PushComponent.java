package org.example.deadknight.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

/**
 * Компонент для отталкивания сущности с плавным замедлением.
 */
public class PushComponent extends Component {

    /** Текущая скорость отталкивания */
    private Point2D velocity = Point2D.ZERO;

    /** Коэффициент замедления (чем больше, тем быстрее тормозит) */
    private final double damping = 3.0;

    /**
     * Добавить импульс для отталкивания.
     * @param impulse Вектор силы (направление и величина)
     */
    public void addImpulse(Point2D impulse) {
        this.velocity = this.velocity.add(impulse);
    }

    @Override
    public void onUpdate(double tpf) {
        if (velocity.magnitude() > 0.1) {
            // Двигаем сущность
            entity.translate(velocity.multiply(tpf));

            // Плавное замедление
            velocity = velocity.subtract(velocity.multiply(damping * tpf));
        } else {
            velocity = Point2D.ZERO;
        }
    }
}
