package org.example.deadknight.mobs.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Point2D;

import java.util.List;


/**
 * Компонент для проигрывания анимации смерти сущности.
 * <p>
 * Создаёт новую сущность с анимацией и удаляет оригинального моба.
 * Направление анимации задаётся через конструктор.
 */
public class DeathAnimationComponent extends Component {

    private final List<Image> deathFrames;
    private final boolean facingRight;
    private static final double FRAME_TIME = 0.3;


    /**
     * Конструктор.
     *
     * @param deathFrames Список кадров анимации смерти.
     * @param facingRight Направление анимации (true — вправо, false — влево).
     */
    public DeathAnimationComponent(List<Image> deathFrames, boolean facingRight) {
        this.deathFrames = deathFrames;
        this.facingRight = facingRight;
    }

    /**
     * Инициализация компонента — создаёт новую сущность с анимацией и удаляет оригинал.
     */
    @Override
    public void onAdded() {
        if (deathFrames == null || deathFrames.isEmpty()) {
            return;
        }

        Point2D pos = getEntityPosition();
        removeOriginalEntity();
        var deathAnim = createDeathAnimationEntity(pos);
        attachAnimationComponent(deathAnim);
    }

    /**
     * Возвращает позицию текущей сущности.
     */
    private Point2D getEntityPosition() {
        return entity.getPosition();
    }

    /**
     * Удаляет оригинальную сущность из мира и очищает её view.
     */
    private void removeOriginalEntity() {
        entity.getViewComponent().clearChildren();
        entity.removeFromWorld();
    }

    /**
     * Создаёт новую сущность для проигрывания анимации.
     *
     * @param pos Позиция для новой сущности.
     * @return Сущность анимации смерти.
     */
    private Entity createDeathAnimationEntity(Point2D pos) {
        return FXGL.entityBuilder()
                .at(pos.getX(), pos.getY() + 15)
                .zIndex(100)
                .buildAndAttach();
    }

    /**
     * Добавляет компонент анимации к сущности.
     *
     * @param deathAnim Сущность для анимации.
     */
    private void attachAnimationComponent(Entity deathAnim) {
        deathAnim.addComponent(new AnimationRunner(List.copyOf(deathFrames), facingRight));
    }


    /**
     * Вложенный компонент, который проигрывает анимацию.
     */
    private static class AnimationRunner extends Component {
        private final List<Image> frames;
        private final boolean facingRight;
        private ImageView view;
        private int frame = 0;
        private double time = 0;

        /**
         * Конструктор.
         *
         * @param frames Список кадров анимации.
         * @param facingRight Направление анимации.
         */
        public AnimationRunner(List<Image> frames, boolean facingRight) {
            this.frames = frames;
            this.facingRight = facingRight;
        }

        /**
         * Инициализация — создаёт ImageView и добавляет его к сущности.
         */
        @Override
        public void onAdded() {
            createView();
        }

        /**
         * Обновление каждый кадр — меняет кадры анимации и удаляет сущность после конца.
         *
         * @param tpf Время кадра (time per frame).
         */
        @Override
        public void onUpdate(double tpf) {
            updateFrame(tpf);
        }

        /**
         * Создаёт ImageView с первым кадром и добавляет к сущности.
         */
        private void createView() {
            if (frames.isEmpty()) return;

            view = new ImageView(frames.get(0));
            view.setFitWidth(70);
            view.setFitHeight(70);
            view.setScaleX(facingRight ? 1 : -1);
            entity.getViewComponent().addChild(view);
        }

        /**
         * Обновляет текущий кадр анимации.
         * <p>
         * После завершения всех кадров удаляет сущность.
         *
         * @param tpf Время кадра (time per frame).
         */
        private void updateFrame(double tpf) {
            if (view == null) return;

            time += tpf;
            if (time >= FRAME_TIME && frame < frames.size()) {
                view.setImage(frames.get(frame));
                frame++;
                time = 0;
            }

            if (frame >= frames.size()) {
                entity.removeFromWorld();
            }
        }
    }
}
