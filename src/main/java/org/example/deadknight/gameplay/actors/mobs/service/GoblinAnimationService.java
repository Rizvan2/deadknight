package org.example.deadknight.gameplay.actors.mobs.service;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для загрузки и предоставления кадров анимации гоблина.
 * <p>
 * Возвращает кадры сразу для левой и правой анимации, чтобы избежать искажений при зеркалировании.
 */
public class GoblinAnimationService {

    private static final double FRAME_SIZE = 150;

    private ImageView toImageView(Image img, boolean flip) {
        ImageView iv = new ImageView(img);
        iv.setFitWidth(FRAME_SIZE);
        iv.setFitHeight(FRAME_SIZE);
        iv.setPreserveRatio(true);
        if (flip) {
            iv.setScaleX(-1);
        }
        return iv;
    }

    public List<ImageView> loadWalkFramesLeft() {
        List<ImageView> frames = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            Image img = FXGL.image("goblin/goblin-" + i + ".png");
            frames.add(toImageView(img, false));
        }
        return frames;
    }

    public List<ImageView> loadWalkFramesRight() {
        List<ImageView> frames = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            Image img = FXGL.image("goblin/goblin-" + i + ".png");
            frames.add(toImageView(img, true));
        }
        return frames;
    }

    public List<ImageView> loadAttackFramesLeft() {
        List<ImageView> frames = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            Image img = FXGL.image("goblin/goblin_attack-" + i + ".png");
            frames.add(toImageView(img, false));
        }
        return frames;
    }

    public List<ImageView> loadAttackFramesRight() {
        List<ImageView> frames = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            Image img = FXGL.image("goblin/goblin_attack-" + i + ".png");
            frames.add(toImageView(img, true));
        }
        return frames;
    }

    public List<ImageView> loadDeathFrames() {
        List<ImageView> frames = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            Image img = FXGL.image("goblin/goblin_death-" + i + ".png");
            frames.add(toImageView(img, false));
        }
        // дублируем последний кадр
        frames.add(frames.get(frames.size() - 1));
        return frames;
    }
}
