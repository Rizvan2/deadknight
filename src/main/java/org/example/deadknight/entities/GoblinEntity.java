package org.example.deadknight.entities;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GoblinEntity {

    private final double speed;
    private final int damage;
    private final List<Image> walkFrames;
    private final List<Image> attackFrames;

    public GoblinEntity(double speed, int damage, List<Image> walkFrames, List<Image> attackFrames) {
        this.speed = speed;
        this.damage = damage;
        this.walkFrames = walkFrames;
        this.attackFrames = attackFrames;
    }

}
