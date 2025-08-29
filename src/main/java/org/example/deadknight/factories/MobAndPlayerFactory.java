package org.example.deadknight.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.AnimationChannelData;
import com.almasb.fxgl.texture.Texture;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.example.deadknight.components.EnemyComponent;
import org.example.deadknight.components.HealthComponent;
import org.example.deadknight.types.EntityType;

import java.util.ArrayList;
import java.util.List;

public class MobAndPlayerFactory implements EntityFactory {

    @Spawns("goblin")
    public Entity newGoblin(SpawnData data) {
        int health = data.getData().containsKey("health") ? (int) data.get("health") : 50;

        // Загружаем кадры
        List<Image> frames = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            frames.add(FXGL.image("goblin-" + i + ".png"));
        }

        ImageView goblinView = new ImageView(frames.get(0));
        goblinView.setFitWidth(110);
        goblinView.setFitHeight(110);
        goblinView.setPreserveRatio(true);

        // Timeline для анимации кадров
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
            int nextIndex = (frames.indexOf(goblinView.getImage()) + 1) % frames.size();
            goblinView.setImage(frames.get(nextIndex));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        Entity goblin = FXGL.entityBuilder(data)
                .type(EntityType.HOSTILE_MOB)
                .view(goblinView)
                .bbox(new HitBox("BODY", new Point2D(30, 60), BoundingShape.box(10, 10)))
                .with(new EnemyComponent())
                .with(new HealthComponent(health))
                .collidable()
                .build();

        goblin.getProperties().setValue("canTakeDamage", true);
        goblin.getProperties().setValue("speed", 50.0);

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
