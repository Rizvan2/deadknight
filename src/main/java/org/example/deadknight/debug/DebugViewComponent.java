package org.example.deadknight.debug;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PhysicsWorld;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
//тут нечего не работает класс создавался для показа хитбоксов на мобах во время игры пока что не доработан
@Required(PhysicsComponent.class)
public class DebugViewComponent extends Component {

    private PhysicsComponent physics;
    private PhysicsWorld world;
    private Rectangle debugRect;

    @Override
    public void onAdded() {
        world = FXGL.getPhysicsWorld();

        // Создаём один прямоугольник на всю сущность
        debugRect = new Rectangle(entity.getWidth(), entity.getHeight());
        debugRect.setStroke(Color.LIME);
        debugRect.setFill(Color.TRANSPARENT);
        entity.getViewComponent().addChild(debugRect);
    }

    @Override
    public void onUpdate(double tpf) {
        if (physics != null && world != null) {
            double x = world.toPixels(physics.getBody().getPosition().x) - entity.getWidth() / 2.0;
            double y = world.toPixels(physics.getBody().getPosition().y) - entity.getHeight() / 2.0;

            entity.setX(x);
            entity.setY(y);
        }
    }
}
