package org.example.deadknight.handlers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public class AnimationHandler {

    private final ImageView view;
    private final List<Image> walkFrames;
    private final List<Image> attackFrames;

    private double walkElapsed = 0;
    private double attackElapsed = 0;
    private int walkIndex = 0;
    private int attackIndex = 0;
    private boolean attacking = false;

    private final double walkFrameTime;
    private final double attackDuration;

    public AnimationHandler(ImageView view, List<Image> walkFrames, List<Image> attackFrames,
                            double walkFrameTime, double attackDuration) {
        this.view = view;
        this.walkFrames = walkFrames;
        this.attackFrames = attackFrames;
        this.walkFrameTime = walkFrameTime;
        this.attackDuration = attackDuration;
    }

    public void update(double tpf, Runnable attackCallback) {
        if (attacking) {
            attackElapsed += tpf;
            if (attackElapsed >= attackDuration && attackIndex < attackFrames.size()) {

                // Наносим урон в начале анимации
                if (attackIndex == 0 && attackCallback != null) {
                    attackCallback.run();
                }

                view.setImage(attackFrames.get(attackIndex));
                attackIndex++;
                attackElapsed = 0;
            }
            if (attackIndex >= attackFrames.size()) {
                attacking = false;
                attackIndex = 0;
            }
        } else {
            walkElapsed += tpf;
            if (walkElapsed >= walkFrameTime) {
                walkIndex = (walkIndex + 1) % walkFrames.size();
                view.setImage(walkFrames.get(walkIndex));
                walkElapsed = 0;
            }
        }
    }

    public void startAttack() {
        attacking = true;
        attackIndex = 0;
        attackElapsed = 0;
    }

    public boolean isAttacking() {
        return attacking;
    }
}
