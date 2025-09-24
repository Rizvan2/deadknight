package org.example.deadknight.infrastructure.assets;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.image.ImageView;

import java.util.Arrays;

/**
 * Утилитарный класс для загрузки и подготовки анимационных кадров гоблина.
 */
public class GoblinAnimationLoader {

    private final int goblinSize;

    public GoblinAnimationLoader(int goblinSize) {
        this.goblinSize = goblinSize;
    }

    public ImageView[] loadWalkRight() {
        return loadFrames("goblin/goblin-", 25, false);
    }

    public ImageView[] loadWalkLeft() {
        return loadFrames("goblin/goblin-", 25, true);
    }

    public ImageView[] loadAttackRight() {
        return loadFrames("goblin/goblin_attack-", 15, false);
    }

    public ImageView[] loadAttackLeft() {
        return loadFrames("goblin/goblin_attack-", 15, true);
    }

    public ImageView[] loadDeathFrames() {
        ImageView[] frames = loadFrames("goblin/goblin_death-", 4, false);
        ImageView last = frames[frames.length - 1];
        ImageView[] extended = Arrays.copyOf(frames, frames.length + 1);
        extended[frames.length] = new ImageView(last.getImage());
        return extended;
    }

    private ImageView[] loadFrames(String prefix, int count, boolean mirrored) {
        ImageView[] frames = new ImageView[count];
        for (int i = 1; i <= count; i++) {
            ImageView iv = new ImageView(FXGL.image(prefix + i + ".png"));
            iv.setFitWidth(goblinSize);
            iv.setFitHeight(goblinSize);
            if (mirrored) {
                iv.setScaleX(-1);
            }
            frames[i - 1] = iv;
        }
        return frames;
    }
}


