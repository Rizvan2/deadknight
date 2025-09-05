package org.example.deadknight.gameplay.actors.mobs.factories;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.image.ImageView;

import java.util.Arrays;

public class GoblinAnimationLoader {

    private final int goblinSize;

    public GoblinAnimationLoader(int goblinSize) {
        this.goblinSize = goblinSize;
    }

    public ImageView[] loadWalkRight() {
        return loadFrames("goblin/goblin-", 25);
    }

    public ImageView[] loadWalkLeft() {
        return createMirrored(loadWalkRight());
    }

    public ImageView[] loadAttackRight() {
        return loadFrames("goblin/goblin_attack-", 15);
    }

    public ImageView[] loadAttackLeft() {
        return createMirrored(loadAttackRight());
    }

    public ImageView[] loadDeathFrames() {
        ImageView[] frames = loadFrames("goblin/goblin_death-", 4);
        // повторяем последний кадр
        ImageView last = frames[frames.length - 1];
        ImageView[] extended = Arrays.copyOf(frames, frames.length + 1);
        extended[frames.length] = new ImageView(last.getImage());
        return extended;
    }

    private ImageView[] loadFrames(String prefix, int count) {
        ImageView[] frames = new ImageView[count];
        for (int i = 1; i <= count; i++) {
            ImageView iv = new ImageView(FXGL.image(prefix + i + ".png"));
            iv.setFitWidth(goblinSize);
            iv.setFitHeight(goblinSize);
            frames[i - 1] = iv;
        }
        return frames;
    }

    private ImageView[] createMirrored(ImageView[] original) {
        ImageView[] mirrored = new ImageView[original.length];
        for (int i = 0; i < original.length; i++) {
            ImageView iv = new ImageView(original[i].getImage());
            iv.setFitWidth(goblinSize);
            iv.setFitHeight(goblinSize);
            iv.setScaleX(-1);
            mirrored[i] = iv;
        }
        return mirrored;
    }
}
