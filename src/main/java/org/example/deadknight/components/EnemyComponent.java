package org.example.deadknight.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class EnemyComponent extends Component {

    private double elapsed = 0;
    private boolean attacking = false;
    private List<Image> walkFrames;
    private List<Image> attackFrames;
    private ImageView goblinView;
    private double lastAttackTime = 0;
    private int attackIndex = 0;
    private double walkElapsed = 0;
    private double attackElapsed = 0;
    private int walkIndex = 0;

    @Override
    public void onAdded() {
        goblinView = (ImageView) entity.getViewComponent().getChildren().get(0);


        goblinView.setSmooth(true);
        goblinView.setCache(true);
        goblinView.setCacheHint(CacheHint.SPEED); // ускоряем отрисовку


        // Загружаем walk кадры заранее в фиксированном размере
        walkFrames = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            walkFrames.add(FXGL.image("goblin-" + i + ".png"));
        }

        // Загружаем attack кадры заранее в фиксированном размере
        attackFrames = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            attackFrames.add(FXGL.image("goblin_attack-" + i + ".png"));
        }

        // Создаем AnimationTimer
        AnimationTimer animationTimer = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                double tpf = (now - lastTime) / 1_000_000_000.0; // секунды
                lastTime = now;

                updateAnimation(tpf);
            }
        };
        animationTimer.start();
    }

    private void updateAnimation(double tpf) {
        if (attacking) {
            attackElapsed += tpf;
            // сколько секунд длится атака
            double attackDuration = 0.04;
            if (attackElapsed >= attackDuration && attackIndex < attackFrames.size()) {
                goblinView.setImage(attackFrames.get(attackIndex));
                attackIndex++;
                attackElapsed = 0;
            }
            if (attackIndex >= attackFrames.size()) {
                attacking = false;
                attackIndex = 0;
            }
        } else {
            walkElapsed += tpf;
            // время на кадр ходьбы
            double walkFrameTime = 0.1;
            if (walkElapsed >= walkFrameTime) {
                walkIndex = (walkIndex + 1) % walkFrames.size();
                goblinView.setImage(walkFrames.get(walkIndex));
                walkElapsed = 0;
            }
        }
    }

    private void startAttack(Entity player) {
        if (!isValidPlayer(player)) return;

        attacking = true;
        attackIndex = 0;
        attackElapsed = 0;

        // Наносим урон один раз при старте
        attackPlayer(player);
    }

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

        if (distance > 40) {
            Point2D move = direction.normalize().multiply(effectiveSpeed * tpf);
            entity.translate(move);
            goblinView.setScaleX(move.getX() >= 0 ? 1 : -1);
            // НЕ меняем attacking здесь!
        } else {
            // секунды между атаками
            double attackCooldown = 1.0;
            if (!attacking && lastAttackTime >= attackCooldown) {
                lastAttackTime = 0;
                startAttack(player);
            }
        }

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