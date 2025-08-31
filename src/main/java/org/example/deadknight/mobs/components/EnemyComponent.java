package org.example.deadknight.mobs.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import lombok.Getter;
import org.example.deadknight.mobs.entities.GoblinEntity;

/**
 * Компонент, управляющий поведением врага (гоблина) в игре.
 * <p>
 * Основные функции:
 * <ul>
 *     <li>Следование за игроком с постоянной скоростью</li>
 *     <li>Переключение анимации ходьбы и атаки через {@link AnimationComponent}</li>
 *     <li>Атаку игрока делегирует {@link AttackComponent}</li>
 * </ul>
 *
 * <p>Используются внутренние флаги:
 * <ul>
 *     <li>{@code isWalking} — отслеживает, идет ли гоблин и запущена ли анимация ходьбы</li>
 *     <li>{@code isAttacking} — отслеживает, атакует ли гоблин и запущена ли анимация атаки</li>
 * </ul>
 *
 * <p>Игровая логика:
 * <ul>
 *     <li>Если игрок находится дальше 40 пикселей, гоблин движется к нему и проигрывается анимация ходьбы.</li>
 *     <li>Если игрок близко (≤40 пикселей), запускается анимация атаки и вызывается метод {@link AttackComponent#tryAttack(Entity, double)}.</li>
 * </ul>
 */
public class EnemyComponent extends Component {

    @Getter
    private final GoblinEntity goblinData;
    private AttackComponent attackComponent;
    private AnimationComponent animationComponent;
    private boolean isWalking = false;
    private boolean isAttacking = false;


    /**
     * Создает компонент для управления конкретным гоблином.
     *
     * @param data данные гоблина (скорость, урон, кадры анимации)
     */
    public EnemyComponent(GoblinEntity data) {
        this.goblinData = data;
    }

    /**
     * Вызывается при добавлении компонента к сущности.
     * <p>Инициализирует компоненты атаки и анимации, привязывает их к сущности.</p>
     */
    @Override
    public void onAdded() {
        // Инициализируем компонент атаки
        attackComponent = new AttackComponent(goblinData.getDamage(), 1.0);
        entity.addComponent(attackComponent);

        // Инициализируем компонент анимации с кадрами ходьбы и атаки
        animationComponent = new AnimationComponent(
                goblinData.getWalkFrames(),
                goblinData.getAttackFrames()
        );
        entity.addComponent(animationComponent);
    }

    /**
     * Основной метод обновления каждый кадр.
     * <p>Выбирает действие: движение к игроку или атака, переключает анимацию.</p>
     *
     * @param tpf время кадра (time per frame)
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

        double distance = player.getPosition().subtract(entity.getPosition()).magnitude();

        if (distance > 40) {
            moveTowardsPlayer(player, tpf);
        } else {
            handleAttack(player, tpf);
        }
    }

    /**
     * Двигает гоблина к игроку и включает анимацию ходьбы.
     *
     * @param player цель для движения
     * @param tpf время кадра
     */
    private void moveTowardsPlayer(Entity player, double tpf) {
        double effectiveSpeed = goblinData.getSpeed();
        Point2D direction = player.getPosition().subtract(entity.getPosition()).normalize();
        Point2D move = direction.multiply(effectiveSpeed * tpf);
        entity.translate(move);

        animationComponent.setScaleX(move.getX() >= 0 ? 1 : -1);

        if (!isWalking) {
            animationComponent.playWalk();
            isWalking = true;
        }

        // сбрасываем флаг атаки, если шли
        if (isAttacking) {
            isAttacking = false;
        }
    }


    /**
     * Обрабатывает атаку гоблина по игроку.
     *
     * @param player цель атаки
     * @param tpf время кадра
     */
    private void handleAttack(Entity player, double tpf) {
        if (!isAttacking) {
            animationComponent.playAttack();
            isAttacking = true;
        }
        attackComponent.tryAttack(player, tpf);
        isWalking = false;
    }
}
