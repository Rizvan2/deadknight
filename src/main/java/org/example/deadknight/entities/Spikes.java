package org.example.deadknight.entities;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.image.ImageView;
import org.example.deadknight.components.HealthComponent;
import org.example.deadknight.types.EntityType;

public class Spikes {


    public static Entity create(double x, double y) {
        ImageView texture = FXGL.texture("spikes.png");
        texture.setFitWidth(64);
        texture.setFitHeight(64);

        Entity spikes = FXGL.entityBuilder()
                .at(x, y)
                .view(texture)
                .bbox(new HitBox("BODY", BoundingShape.box(64, 64)))
                .type(EntityType.SPIKES)
                .with(new HealthComponent(50)) // добавляем здоровье
                .build();

        spikes.getProperties().setValue("canTakeDamage", true);

        return spikes;
    }

}
