package org.example.deadknight.gameplay.components.debug;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.deadknight.config.GameConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Компонент для отладки хитбоксов сущностей.
 * <p>
 * Если включён флаг {@link GameConfig#DEBUG_HITBOXES}, то для каждой сущности
 * рисуются прозрачные красные прямоугольники поверх её хитбоксов.
 * Прямоугольники добавляются в {@code ViewComponent} сущности и автоматически
 * обновляют свои координаты при движении.
 *
 * <p>Используется для визуальной проверки столкновений и корректности размеров хитбоксов.
 */
public class DebugHitBoxComponent extends Component {

    /**
     * Словарь соответствия хитбоксов и их графических представлений.
     * Ключ — {@link HitBox}, значение — {@link Rectangle}, отображающий его на экране.
     */
    private final Map<HitBox, Rectangle> hitBoxRects = new HashMap<>();

    /**
     * Вызывается при добавлении компонента к сущности.
     * Если отладка хитбоксов отключена в {@link GameConfig},
     * метод завершается без действия.
     * <p>
     * Для каждого хитбокса сущности создаётся красный прямоугольник,
     * который добавляется во {@code ViewComponent}.
     */
    @Override
    public void onAdded() {
        if (GameConfig.DEBUG_HITBOXES == false) {
            return;
        }
        BoundingBoxComponent bbox = entity.getComponent(BoundingBoxComponent.class);
        if (bbox == null) return;

        for (HitBox hb : bbox.hitBoxesProperty()) {
            Rectangle rect = new Rectangle(hb.getWidth(), hb.getHeight());
            rect.setStroke(Color.RED);
            rect.setFill(Color.color(1, 0, 0, 0.2));
            rect.setMouseTransparent(true);

            entity.getViewComponent().addChild(rect);
            hitBoxRects.put(hb, rect);
        }
    }

    /**
     * Вызывается каждый кадр игры.
     * Обновляет координаты прямоугольников так, чтобы они соответствовали
     * текущему положению хитбоксов сущности.
     *
     * @param tpf время, прошедшее с последнего кадра (в секундах)
     */
    @Override
    public void onUpdate(double tpf) {
        for (Map.Entry<HitBox, Rectangle> entry : hitBoxRects.entrySet()) {
            HitBox hb = entry.getKey();
            Rectangle rect = entry.getValue();

            rect.setX(hb.getMinX());
            rect.setY(hb.getMinY());
        }
    }
}
