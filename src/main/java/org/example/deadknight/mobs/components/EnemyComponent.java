package org.example.deadknight.mobs.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import lombok.Getter;
import org.example.deadknight.mobs.entities.GoblinEntity;


/**
 * Компонент логики врага (гоблина).
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Движение гоблина в сторону игрока</li>
 *     <li>Атаку игрока</li>
 *     <li>Взаимодействие с компонентом анимации {@link AnimationComponent} и компонентом атаки {@link AttackComponent}</li>
 * </ul>
 * Анимацию полностью делегирует {@link AnimationComponent}.
 */
public class EnemyComponent extends Component {

    /**
     * Данные гоблина (скорость, урон, кадры анимации и т.д.)
     */
    @Getter
    private final GoblinEntity goblinData;

    /**
     * Компонент анимации гоблина
     */
    private AnimationComponent animation;

    /**
     * Компонент атаки гоблина
     */
    private AttackComponent attackComponent;

    /**
     * Создает компонент логики врага с заданными данными.
     *
     * @param data данные гоблина
     */
    public EnemyComponent(GoblinEntity data) {
        this.goblinData = data;
    }

    /**
     * Вызывается после добавления компонента к сущности.
     * <p>
     * Инициализирует компоненты анимации и атаки и добавляет их к сущности.
     */
    @Override
    public void onAdded() {
        // Компонент анимации (таймер внутри него управляет кадрами)
        animation = new AnimationComponent(goblinData.getWalkFrames(), goblinData.getAttackFrames());
        entity.addComponent(animation);

        // Компонент атаки
        attackComponent = new AttackComponent(goblinData.getDamage(), 1.0);
        entity.addComponent(attackComponent);
    }

    /**
     * Обновление логики врага каждый кадр.
     * <p>
     * В зависимости от расстояния до игрока:
     * <ul>
     *     <li>Если игрок далеко — гоблин движется к нему</li>
     *     <li>Если игрок близко — гоблин атакует</li>
     * </ul>
     *
     * @param tpf время прошедшее с последнего кадра (time per frame)
     */
    @Override
    public void onUpdate(double tpf) {
        Entity player = FXGL.getGameWorld()
                .getEntities()
                .stream()
                .filter(e -> e.getProperties().exists("isPlayer") && e.getProperties().getBoolean("isPlayer"))
                .findFirst()
                .orElse(null);

        if (player == null) return;

        Point2D direction = player.getPosition().subtract(entity.getPosition());
        double distance = direction.magnitude();

        if (distance > 40) {
            moveTowardsPlayer(direction, tpf);
        } else {
            attackPlayer(player, tpf);
        }
    }

    /**
     * Двигает гоблина в сторону игрока и запускает анимацию ходьбы.
     *
     * @param direction вектор направления к игроку
     * @param tpf время прошедшее с последнего кадра
     */
    private void moveTowardsPlayer(Point2D direction, double tpf) {
        Point2D move = direction.normalize().multiply(goblinData.getSpeed() * tpf);
        entity.translate(move);

        animation.setScaleX(move.getX() >= 0 ? 1 : -1);

        if (!animation.isAttacking()) {
            animation.playWalk();
        }
    }

    /**
     * Атакует игрока и запускает анимацию атаки.
     *
     * @param player сущность игрока
     * @param tpf время прошедшее с последнего кадра
     */
    private void attackPlayer(Entity player, double tpf) {
        if (!animation.isAttacking()) {
            animation.playAttack();
        }
        attackComponent.tryAttack(player, tpf);
    }

}
