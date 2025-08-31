package org.example.deadknight.mobs.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.animation.AnimationTimer;
import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import org.example.deadknight.mobs.entities.GoblinEntity;

import java.util.List;

/**
 * Управляет анимацией моба: ходьба и атака.
 * Оптимизировано: дублирование кода обновления кадров убрано.
 */
public class AnimationComponent extends Component {

    @Getter
    private final GoblinEntity goblinData;
    private ImageView goblinView;
    private boolean attacking = false;

    private int walkIndex = 0;
    private int attackIndex = 0;
    private double walkElapsed = 0;
    private double attackElapsed = 0;

    private static final double WALK_FRAME_TIME = 0.1;
    private static final double ATTACK_FRAME_TIME = 0.04;

    public AnimationComponent(GoblinEntity goblinData) {
        this.goblinData = goblinData;
    }

    @Override
    public void onAdded() {

        goblinView = (ImageView) entity.getViewComponent().getChildren().get(0);
        goblinView.setSmooth(true);
        goblinView.setCache(true);
        goblinView.setCacheHint(CacheHint.SPEED);

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
        List<Image> walkFrames = goblinData.getWalkFrames();
        List<Image> attackFrames = goblinData.getAttackFrames();

        if (attacking) {
            attackElapsed += tpf;
            if (attackElapsed >= ATTACK_FRAME_TIME && attackIndex < attackFrames.size()) {
                goblinView.setImage(attackFrames.get(attackIndex));
                attackIndex++;
                attackElapsed = 0;
            }
            if (attackIndex >= attackFrames.size()) {
                playWalk(); // возвращаемся к ходьбе
            }
        } else {
            walkElapsed += tpf;
            if (walkElapsed >= WALK_FRAME_TIME) {
                walkIndex = (walkIndex + 1) % walkFrames.size();
                goblinView.setImage(walkFrames.get(walkIndex));
                walkElapsed = 0;
            }
        }
    }

    public void playAttack() {
        if (attacking) return;
        attacking = true;
        attackIndex = 0;
        attackElapsed = 0;
    }

    public void playWalk() {
        attacking = false;
        walkIndex = 0;
        walkElapsed = 0;
    }

    public void setScaleX(double scaleX) {
        if (goblinView != null) goblinView.setScaleX(scaleX);
    }

    public boolean isAttacking() {
        return attacking;
    }
}
