package org.example.deadknight.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.deadknight.components.EnemyComponent;
import org.example.deadknight.components.HealthComponent;
import org.example.deadknight.types.EntityType;

public class MobAndPlayerFactory implements EntityFactory {

    @Spawns("goblin")
    public Entity newGoblin(SpawnData data) {
        int health;
        if (data.getData().containsKey("health")) {
            health = (int) data.getData().get("health"); // приведение Object к int
        } else {
            health = 50; // дефолтное здоровье
        }

        Entity goblin = FXGL.entityBuilder(data)
                .type(EntityType.HOSTILE_MOB)
                .view(new Rectangle(30, 30, Color.GREEN))
                .bbox(new HitBox("BODY", BoundingShape.box(30, 30))) // <-- добавляем коллизию
                .with(new EnemyComponent())
                .with(new HealthComponent(health))
                .collidable()
                .build();

        // добавляем метку после билда
        goblin.getProperties().setValue("canTakeDamage", true);
        goblin.getProperties().setValue("speed", 50);

        return goblin;

    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityType.PLAYER)
                .view(new Rectangle(40, 40, Color.BLUE))
                .collidable()
                .build();
    }
}
