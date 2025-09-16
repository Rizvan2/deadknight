package org.example.deadknight.gameplay.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.image.ImageView;
import javafx.geometry.Point2D;

/**
 * Компонент для проигрывания анимации смерти сущности.
 * <p>
 * Логика работы:
 * <ul>
 *   <li>При добавлении к сущности — фиксирует её позицию.</li>
 *   <li>Удаляет оригинальную сущность (например, моба).</li>
 *   <li>Создаёт временную сущность, которая проигрывает кадры анимации смерти.</li>
 *   <li>После завершения анимации временная сущность удаляется.</li>
 * </ul>
 *
 * Таким образом, данный компонент отвечает только за визуализацию смерти,
 * не затрагивая игровую механику (лут, опыт, статистику).
 */
public class DeathAnimationComponent extends Component {

    /** Кадры анимации смерти */
    private final ImageView[] deathFrames;

    /** Направление отображения (true — вправо, false — влево) */
    private final boolean facingRight;

    /**
     * Конструктор.
     * Создаёт компонент анимации смерти.
     *
     * @param deathFrames массив кадров анимации
     * @param facingRight направление (true — вправо, false — влево)
     */
    public DeathAnimationComponent(ImageView[] deathFrames, boolean facingRight) {
        this.deathFrames = deathFrames;
        this.facingRight = facingRight;
    }

    /**
     * Метод вызывается при добавлении компонента к сущности.
     * <p>
     * Если кадры заданы:
     * <ul>
     *   <li>Определяет текущую позицию сущности;</li>
     *   <li>Удаляет оригинал;</li>
     *   <li>Создаёт новую сущность для анимации;</li>
     *   <li>Прикрепляет к ней проигрыватель анимации.</li>
     * </ul>
     */
    @Override
    public void onAdded() {
        if (deathFrames == null || deathFrames.length == 0) {
            return;
        }

        Point2D pos = getEntityPosition();

        removeOriginalEntity();
        var deathAnim = createDeathAnimationEntity(pos);
        attachAnimationComponent(deathAnim);
    }


    /**
     * Возвращает текущую позицию сущности.
     *
     * @return позиция сущности
     */
    private Point2D getEntityPosition() {
        return entity.getPosition();
    }

    /**
     * Удаляет оригинальную сущность из мира:
     * <ul>
     *   <li>Очищает список визуальных компонентов;</li>
     *   <li>Удаляет саму сущность из игрового мира.</li>
     * </ul>
     */
    private void removeOriginalEntity() {
        entity.getViewComponent().clearChildren();
        entity.removeFromWorld();
    }


    /**
     * Создаёт временную сущность для отображения анимации смерти.
     *
     * @param pos позиция, в которой должна появиться анимация
     * @return новая сущность с анимацией
     */
    private Entity createDeathAnimationEntity(Point2D pos) {
        return FXGL.entityBuilder()
                .at(pos.getX(), pos.getY() + 15)
                .zIndex(100)
                .buildAndAttach();
    }

    /**
     * Прикрепляет к временной сущности компонент,
     * отвечающий за проигрывание анимации.
     *
     * @param deathAnim сущность для анимации
     */
    private void attachAnimationComponent(Entity deathAnim) {
        deathAnim.addComponent(new AnimationRunner(deathFrames, facingRight));
    }


    /**
     * Вложенный компонент, который последовательно проигрывает кадры анимации.
     * <p>
     * После показа последнего кадра сущность автоматически удаляется.
     */
    private static class AnimationRunner extends Component {

        /** Кадры анимации */
        private final ImageView[] frames; // берем массив кадров для анимации

        /** Направление отображения */
        private final boolean facingRight;

        /** Текущий визуальный элемент */
        private ImageView view;

        /** Индекс текущего кадра */
        private int frame = 0;

        /** Накопленное время между кадрами */
        private double time = 0;

        /** Длительность отображения одного кадра (в секундах) */
        private static final double FRAME_TIME = 0.3;

        /**
         * Создаёт компонент проигрывателя анимации.
         *
         * @param frames массив кадров анимации
         * @param facingRight направление (true — вправо, false — влево)
         */
        public AnimationRunner(ImageView[] frames, boolean facingRight) {
            this.frames = frames;
            this.facingRight = facingRight;
        }

        /**
         * Вызывается при добавлении компонента к сущности.
         * Создаёт первый кадр и добавляет его во View.
         */
        @Override
        public void onAdded() {
            createView();
        }

        /**
         * Обновляет состояние анимации каждый кадр игрового цикла.
         *
         * @param tpf время с последнего кадра (time per frame)
         */
        @Override
        public void onUpdate(double tpf) {
            updateFrame(tpf);
        }

        /**
         * Создаёт первый кадр анимации и добавляет его в сущность.
         */
        private void createView() {
            if (frames.length == 0) return;

            view = new ImageView(frames[0].getImage());
            view.setFitWidth(100);
            view.setFitHeight(100);
            view.setScaleX(facingRight ? 1 : -1);
            entity.getViewComponent().addChild(view);
        }

        /**
         * Обновляет кадр анимации.
         * <ul>
         *   <li>Накапливает время;</li>
         *   <li>По истечении FRAME_TIME переключает кадр;</li>
         *   <li>После показа последнего кадра удаляет сущность из мира.</li>
         * </ul>
         *
         * @param tpf время с последнего кадра (time per frame)
         */
        private void updateFrame(double tpf) {
            if (view == null) return;

            time += tpf;
            if (time >= FRAME_TIME && frame < frames.length) {
                view.setImage(frames[frame].getImage());
                frame++;
                time = 0;
            }

            if (frame >= frames.length) {
                entity.removeFromWorld();
            }
        }
    }
}

