package org.example.deadknight.gameplay.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.scene.CacheHint;
import javafx.scene.image.ImageView;
import lombok.Getter;
import org.example.deadknight.gameplay.actors.mobs.entities.GoblinEntity;

/**
 * Компонент для управления анимацией гоблина.
 * <p>
 * Поддерживает анимации ходьбы и атаки.
 * Отслеживает время между кадрами и переключает спрайты
 * с заданным временным интервалом.
 */
public class AnimationComponent extends Component {

    /** Данные о гоблине: текстуры, характеристики. */
    @Getter
    private final GoblinEntity goblinData;

    /** Визуальный элемент, на котором отображается текущий кадр. */
    private ImageView goblinView;

    /** Флаг, указывающий, выполняется ли сейчас анимация атаки. */
    @Getter
    private boolean attacking = false;

    /** Индекс текущего кадра анимации ходьбы. */
    private int walkIndex = 0;

    /** Индекс текущего кадра анимации атаки. */
    private int attackIndex = 0;

    /** Время, прошедшее с момента последнего кадра ходьбы. */
    private double walkElapsed = 0;

    /** Время, прошедшее с момента последнего кадра атаки. */
    private double attackElapsed = 0;

    /** Время отображения одного кадра ходьбы. */
    private static final double WALK_FRAME_TIME = 0.1;

    /** Время отображения одного кадра атаки. */
    private static final double ATTACK_FRAME_TIME = 0.04;

    /**
     * Создаёт компонент анимации для конкретного гоблина.
     *
     * @param goblinData данные о гоблине (спрайты и характеристики)
     */
    public AnimationComponent(GoblinEntity goblinData) {
        this.goblinData = goblinData;
    }

    /**
     * Вызывается при добавлении компонента к сущности.
     * Инициализирует {@link #goblinView} и настраивает кеширование для ускорения отрисовки.
     */
    @Override
    public void onAdded() {
        if (!entity.getViewComponent().getChildren().isEmpty()) {
            goblinView = (ImageView) entity.getViewComponent().getChildren().get(0);
            goblinView.setSmooth(true);
            goblinView.setCache(true);
            goblinView.setCacheHint(CacheHint.SPEED);
        }
    }

    /**
     * Вызывается каждый кадр игры для обновления состояния анимации сущности.
     * <p>
     * Если выполняется анимация атаки, делегирует обновление в {@link #updateAttackAnimation(double)}.
     * В противном случае обновляет анимацию ходьбы через {@link #updateWalkAnimation(double)}.
     *
     * @param tpf время кадра (time per frame), используется для расчёта прогресса анимации
     */
    @Override
    public void onUpdate(double tpf) {
        if (attacking) {
            updateAttackAnimation(tpf);
        } else {
            updateWalkAnimation(tpf);
        }
    }

    /**
     * Обновляет кадры анимации атаки.
     *
     * @param tpf время кадра (time per frame)
     */
    private void updateAttackAnimation(double tpf) {
        attackElapsed += tpf;
        if (attackElapsed >= ATTACK_FRAME_TIME) {
            if (attackIndex < goblinData.getAttackFrames().size()) {
                goblinView.setImage(goblinData.getAttackFrames().get(attackIndex));
                attackIndex++;
                attackElapsed = 0;
            } else {
                // Атака завершена — переключаемся на ходьбу
                attacking = false;
                walkIndex = 0;
                walkElapsed = 0;
            }
        }
    }

    /**
     * Обновляет кадры анимации ходьбы.
     *
     * @param tpf время кадра (time per frame)
     */
    private void updateWalkAnimation(double tpf) {
        walkElapsed += tpf;
        if (walkElapsed >= WALK_FRAME_TIME) {
            walkIndex = (walkIndex + 1) % goblinData.getWalkFrames().size();
            goblinView.setImage(goblinData.getWalkFrames().get(walkIndex));
            walkElapsed = 0;
        }
    }

    /**
     * Запускает анимацию атаки с начала.
     * Если атака уже выполняется — повторно не запускается.
     */
    public void playAttack() {
        if (attacking) return;
        attacking = true;
        attackIndex = 0;
        attackElapsed = 0;
    }

    /**
     * Отражает спрайт по горизонтали.
     *
     * @param scaleX масштаб по оси X (1 — нормальный, -1 — зеркальный)
     */
    public void setScaleX(double scaleX) {
        if (goblinView != null) goblinView.setScaleX(scaleX);
    }

    public double getScaleX() {
        if (goblinView != null)
            return goblinView.getScaleX(); // возвращаем значение
        return 1.0; // стандартное значение, если view нет
    }
}
