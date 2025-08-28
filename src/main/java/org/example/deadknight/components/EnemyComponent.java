package org.example.deadknight.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import javafx.animation.AnimationTimer;
import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.deadknight.handlers.AnimationHandler;
import org.example.deadknight.handlers.AttackHandler;
import org.example.deadknight.handlers.MovementHandler;

import java.util.ArrayList;
import java.util.List;

public class EnemyComponent extends Component {

    private ImageView goblinView;
    private AnimationHandler animationHandler;
    private MovementHandler movementHandler;
    private AttackHandler attackHandler;

    private double lastAttackTime = 0;
    private final double attackCooldown = 1.0; // секунды между атаками
    private final double walkFrameTime = 0.1;
    private final double attackDuration = 0.04;

    @Override
    public void onAdded() {
        goblinView = (ImageView) entity.getViewComponent().getChildren().get(0);
        goblinView.setSmooth(true);
        goblinView.setCache(true);
        goblinView.setCacheHint(CacheHint.SPEED);

        // Загружаем кадры
        List<Image> walkFrames = new ArrayList<>();
        List<Image> attackFrames = new ArrayList<>();
        for (int i = 1; i <= 25; i++) walkFrames.add(FXGL.image("goblin-" + i + ".png"));
        for (int i = 1; i <= 15; i++) attackFrames.add(FXGL.image("goblin_attack-" + i + ".png"));

        // Инициализация хендлеров
        animationHandler = new AnimationHandler(goblinView, walkFrames, attackFrames, walkFrameTime, attackDuration);
        movementHandler = new MovementHandler(entity, entity.getProperties().exists("speed") ? entity.getProperties().getDouble("speed") : 50);
        attackHandler = new AttackHandler(10);

        // Запуск AnimationTimer для обновления анимаций
        AnimationTimer timer = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                double tpf = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                updateComponent(tpf);
            }
        };
        timer.start();
    }

    private void updateComponent(double tpf) {
        Entity player = FXGL.getGameWorld()
                .getEntities().stream()
                .filter(e -> e.getProperties().exists("isPlayer") && e.getProperties().getBoolean("isPlayer"))
                .findFirst()
                .orElse(null);

        if (player == null) return;

        lastAttackTime += tpf;

        // Движение к игроку
        double distance = player.getPosition().subtract(entity.getPosition()).magnitude();
        if (distance > 40) {
            movementHandler.moveTowards(player, tpf);

            // Смотрим на игрока
            double dx = player.getX() - entity.getX();
            goblinView.setScaleX(dx >= 0 ? 1 : -1);
        } else {
            // Атака
            if (!animationHandler.isAttacking() && lastAttackTime >= attackCooldown) {
                lastAttackTime = 0;
                animationHandler.startAttack();
            }
        }

        // Обновление анимаций и атаки в середине анимации
        animationHandler.update(tpf, () -> {
            if (distance <= 40) {
                attackHandler.attack(player);
            }
        });
    }
}
