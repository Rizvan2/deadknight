package org.example.deadknight.gameplay.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.deadknight.config.GameConfig;

/**
 * Компонент для отрисовки хитбокса рыцаря на экране.
 * <p>
 * Отображает BoundingBox рыцаря поверх игрового мира,
 * учитывая смещение камеры, и обновляется каждый кадр.
 * Используется только для дебага.
 */
public class KnightDebugHitBoxComponent extends Component {

    private Canvas canvas;
    private GraphicsContext g;

    @Override
    public void onAdded() {
        // Canvas растягивается на весь экран, чтобы вместить весь мир
        canvas = new Canvas(FXGL.getAppWidth(), FXGL.getAppHeight());
        g = canvas.getGraphicsContext2D();

        // Настройка внешнего вида хитбокса
        g.setStroke(Color.RED);
        g.setFill(Color.color(1, 0, 0, 0.2));

        // Добавляем Canvas в сцену поверх всего UI
        FXGL.getGameScene().addUINode(canvas);
    }

    @Override
    public void onUpdate(double tpf) {
        if (GameConfig.DEBUG_HITBOXES == false){
            return;
        }
        Entity e = getEntity();

        // Получаем положение камеры, чтобы корректно рисовать хитбокс относительно экрана
        double camX = FXGL.getGameScene().getViewport().getX();
        double camY = FXGL.getGameScene().getViewport().getY();

        // Смещаем Canvas по камере (если нужно) — обычно для фиксированной сцены можно не смещать
        canvas.setTranslateX(e.getX() - camX);
        canvas.setTranslateY(e.getY() - camY);

        // Очищаем Canvas перед перерисовкой
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        BoundingBoxComponent bbox = e.getComponent(BoundingBoxComponent.class);
        if (bbox == null) return;

        // Рисуем все хитбоксы сущности
        for (HitBox hb : bbox.hitBoxesProperty()) {
            g.strokeRect(hb.getMinX(), hb.getMinY(), hb.getWidth(), hb.getHeight());
            g.fillRect(hb.getMinX(), hb.getMinY(), hb.getWidth(), hb.getHeight());
        }
    }
}
