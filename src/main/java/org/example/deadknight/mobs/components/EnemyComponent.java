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
import org.example.deadknight.components.HealthComponent;
import org.example.deadknight.mobs.entities.GoblinEntity;


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
    private boolean deathPlayed = false;

    private AnimationComponent animationComponent;
    private AttackComponent attackComponent;

    // флаг для отслеживания состояния, чтобы не вызывать playWalk() / playAttack() каждый кадр
    private boolean wasWalking = true;

    public EnemyComponent(GoblinEntity data) {
        this.goblinData = data;
    }

    @Override
    public void onAdded() {
        // Попробуем получить уже добавленные компоненты (если фабрика их добавила)
        if (entity.hasComponent(AnimationComponent.class)) {
            animationComponent = entity.getComponent(AnimationComponent.class);
        } else {
            animationComponent = new AnimationComponent(goblinData);
            entity.addComponent(animationComponent);
        }

        if (entity.hasComponent(AttackComponent.class)) {
            attackComponent = entity.getComponent(AttackComponent.class);
        } else {
            attackComponent = new AttackComponent(goblinData.getDamage(), 1.0);
            entity.addComponent(attackComponent);
        }
    }

    @Override
    public void onUpdate(double tpf) {
        if (isDead() && !deathPlayed) {
            playDeathAnimation();
            deathPlayed = true; // анимация один раз
            return; // дальше движение/атака не выполняется
        }
        if (deathPlayed) return;

        Entity player = FXGL.getGameWorld()
                .getEntities()
                .stream()
                .filter(e -> e.getProperties().exists("isPlayer") && e.getProperties().getBoolean("isPlayer"))
                .findFirst()
                .orElse(null);

        if (player == null) return;


        double effectiveSpeed = goblinData.getSpeed();

        Point2D direction = player.getPosition().subtract(entity.getPosition());
        double distance = direction.magnitude();

        boolean inAttackRange = distance <= 40;

        if (!inAttackRange) {
            // ходим — вызывать playWalk только при переходе в состояние ходьбы
            if (!wasWalking) {
                animationComponent.playWalk();
                wasWalking = true;
            }

            Point2D move = direction.normalize().multiply(effectiveSpeed * tpf);
            entity.translate(move);
            animationComponent.setScaleX(move.getX() >= 0 ? 1 : -1);

        } else {
            // атакуем — вызывать playAttack только при переходе в состояние атаки
            if (wasWalking) {
                animationComponent.playAttack();
                wasWalking = false;
            }

            // tryAttack — пусть сам управляет кулдауном и возвращает true если атака запущена (по желанию)
            attackComponent.tryAttack(player, tpf);
        }
    }

    public boolean isDead() {
        HealthComponent health = entity.getComponent(HealthComponent.class);
        return health.isDead();
    }

    public void playDeathAnimation() {
        var frames = goblinData.getDeathFrames();
        if (frames == null || frames.isEmpty()) return;

        FXGL.entityBuilder()
                .at(entity.getX(), entity.getY())
                .view(new ImageView(frames.get(0))) // новый ImageView
                .with(new DeathAnimationComponent(frames))
                .buildAndAttach();

        entity.removeFromWorld(); // гоблин удаляется сразу, но анимация привязана к новой сущности
    }


}
