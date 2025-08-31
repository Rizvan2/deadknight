package org.example.deadknight.mobs.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import org.example.deadknight.mobs.entities.GoblinEntity;

import java.util.List;

/**
 * Компонент, управляющий поведением врага (гоблина).
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Анимацию ходьбы и атаки</li>
 *     <li>Следование за игроком</li>
 * </ul>
 * <p>
 * Атаку теперь делегирует {@link AttackComponent}.
 */
public class EnemyComponent extends Component {

    @Getter
    private final GoblinEntity goblinData;
    private ImageView goblinView;
    private double elapsed = 0;

    private AnimationComponent animationComponent;
    private AttackComponent attackComponent;

    public EnemyComponent(GoblinEntity data) {
        this.goblinData = data;
    }

    @Override
    public void onAdded() {
        goblinView = (ImageView) entity.getViewComponent().getChildren().get(0);

        goblinView.setSmooth(true);
        goblinView.setCache(true);
        goblinView.setCacheHint(CacheHint.SPEED);

        // Инициализируем анимацию
        animationComponent = new AnimationComponent(goblinData);
        entity.addComponent(animationComponent); // важно добавить в entity

        // Инициализируем компонент атаки с нужным уроном и кулдауном
        attackComponent = new AttackComponent(goblinData.getDamage(), 1.0);
        entity.addComponent(attackComponent);

        startAnimationTimer();
    }

    private void startAnimationTimer() {
        AnimationTimer animationTimer = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                double tpf = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                animationComponent.update(tpf);
            }
        };
        animationTimer.start();
    }

//    private void updateAnimation(double tpf) {
//        List<Image> walkFrames = goblinData.getWalkFrames();
//        List<Image> attackFrames = goblinData.getAttackFrames();
//
//        if (attacking) {
//            attackElapsed += tpf;
//            double attackFrameTime = 0.04;
//            if (attackElapsed >= attackFrameTime && attackIndex < attackFrames.size()) {
//                goblinView.setImage(attackFrames.get(attackIndex));
//                attackIndex++;
//                attackElapsed = 0;
//            }
//            if (attackIndex >= attackFrames.size()) {
//                attacking = false;
//                attackIndex = 0;
//            }
//        } else {
//            walkElapsed += tpf;
//            double walkFrameTime = 0.1;
//            if (walkElapsed >= walkFrameTime) {
//                walkIndex = (walkIndex + 1) % walkFrames.size();
//                goblinView.setImage(walkFrames.get(walkIndex));
//                walkElapsed = 0;
//            }
//        }
//    }

    @Override
    public void onUpdate(double tpf) {
        Entity player = FXGL.getGameWorld()
                .getEntities()
                .stream()
                .filter(e -> e.getProperties().exists("isPlayer") && e.getProperties().getBoolean("isPlayer"))
                .findFirst()
                .orElse(null);

        if (player == null) return;

        elapsed += tpf;

        double maxSpeed = goblinData.getSpeed();
        double factor = Math.min(1, elapsed / 2);
        double effectiveSpeed = maxSpeed * factor;

        Point2D direction = player.getPosition().subtract(entity.getPosition());
        double distance = direction.magnitude();

        if (distance > 40) {
            Point2D move = direction.normalize().multiply(effectiveSpeed * tpf);
            entity.translate(move);
            goblinView.setScaleX(move.getX() >= 0 ? 1 : -1);
        } else {
            attackComponent.tryAttack(player, tpf);
        }
    }
}
