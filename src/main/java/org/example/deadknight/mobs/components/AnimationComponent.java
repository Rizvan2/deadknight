package org.example.deadknight.mobs.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.animation.AnimationTimer;
import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Компонент, управляющий покадровой анимацией моба.
 * <p>
 * Поддерживает:
 * <ul>
 *     <li>Анимацию ходьбы</li>
 *     <li>Анимацию атаки</li>
 *     <li>Изменение направления спрайта по горизонтали</li>
 * </ul>
 * <p>
 * Логика обновления кадров реализована через {@link AnimationTimer}.
 */
@Getter
@Setter
public class AnimationComponent extends Component {

    /**
     * Анимация ходьбы моба.
     */
    private final FrameAnimation walkAnim;

    /**
     * Анимация атаки моба.
     */
    private final FrameAnimation attackAnim;

    /**
     * ImageView, используемый для отображения текущего кадра спрайта.
     */
    private ImageView view;

    /**
     * Флаг, указывающий, выполняется ли анимация атаки.
     */
    private boolean attacking = false;

    /**
     * Накопитель времени для кадров ходьбы.
     */
    private double walkElapsed = 0;

    /**
     * Накопитель времени для кадров атаки.
     */
    private double attackElapsed = 0;

    /**
     * Создает компонент анимации для моба с указанными кадрами ходьбы и атаки.
     *
     * @param walkFrames   список кадров анимации ходьбы
     * @param attackFrames список кадров анимации атаки
     */
    public AnimationComponent(List<Image> walkFrames, List<Image> attackFrames) {
        this.walkAnim = new FrameAnimation(walkFrames, 0.1);
        this.attackAnim = new FrameAnimation(attackFrames, 0.04);
    }

    /**
     * Вызывается при добавлении компонента к сущности.
     * <p>Инициализирует {@link ImageView} и запускает {@link AnimationTimer} для обновления кадров.</p>
     */
    @Override
    public void onAdded() {
        view = (ImageView) entity.getViewComponent().getChildren().get(0);
        view.setSmooth(true);
        view.setCache(true);
        view.setCacheHint(CacheHint.SPEED);

        startTimer();
    }

    /**
     * Запускает таймер обновления кадров анимации.
     */
    private void startTimer() {
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
                updateAnimation(tpf);
            }
        };
        timer.start();
    }

    /**
     * Обновляет текущий кадр анимации в зависимости от состояния {@link #attacking}.
     *
     * @param tpf время с последнего кадра (секунды)
     */
    private void updateAnimation(double tpf) {
        if (attacking) {
            attackElapsed += tpf;
            if (attackElapsed >= 0.04) {
                view.setImage(attackAnim.update(attackElapsed));
                attackElapsed = 0;
                if (attackAnim.isFinished()) {
                    attacking = false;
                    attackAnim.reset();
                }
            }
        } else {
            walkElapsed += tpf;
            if (walkElapsed >= 0.1) {
                view.setImage(walkAnim.update(walkElapsed));
                walkElapsed = 0;
            }
        }
    }

    /**
     * Запускает анимацию атаки.
     * <p>Сбрасывает состояние {@link FrameAnimation} для атаки и устанавливает флаг {@link #attacking}.</p>
     */
    public void playAttack() {
        attacking = true;
        attackAnim.reset();
    }

    /**
     * Возвращает анимацию к ходьбе.
     * <p>Сбрасывает состояние {@link FrameAnimation} для ходьбы и сбрасывает флаг {@link #attacking}.</p>
     */
    public void playWalk() {
        attacking = false;
        walkAnim.reset();
    }

    /**
     * Меняет горизонтальное направление спрайта.
     *
     * @param scaleX 1 для обычного направления, -1 для зеркального
     */
    public void setScaleX(double scaleX) {
        if (view != null) view.setScaleX(scaleX);
    }
}
