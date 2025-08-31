package org.example.deadknight.mobs.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.animation.AnimationTimer;
import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

/**
 * Управляет анимацией моба: ходьба и атака.
 */
public class AnimationComponent extends Component {

    private final List<Image> walkFrames;
    private final List<Image> attackFrames;
    private ImageView view;

    private boolean attacking = false;
    private int walkIndex = 0;
    private int attackIndex = 0;
    private double walkElapsed = 0;
    private double attackElapsed = 0;

    public AnimationComponent(List<Image> walkFrames, List<Image> attackFrames) {
        this.walkFrames = walkFrames;
        this.attackFrames = attackFrames;
    }

    @Override
    public void onAdded() {
        view = (ImageView) entity.getViewComponent().getChildren().get(0);
        view.setSmooth(true);
        view.setCache(true);
        view.setCacheHint(CacheHint.SPEED);
        startAnimationTimer();
    }

    private void startAnimationTimer() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) { lastTime = now; return; }
                double tpf = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                update(tpf);
            }
        };
        timer.start();
    }

    private void update(double tpf) {
        if (attacking) {
            attackElapsed += tpf;
            double attackFrameTime = 0.04;
            if (attackElapsed >= attackFrameTime && attackIndex < attackFrames.size()) {
                view.setImage(attackFrames.get(attackIndex));
                attackIndex++;
                attackElapsed = 0;
            }
            if (attackIndex >= attackFrames.size()) {
                playWalk(); // автоматически возвращаемся к ходьбе
            }
        } else {
            walkElapsed += tpf;
            double walkFrameTime = 0.1;
            if (walkElapsed >= walkFrameTime) {
                walkIndex = (walkIndex + 1) % walkFrames.size();
                view.setImage(walkFrames.get(walkIndex));
                walkElapsed = 0;
            }
        }
    }

    /** Меняет горизонтальное направление спрайта */
    public void setScaleX(double scaleX) {
        if (view != null) {
            view.setScaleX(scaleX);
        }
    }

    /** Запускает анимацию атаки */
    public void playAttack() {
        attacking = true;
        attackIndex = 0;
        attackElapsed = 0;
    }

    /** Возвращает анимацию к ходьбе */
    public void playWalk() {
        attacking = false;
        walkIndex = 0;
        walkElapsed = 0;
    }
}
