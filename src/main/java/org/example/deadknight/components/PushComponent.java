package org.example.deadknight.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

public class PushComponent extends Component {

    private Point2D pushVector = Point2D.ZERO;

    public void addPush(Point2D vector) {
        pushVector = pushVector.add(vector);
    }

    @Override
    public void onUpdate(double tpf) {
        if (pushVector.magnitude() > 0.1) {
            entity.translate(pushVector.multiply(tpf));
            pushVector = pushVector.multiply(0.9); // затухание для плавного торможения
        } else {
            pushVector = Point2D.ZERO;
        }
    }
}
