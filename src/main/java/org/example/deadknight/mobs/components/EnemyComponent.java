package org.example.deadknight.mobs.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import lombok.Getter;
import org.example.deadknight.components.HealthComponent;
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

    /**
     * Конструктор.
     *
     * @param data данные гоблина
     */
    public EnemyComponent(GoblinEntity data) {
        this.goblinData = data;
    }

    /**
     * Инициализация компонентов при добавлении сущности.
     * <p>
     * Создаёт или получает компоненты анимации и атаки.
     */
    @Override
    public void onAdded() {
        initAnimationComponent();
        initAttackComponent();
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

    /**
     * Проверяет смерть сущности и запускает анимацию смерти.
     *
     * @return true, если сущность умерла и анимация проигрывается
     */
    private boolean handleDeath() {
        if (isDead() && !deathPlayed) {
            playDeathAnimation();
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

    /**
     * Запускает анимацию смерти гоблина.
     * <p>
     * Создаёт новую сущность с компонентом {@link DeathAnimationComponent} и удаляет
     * текущую сущность из мира.
     */
    public void playDeathAnimation() {
        List<Image> frames = goblinData.getDeathFrames();
        if (frames == null || frames.isEmpty()) return;

        boolean facingRight = getFacingDirection();

        Entity deathAnim = createDeathAnimationEntity();

        attachDeathAnimationComponent(deathAnim, frames, facingRight);

        removeOriginalEntity();
    }

    /**
     * Определяет направление взгляда гоблина.
     *
     * @return true, если гоблин смотрит вправо; false — влево
     */
    private boolean getFacingDirection() {
        return animationComponent.getScaleX() > 0;
    }

    /**
     * Создаёт сущность для проигрывания анимации смерти.
     *
     * @return новая сущность для анимации
     */
    private Entity createDeathAnimationEntity() {
        return FXGL.entityBuilder()
                .at(entity.getX(), entity.getY())
                .zIndex(100)
                .buildAndAttach();
    }

    /**
     * Добавляет компонент анимации смерти к сущности.
     *
     * @param deathAnim   сущность анимации
     * @param frames      кадры анимации
     * @param facingRight направление взгляда
     */
    private void attachDeathAnimationComponent(Entity deathAnim, List<Image> frames, boolean facingRight) {
        deathAnim.addComponent(new DeathAnimationComponent(frames, facingRight));
    }

    /**
     * Удаляет оригинального гоблина из мира.
     */
    private void removeOriginalEntity() {
        entity.removeFromWorld();
    }
}
