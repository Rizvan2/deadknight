package org.example.deadknight.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class EnemyComponent extends Component {

    private double elapsed = 0;
    private boolean attacking = false;
    private List<Image> walkFrames;
    private List<Image> attackFrames;
    private ImageView goblinView;


    @Override
    public void onAdded() {
        goblinView = (ImageView) entity.getViewComponent().getChildren().get(0);

        // Загружаем кадры
        walkFrames = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            walkFrames.add(FXGL.image("goblin-" + i + ".png"));
        }

        attackFrames = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            attackFrames.add(FXGL.image("goblin_attack-" + i + ".png"));
        }

        // Запускаем walk-анимацию по таймеру
        Timeline walkTimeline = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> animateWalk()));
        walkTimeline.setCycleCount(Timeline.INDEFINITE);
        walkTimeline.play();
    }

    private double attackCooldown = 1.0; // секунды между атаками
    private double lastAttackTime = 0;

    @Override
    public void onUpdate(double tpf) {
        Entity player = FXGL.getGameWorld()
                .getEntities().stream()
                .filter(e -> e.getProperties().exists("isPlayer") && e.getProperties().getBoolean("isPlayer"))
                .findFirst()
                .orElse(null);

        if (player == null) return;

        elapsed += tpf;
        lastAttackTime += tpf;

        double maxSpeed = entity.getProperties().exists("speed") ? entity.getProperties().getDouble("speed") : 50;
        double factor = Math.min(1, elapsed / 2);
        double effectiveSpeed = maxSpeed * factor;

        Point2D direction = player.getPosition().subtract(entity.getPosition());
        double distance = direction.magnitude();

        if (distance > 40) { // ходим к игроку
            Point2D move = direction.normalize().multiply(effectiveSpeed * tpf);
            entity.translate(move);
            goblinView.setScaleX(move.getX() >= 0 ? 1 : -1);
            attacking = false;
        } else {
            if (!attacking && lastAttackTime >= attackCooldown) {
                attacking = true;
                lastAttackTime = 0;
                startAttack(player);
            }
        }
    }


    private int walkIndex = 0;

    private void animateWalk() {

        if (!attacking && goblinView != null) {
            walkIndex = (walkIndex + 1) % walkFrames.size();
            goblinView.setImage(walkFrames.get(walkIndex));
        }
    }

    private void startAttack(Entity player) {
        if (!isValidPlayer(player)) return;

        attacking = true;

        // Наносим урон один раз
        attackPlayer(player);

        // Плей анимацию (без нанесения урона каждый кадр)
        Timeline attackAnimation = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> animateWalk()));
        attackAnimation.setCycleCount(attackFrames.size());
        attackAnimation.setOnFinished(e -> attacking = false);
        attackAnimation.play();
    }

    private void attackPlayer(Entity player) {
        if (!isValidPlayer(player)) return;

        player.getComponentOptional(HealthComponent.class).ifPresent(h -> {
            if (!h.isDead()) {
                h.takeDamage(10);

                // Если игрок умер после удара — удаляем
                if (h.isDead()) {
                    player.removeFromWorld();
                }
            }
        });
    }

    // Вспомогательный метод для проверки, что игрок живой и существует
    private boolean isValidPlayer(Entity player) {
        return player != null &&
                player.getWorld() != null &&
                player.hasComponent(HealthComponent.class);
    }

}
