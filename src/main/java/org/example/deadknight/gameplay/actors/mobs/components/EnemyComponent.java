package org.example.deadknight.gameplay.actors.mobs.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import lombok.Getter;
import lombok.Setter;
import org.example.deadknight.gameplay.actors.mobs.entities.GoblinEntity;
import org.example.deadknight.gameplay.actors.mobs.service.CombatService;
import org.example.deadknight.gameplay.actors.mobs.service.DeathAnimationService;
import org.example.deadknight.gameplay.actors.mobs.service.MovementService;
import org.example.deadknight.gameplay.components.HealthComponent;
import org.example.deadknight.gameplay.components.SpeedComponent;

/**
 * Компонент, управляющий поведением врага (например, {@link GoblinEntity}).
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>инициализацию базовых компонентов (анимация, атака, скорость)</li>
 *     <li>движение к игроку</li>
 *     <li>проверку дистанции и атаку</li>
 *     <li>обработку смерти и дропа</li>
 * </ul>
 * Вся логика делегируется сервисам:
 * {@link MovementService}, {@link CombatService}, {@link DeathAnimationService}.
 */
@Getter
@Setter
public class EnemyComponent extends Component {

    /** Дистанция, с которой враг может атаковать игрока */
    private static final double ATTACK_RANGE = 50.0;

    /** Время перезарядки атаки в секундах */
    private static final double ATTACK_COOLDOWN = 1.0;

    /** Интервал поиска игрока в секундах */
    private static final double PLAYER_SEARCH_INTERVAL = 1.0;

    /** Данные врага (скорость, урон, анимации и т.д.) */
    private final GoblinEntity goblinData;

    /** Управляет анимациями врага */
    private AnimationComponent animationComponent;

    /** Логика атаки врага */
    private AttackComponent attackComponent;

    /** Сервис проигрывания анимации смерти */
    private DeathAnimationService deathAnimationService;

    /** Сервис перемещения врага */
    private MovementService movementService;

    /** Сервис боевого поведения (атака, проверка дистанции) */
    private CombatService combatService;

    /** Ссылка на игрока (обновляется раз в {@link #PLAYER_SEARCH_INTERVAL}) */
    private Entity player;

    /** Таймер для поиска игрока */
    private double playerSearchTimer = 0;

    /** Кэшированный компонент здоровья */
    private HealthComponent healthComponent;

    /**
     * Создает компонент для управления поведением врага.
     *
     * @param data данные врага (параметры {@link GoblinEntity})
     */
    public EnemyComponent(GoblinEntity data) {
        this.goblinData = data;
    }

    /**
     * Вызывается при добавлении врага в игровой мир.
     * Инициализирует все компоненты, сервисы и кэширует часто используемые данные.
     */
    @Override
    public void onAdded() {
        initComponents();
        initServices();
        cacheCommonComponents();
        updatePlayer(); // первый поиск игрока
    }

    /**
     * Вызывается каждый кадр игры. Обновляет поведение врага:
     * <ul>
     *     <li>обрабатывает смерть</li>
     *     <li>обновляет игрока с интервалом</li>
     *     <li>выполняет движение или атаку</li>
     * </ul>
     *
     * @param tpf время на кадр (time per frame)
     */
    @Override
    public void onUpdate(double tpf) {
        if (handleDeath()) return;

        updatePlayerIfNeeded(tpf);
        if (player == null) return;

        handleCombatAndMovement(tpf);
    }

    // --- Вспомогательные методы ---

    /** Инициализирует вспомогательные компоненты врага (анимацию, атаку, скорость). */
    private void initComponents() {
        initAnimationComponent();
        initAttackComponent();

        if (!entity.hasComponent(SpeedComponent.class)) {
            entity.addComponent(new SpeedComponent(goblinData.getSpeed()));
        }
    }

    /** Инициализирует сервисы поведения врага. */
    private void initServices() {
        double actualSpeed = entity.getComponent(SpeedComponent.class).getSpeed();
        movementService = new MovementService(entity, actualSpeed);
        combatService = new CombatService(attackComponent, animationComponent);
        deathAnimationService = new DeathAnimationService(entity, goblinData, animationComponent);
    }

    /** Кэширует часто используемые компоненты (например, здоровье). */
    private void cacheCommonComponents() {
        healthComponent = entity.getComponent(HealthComponent.class);
    }

    /**
     * Проверяет, нужно ли обновить игрока.
     * Поиск игрока выполняется только с интервалом {@link #PLAYER_SEARCH_INTERVAL}.
     */
    private void updatePlayerIfNeeded(double tpf) {
        playerSearchTimer += tpf;
        if (player == null || playerSearchTimer >= PLAYER_SEARCH_INTERVAL) {
            updatePlayer();
            playerSearchTimer = 0;
        }
    }

    /**
     * Выполняет движение или атаку в зависимости от дистанции до игрока.
     *
     * @param tpf время на кадр
     */
    private void handleCombatAndMovement(double tpf) {
        if (!combatService.isInRange(entity, player, ATTACK_RANGE)) {
            movementService.moveToTarget(player, tpf);
        } else {
            combatService.tryAttack(player, tpf);
        }
    }

    /**
     * Обрабатывает смерть врага: проигрывает анимацию и выбрасывает дроп.
     *
     * @return true, если враг мертв и анимация смерти уже запущена
     */
    private boolean handleDeath() {
        if (healthComponent.isDead() && !deathAnimationService.isDeathPlayed()) {
            entity.getComponentOptional(DropComponent.class)
                    .ifPresent(drop -> drop.dropLoot(entity.getPosition()));

            deathAnimationService.playDeathAnimation();
            return true;
        }
        return deathAnimationService.isDeathPlayed();
    }

    /** Инициализирует компонент анимации. */
    private void initAnimationComponent() {
        if (entity.hasComponent(AnimationComponent.class)) {
            animationComponent = entity.getComponent(AnimationComponent.class);
        } else {
            animationComponent = new AnimationComponent(goblinData);
            entity.addComponent(animationComponent);
        }
    }

    /** Инициализирует компонент атаки. */
    private void initAttackComponent() {
        if (entity.hasComponent(AttackComponent.class)) {
            attackComponent = entity.getComponent(AttackComponent.class);
        } else {
            attackComponent = new AttackComponent(goblinData.getDamage(), ATTACK_COOLDOWN);
            entity.addComponent(attackComponent);
        }
    }

    /** Выполняет поиск игрока в игровом мире. */
    private void updatePlayer() {
        player = FXGL.getGameWorld()
                .getEntities()
                .stream()
                .filter(e -> e.getProperties().exists("isPlayer") && e.getProperties().getBoolean("isPlayer"))
                .findFirst()
                .orElse(null);
    }
}
