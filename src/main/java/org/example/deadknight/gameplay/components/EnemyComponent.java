package org.example.deadknight.gameplay.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import lombok.Getter;
import org.example.deadknight.gameplay.actors.mobs.entities.GoblinEntity;
import org.example.deadknight.gameplay.actors.mobs.service.CombatService;
import org.example.deadknight.gameplay.actors.mobs.service.DeathAnimationService;
import org.example.deadknight.gameplay.actors.mobs.service.MovementService;

/**
 * Компонент, управляющий поведением врага (гоблина) в игровом мире.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Проигрывание анимаций ходьбы и атаки через {@link AnimationComponent}</li>
 *     <li>Следование за игроком и взаимодействие с ним через {@link MovementService} и {@link CombatService}</li>
 *     <li>Запуск анимации смерти через {@link DeathAnimationService}</li>
 * </ul>
 * <p>
 * Основная логика атаки делегируется {@link AttackComponent}.
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

    /** Сервис проигрывания анимации смерти. */
    private DeathAnimationService deathAnimationService;

    /** Сервис перемещения сущности. */
    private MovementService movementService;

    /** Сервис атаки и проверки дистанции до игрока. */
    private CombatService combatService;

    /**
     * Конструктор.
     *
     * @param data данные гоблина
     */
    public EnemyComponent(GoblinEntity data) {
        this.goblinData = data;
    }

    /**
     * Инициализация компонентов и сервисов после добавления сущности в мир.
     */
    @Override
    public void onAdded() {
        initAnimationComponent();
        initAttackComponent();

        movementService = new MovementService(entity, animationComponent, goblinData.getSpeed());
        combatService = new CombatService(attackComponent, animationComponent);

        deathAnimationService = new DeathAnimationService(entity, goblinData, animationComponent);
    }

    /**
     * Обновление состояния сущности каждый кадр.
     * <p>
     * Сюда входит:
     * <ul>
     *     <li>Проверка смерти и запуск анимации через {@link DeathAnimationService}</li>
     *     <li>Поиск игрока в мире</li>
     *     <li>Движение к игроку через {@link MovementService}</li>
     *     <li>Атака игрока через {@link CombatService}</li>
     * </ul>
     *
     * @param tpf время кадра (time per frame)
     */
    @Override
    public void onUpdate(double tpf) {
        if (handleDeath()) return;

        // Находим игрока
        Entity player = FXGL.getGameWorld()
                .getEntities()
                .stream()
                .filter(e -> e.getProperties().exists("isPlayer") &&
                        e.getProperties().getBoolean("isPlayer"))
                .findFirst()
                .orElse(null);
        if (player == null) return;

        Point2D direction = player.getPosition().subtract(entity.getPosition());

        if (!combatService.isInRange(entity, player, 20)) {
            movementService.moveTowards(direction, tpf);
        } else {
            combatService.tryAttack(player, tpf);
        }
    }

    /**
     * Проверяет смерть сущности и запускает анимацию смерти, если она ещё не была проиграна.
     *
     * @return true, если гоблин умер и анимация проигрывается
     */
    private boolean handleDeath() {
        if (isDead() && !deathPlayed) {
            deathAnimationService.playDeathAnimation();
            deathPlayed = true;
            return true;
        }
        return deathPlayed;
    }

    /**
     * Проверяет, жив ли гоблин.
     *
     * @return true, если здоровье гоблина равно нулю
     */
    public boolean isDead () {
        HealthComponent health = entity.getComponent(HealthComponent.class);
        return health.isDead();
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


}