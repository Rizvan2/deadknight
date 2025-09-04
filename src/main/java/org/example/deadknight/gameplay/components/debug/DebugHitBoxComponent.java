package org.example.deadknight.gameplay.components.debug;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;

public class DebugHitBoxComponent extends Component {

    private final Map<HitBox, Rectangle> hitBoxRects = new HashMap<>();

    @Override
    public void onAdded() {
        BoundingBoxComponent bbox = entity.getComponent(BoundingBoxComponent.class);
        if (bbox == null) return;

        for (HitBox hb : bbox.hitBoxesProperty()) {
            Rectangle rect = new Rectangle(hb.getWidth(), hb.getHeight());
            rect.setStroke(Color.RED);
            rect.setFill(Color.color(1, 0, 0, 0.2));
            rect.setMouseTransparent(true);

            entity.getViewComponent().addChild(rect); // только один раз
            hitBoxRects.put(hb, rect);
        }
    }

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
