package org.example.deadknight.mobs.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import lombok.Getter;
import org.example.deadknight.components.HealthComponent;
import org.example.deadknight.mobs.entities.GoblinEntity;
import org.example.deadknight.mobs.service.DeathAnimationService;

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
 * Атаку делегирует {@link AttackComponent}.
 */
public class EnemyComponent extends Component {

    /** Данные гоблина, включая кадры анимации, скорость и урон. */
    @Getter
    private final GoblinEntity goblinData;

    /** Флаг, показывающий, была ли проиграна анимация смерти. */
    private boolean deathPlayed = false;

    /** Компонент анимации для управления кадрами ходьбы и атаки. */
    private AnimationComponent animationComponent;

    /** Компонент атаки, управляющий логикой атак. */
    private AttackComponent attackComponent;

    /** Флаг состояния ходьбы, чтобы не вызывать анимацию каждый кадр. */
    private boolean wasWalking = true;

    private DeathAnimationService deathAnimationService;


    /**
     * Конструктор.
     *
     * @param data данные гоблина
     */
    public EnemyComponent(GoblinEntity data) {
        this.goblinData = data;
    }

    @Override
    public void onAdded() {
        initAnimationComponent();
        initAttackComponent();

        // создаём сервис после того, как есть animationComponent
        deathAnimationService = new DeathAnimationService(entity, goblinData, animationComponent);
    }

    /**
     * Инициализирует компонент анимации.
     * <p>
     * Если компонент уже добавлен, получает его из сущности, иначе создаёт новый.
     */
    private void initAnimationComponent() {
        if (entity.hasComponent(AnimationComponent.class)) {
            animationComponent = entity.getComponent(AnimationComponent.class);
        } else {
            animationComponent = new AnimationComponent(goblinData);
            entity.addComponent(animationComponent);
        }
    }

    /**
     * Инициализирует компонент атаки.
     * <p>
     * Если компонент уже добавлен, получает его из сущности, иначе создаёт новый.
     */
    private void initAttackComponent() {
        if (entity.hasComponent(AttackComponent.class)) {
            attackComponent = entity.getComponent(AttackComponent.class);
        } else {
            attackComponent = new AttackComponent(goblinData.getDamage(), 1.0);
            entity.addComponent(attackComponent);
        }
    }

    /**
     * Обновление состояния сущности каждый кадр.
     * <p>
     * Проверяет смерть, движение к игроку или атаку.
     *
     * @param tpf время кадра (time per frame)
     */
    @Override
    public void onUpdate(double tpf) {
        if (handleDeath()) return;

        Entity player = findPlayer();
        if (player == null) return;

        Point2D direction = player.getPosition().subtract(entity.getPosition());
        double distance = direction.magnitude();

        if (!isInAttackRange(distance)) {
            moveTowardsPlayer(direction, tpf);
        } else {
            attackPlayer(player, tpf);
        }
    }

    private boolean handleDeath() {
        if (isDead() && !deathPlayed) {
            deathAnimationService.playDeathAnimation(); // вызываем сервис
            deathPlayed = true;
            return true;
        }
        return deathPlayed;
    }

    /**
     * Находит игрока в мире.
     *
     * @return сущность игрока или null, если игрок не найден
     */
    private Entity findPlayer() {
        return FXGL.getGameWorld()
                .getEntities()
                .stream()
                .filter(e -> e.getProperties().exists("isPlayer") &&
                        e.getProperties().getBoolean("isPlayer"))
                .findFirst()
                .orElse(null);
    }

    /**
     * Проверяет, находится ли игрок в радиусе атаки.
     *
     * @param distance расстояние до игрока
     * @return true, если игрок в радиусе атаки
     */
    private boolean isInAttackRange(double distance) {
        return distance <= 40;
    }

    /**
     * Перемещает гоблина к игроку и обновляет анимацию ходьбы.
     *
     * @param direction вектор движения к игроку
     * @param tpf       время кадра
     */
    private void moveTowardsPlayer(Point2D direction, double tpf) {
        if (!wasWalking) {
            animationComponent.playWalk();
            wasWalking = true;
        }

        Point2D move = direction.normalize().multiply(goblinData.getSpeed() * tpf);
        entity.translate(move);
        animationComponent.setScaleX(move.getX() >= 0 ? 1 : -1);
    }

    /**
     * Атакует игрока и обновляет анимацию атаки.
     *
     * @param player сущность игрока
     * @param tpf    время кадра
     */
    private void attackPlayer(Entity player, double tpf) {
        if (wasWalking) {
            animationComponent.playAttack();
            wasWalking = false;
        }
        attackComponent.tryAttack(player, tpf);
    }

    /**
     * Проверяет, жив ли гоблин.
     *
     * @return true, если здоровье гоблина равно нулю
     */
    public boolean isDead() {
        HealthComponent health = entity.getComponent(HealthComponent.class);
        return health.isDead();
    }
}
