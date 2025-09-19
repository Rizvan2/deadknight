package org.example.deadknight.gameplay.actors.mobs.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.scene.CacheHint;
import javafx.scene.image.ImageView;
import lombok.Getter;
import org.example.deadknight.gameplay.actors.mobs.entities.GoblinEntity;

/**
 * Компонент анимации для сущности гоблина.
 * <p>
 * Отвечает за воспроизведение анимаций ходьбы и атаки,
 * а также за смену направления взгляда сущности.
 */
public class AnimationComponent extends Component {

    /** Данные гоблина, содержащие его анимации */
    @Getter
    private final GoblinEntity goblinData;

    /** Флаг, указывающий, что гоблин сейчас атакует */
    @Getter
    private boolean attacking = false;

    /** Индекс текущего кадра ходьбы */
    private int walkIndex = 0;

    /** Индекс текущего кадра атаки */
    private int attackIndex = 0;

    /** Прошедшее время для анимации ходьбы */
    private double walkElapsed = 0;

    /** Прошедшее время для анимации атаки */
    private double attackElapsed = 0;

    /** Время между кадрами ходьбы */
    private static final double WALK_FRAME_TIME = 0.1;

    /** Время между кадрами атаки */
    private static final double ATTACK_FRAME_TIME = 0.04;

    /** Направление взгляда гоблина (true — вправо, false — влево) */
    private boolean facingRight = true;

    /** Массив кадров анимации ходьбы вправо */
    private final ImageView[] walkRight;

    /** Массив кадров анимации ходьбы влево */
    private final ImageView[] walkLeft;

    /** Массив кадров анимации атаки вправо */
    private final ImageView[] attackRight;

    /** Массив кадров анимации атаки влево */
    private final ImageView[] attackLeft;

    /** Текущий отображаемый кадр */
    private ImageView currentSprite;

    /**
     * Создаёт компонент анимации для гоблина.
     *
     * @param goblinData объект сущности гоблина с его анимациями
     */
    public AnimationComponent(GoblinEntity goblinData) {
        this.goblinData = goblinData;

        this.walkRight = goblinData.getWalkRight();
        this.attackRight = goblinData.getAttackRight();

        this.walkLeft = goblinData.getWalkLeft();
        this.attackLeft = goblinData.getAttackLeft();
    }

    /**
     * Инициализация компонента после добавления к сущности.
     * Создаётся ImageView для отображения текущего кадра анимации.
     */
    @Override
    public void onAdded() {
        currentSprite = new ImageView(walkRight[0].getImage());
        currentSprite.setFitWidth(goblinData.getWalkRight()[0].getFitWidth());
        currentSprite.setFitHeight(goblinData.getWalkRight()[0].getFitHeight());
        currentSprite.setSmooth(true);
        currentSprite.setCache(true);
        currentSprite.setCacheHint(CacheHint.SPEED);

        entity.getViewComponent().clearChildren();
        entity.getViewComponent().addChild(currentSprite);
    }

    /**
     * Устанавливает текущий кадр анимации.
     *
     * @param frame кадр, который нужно отобразить
     */
    private void setFrame(ImageView frame) {
        currentSprite.setImage(frame.getImage());
        currentSprite.setFitWidth(frame.getFitWidth());
        currentSprite.setFitHeight(frame.getFitHeight());
        currentSprite.setScaleX(frame.getScaleX());
    }

    /**
     * Меняет направление взгляда гоблина.
     *
     * @param facingRight true, если гоблин смотрит вправо; false — влево
     */
    public void setFacingRight(boolean facingRight) {
        if (this.facingRight != facingRight) {
            this.facingRight = facingRight;

            // Смена направления сбрасывает индексы и время анимаций
            entity.getViewComponent().removeChild(currentSprite);
            currentSprite = facingRight ? walkRight[0] : walkLeft[0];
            entity.getViewComponent().addChild(currentSprite);

            walkIndex = 0;
            attackIndex = 0;
            walkElapsed = 0;
            attackElapsed = 0;
        }
    }

    /**
     * Обновление анимации каждый кадр.
     *
     * @param tpf время с последнего обновления (time per frame)
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
     * Обновляет анимацию ходьбы.
     *
     * @param tpf время с последнего обновления
     */
    private void updateWalkAnimation(double tpf) {
        walkElapsed += tpf;
        if (walkElapsed >= WALK_FRAME_TIME) {
            ImageView[] frames = facingRight ? walkRight : walkLeft;
            setFrame(frames[walkIndex]);
            walkIndex = (walkIndex + 1) % frames.length;
            walkElapsed = 0;
        }
    }

    /**
     * Обновляет анимацию атаки.
     *
     * @param tpf время с последнего обновления
     */
    private void updateAttackAnimation(double tpf) {
        attackElapsed += tpf;
        if (attackElapsed >= ATTACK_FRAME_TIME) {
            ImageView[] frames = facingRight ? attackRight : attackLeft;

            if (attackIndex < frames.length) {
                setFrame(frames[attackIndex]);
                attackIndex++;
                attackElapsed = 0;
            } else {
                attacking = false;
                attackIndex = 0;
                walkElapsed = 0;

                setFrame(facingRight ? walkRight[walkIndex] : walkLeft[walkIndex]);
            }
        }
    }

    /**
     * Запускает анимацию атаки.
     * Если гоблин уже атакует, вызов игнорируется.
     */
    public void playAttack() {
        if (attacking) return;
        attacking = true;
        attackIndex = 0;
        attackElapsed = 0;
    }

    /**
     * Проверяет направление взгляда гоблина.
     *
     * @return true, если гоблин смотрит вправо; false — влево
     */
    public boolean isFacingRight() {
        return facingRight;
    }
}
