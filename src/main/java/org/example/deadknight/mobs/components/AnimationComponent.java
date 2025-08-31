package org.example.deadknight.mobs.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.animation.AnimationTimer;
import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

/**
 * Управляет анимацией моба: ходьба и атака.
 * Оптимизировано: дублирование кода обновления кадров убрано.
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

    private static final double WALK_FRAME_TIME = 0.1;
    private static final double ATTACK_FRAME_TIME = 0.04;

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

    public void update(double tpf) {
        if (attacking) {
            updateFrames(attackFrames, ATTACK_FRAME_TIME, true);
        } else {
            updateFrames(walkFrames, WALK_FRAME_TIME, false);
        }
    }

    /**
     * Универсальное обновление кадров.
     * @param frames список кадров
     * @param frameTime время одного кадра
     * @param isAttack true для атаки, false для ходьбы
     */
    private void updateFrames(List<Image> frames, double frameTime, boolean isAttack) {
        if (isAttack) {
            attackElapsed += frameTime;
            if (attackElapsed >= frameTime && attackIndex < frames.size()) {
                view.setImage(frames.get(attackIndex));
                attackIndex++;
                attackElapsed = 0;
            }
            if (attackIndex >= frames.size()) {
                playWalk(); // после атаки возвращаемся к ходьбе
            }
        } else {
            walkElapsed += frameTime;
            if (walkElapsed >= frameTime) {
                walkIndex = (walkIndex + 1) % frames.size();
                view.setImage(frames.get(walkIndex));
                walkElapsed = 0;
            }
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

    public void setScaleX(double scaleX) {
        if (view != null) view.setScaleX(scaleX);
    }

    public boolean isAttacking() {
        return attacking;
    }
}
